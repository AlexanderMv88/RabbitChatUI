package org.ChatUI.ui;

import com.vaadin.navigator.View;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;
import org.ChatUI.entity.Employee;

import java.io.IOException;

public class LoginForm extends VerticalLayout implements View {


    public ComboBox<Employee> userCBox = new ComboBox<Employee>("Пользователь:");
    //private List<Employee> employees = new ArrayList<Employee>();
    private Button okBtn = new Button("Ok", e->{
        if (checkUserData()) {
            ((NavigatorUI) getUI()).getNavigator().navigateTo(((NavigatorUI) getUI()).MAIN_MENU_FORM);
        } else {
            new Notification("Ошибка",
                    "Неверно введены имя пользователя или пароль",
                    Notification.Type.ERROR_MESSAGE, true)
                    .show(Page.getCurrent());
        }
    });
    private Button createEmployeeBtn = new Button("Создать", e->{
        EmployeeWindow employeeWindow = new EmployeeWindow();
        getUI().addWindow(employeeWindow);
    });




    public LoginForm() {
        try {
            getAllEmployeeToCBox();
            setWidth("100%");
            setSpacing(true);
            //TODO
            Label label = new Label("Вход");


            HorizontalLayout horizontalLayout =new HorizontalLayout(userCBox, okBtn);
            horizontalLayout.setComponentAlignment(userCBox, Alignment.MIDDLE_CENTER);
            horizontalLayout.setComponentAlignment(okBtn, Alignment.MIDDLE_CENTER);

            VerticalLayout vLayout = new VerticalLayout(label, horizontalLayout, createEmployeeBtn);
            vLayout.setSpacing(true);
            addComponents(vLayout);
            vLayout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
            vLayout.setComponentAlignment(horizontalLayout, Alignment.MIDDLE_CENTER);
            vLayout.setComponentAlignment(createEmployeeBtn, Alignment.MIDDLE_CENTER);




        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private boolean checkUserData() {
        if (userCBox.getValue() != null /*&& passwordField.getValue() != null*/) {
            //EmployeeRepository employeeRepository = (EmployeeRepository) ((NavigatorUI) getCurrent()).repositories.get("employeeRepository");

            for (Employee user : ((NavigatorUI) UI.getCurrent()).getEmployees()) {
                if (user.getFullName().equals(userCBox.getValue().toString())/* && user.getPas().equals(passwordField.getValue())*/) {
                    if (user != null) {
                        VaadinSession.getCurrent().setAttribute(Employee.class, user);
                        //((NavigatorUI) getUI()).setCurUser(user);
                        return true;
                    } else {
                        return false;
                    }
                }

            }
        } else {
            return false;
        }
        return false;
    }

    private void getAllEmployeeToCBox() throws IOException {
        /*RabbitTemplate rabbitTemplate = ((NavigatorUI) UI.getCurrent()).getRabbitTemplate();
        rabbitTemplate.setExchange(TO_SERVICE_EMPLOYEE_FANOUT_EXCHANGE);
        Message msgRequest = createMessage(EMPLOYEE_SELECT_EVENT, "");
        Message msg = rabbitTemplate.sendAndReceive(msgRequest);
        String jsonEntities = new String(msg.getBody());
        //String jsonEntities = (String) rabbitTemplate.convertSendAndReceive("", "getAll");


        ObjectMapper mapper = new ObjectMapper();
        CollectionType javaType = mapper.getTypeFactory()
                .constructCollectionType(List.class, Employee.class);

        employees = mapper.readValue(jsonEntities, javaType);
        *//*List<Employee> chatUsers = chatUsersResponse.getBody();*/
        userCBox.setItems(((NavigatorUI) UI.getCurrent()).getEmployees());
    }



}
