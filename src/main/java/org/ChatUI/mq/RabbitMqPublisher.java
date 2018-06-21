package org.ChatUI.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ChatUI.entity.Employee;
import org.ChatUI.entity.Msg;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.ChatUI.mq.RabbitEmployee.*;
import static org.ChatUI.mq.RabbitMsg.MSG_CREATE_EVENT;
import static org.ChatUI.mq.RabbitMsg.TO_SERVICE_MSG_FANOUT_EXCHANGE;

public class RabbitMqPublisher {
    public static Message createMessage(String action, String payload){
        return MessageBuilder.withBody(payload.getBytes())
                .setHeader("action", action)
                .build();

    }

    public void sendUpdateEmployeeMessage(RabbitTemplate rabbitTemplate, Employee oldEmployee, Employee newEmployee) throws JsonProcessingException {
        List<Employee> employees = new ArrayList<Employee>();
        employees.add(oldEmployee);
        employees.add(newEmployee);
        String jsonEmployeeForUpdate= new ObjectMapper().writeValueAsString(employees);
        Message msg = createMessage(EMPLOYEE_UPDATE_EVENT, jsonEmployeeForUpdate);
        rabbitTemplate.setExchange(TO_SERVICE_EMPLOYEE_FANOUT_EXCHANGE);
        rabbitTemplate.send(msg);
    }

    public void sendDeleteEmployeeMessage(RabbitTemplate rabbitTemplate, Employee employee) throws JsonProcessingException {
        String jsonEmployeeForRemove= new ObjectMapper().writeValueAsString(employee);
        Message msg = createMessage(EMPLOYEE_DELETE_EVENT, jsonEmployeeForRemove);
        rabbitTemplate.setExchange(TO_SERVICE_EMPLOYEE_FANOUT_EXCHANGE);
        rabbitTemplate.send(msg);
    }

    public void sendCreateEmployeeMessage(RabbitTemplate rabbitTemplate, Employee employee) throws JsonProcessingException {
        String jsonEmployeeForInsert= new ObjectMapper().writeValueAsString(employee);
        Message msg = createMessage(EMPLOYEE_CREATE_EVENT, jsonEmployeeForInsert);
        rabbitTemplate.setExchange(TO_SERVICE_EMPLOYEE_FANOUT_EXCHANGE);
        rabbitTemplate.send(msg);
    }

    public void sendCreateMsgMessage(RabbitTemplate rabbitTemplate, Msg msg) throws JsonProcessingException {
        String jsonMsgForInsert= new ObjectMapper().writeValueAsString(msg);
        Message message = createMessage(MSG_CREATE_EVENT, jsonMsgForInsert);
        rabbitTemplate.setExchange(TO_SERVICE_MSG_FANOUT_EXCHANGE);
        rabbitTemplate.send(message);
    }
}
