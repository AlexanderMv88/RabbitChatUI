package org.ChatUI.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import org.ChatUI.BasicForm;
import org.ChatUI.entity.Employee;
import org.ChatUI.entity.Msg;
import org.ChatUI.mq.RabbitMqPublisher;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.IOException;
import java.util.List;

import static com.vaadin.ui.UI.getCurrent;
import static org.ChatUI.mq.RabbitEmployee.EMPLOYEE_SELECT_EVENT;
import static org.ChatUI.mq.RabbitEmployee.TO_SERVICE_EMPLOYEE_FANOUT_EXCHANGE;
import static org.ChatUI.mq.RabbitMqPublisher.createMessage;
import static org.ChatUI.mq.RabbitMsg.MSG_SELECT_EVENT;
import static org.ChatUI.mq.RabbitMsg.TO_SERVICE_MSG_FANOUT_EXCHANGE;

public class MainMenuForm extends BasicForm {


    public TextArea chatTArea = new TextArea("Сообщения");
    private TextArea messageTArea = new TextArea("Ваше сообщение");
    private Button sendBtn = new Button("Отправить", e->{
        if (!messageTArea.getValue().trim().isEmpty()) {
            RabbitTemplate rabbitTemplate = ((NavigatorUI) getCurrent()).getRabbitTemplate();
            try {
                new RabbitMqPublisher().sendCreateMsgMessage(rabbitTemplate, new Msg(messageTArea.getValue(), user));
                messageTArea.clear();
            } catch (JsonProcessingException e1) {
                e1.printStackTrace();
            }
        }
    });



    public MainMenuForm() {
        chatTArea.setSizeFull();

        HorizontalLayout yourMsgHLayout = new HorizontalLayout(messageTArea, sendBtn);
        messageTArea.setSizeFull();
        //sendBtn.setSizeFull();
        yourMsgHLayout.setSizeFull();

        yourMsgHLayout.setExpandRatio(messageTArea,0.8f);
        yourMsgHLayout.setExpandRatio(sendBtn,0.2f);

        VerticalLayout vlayout = new VerticalLayout(new Label("Главное меню"), chatTArea, yourMsgHLayout);
        vlayout.setSizeFull();
        vlayout.setExpandRatio(chatTArea,0.8f);
        vlayout.setExpandRatio(yourMsgHLayout,0.2f);
        vlayout.setId("vLayout");
        vlayout.setMargin(new MarginInfo(true, false,false,false));

        vLayout.addComponent(vlayout);
        vLayout.setSizeFull();
        vLayout.setExpandRatio(vlayout,1f);


    }

    private void getAllMsgToChat() throws IOException {
        RabbitTemplate rabbitTemplate = ((NavigatorUI) getCurrent()).getRabbitTemplate();
        rabbitTemplate.setExchange(TO_SERVICE_MSG_FANOUT_EXCHANGE);
        Message messageRequest = createMessage(MSG_SELECT_EVENT, "");
        Message message = rabbitTemplate.sendAndReceive(messageRequest);
        String jsonEntities = new String(message.getBody());
        //String jsonEntities = (String) rabbitTemplate.convertSendAndReceive("", "getAll");


        ObjectMapper mapper = new ObjectMapper();
        CollectionType javaType = mapper.getTypeFactory()
                .constructCollectionType(List.class, Msg.class);

        List<Msg> msgs = mapper.readValue(jsonEntities, javaType);
        /*List<Employee> chatUsers = chatUsersResponse.getBody();*/
        for(Msg msg:msgs){
            chatTArea.setValue(chatTArea.getValue() + "\n" + msg.getSender()+": "+msg.getText());
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        if (checkCurrentUser()){
            try {
                getAllMsgToChat();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
