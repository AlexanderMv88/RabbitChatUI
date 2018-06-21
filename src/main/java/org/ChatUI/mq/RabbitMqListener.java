package org.ChatUI.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ChatUI.Broadcaster;
import org.ChatUI.WebSocketMsg;
import org.ChatUI.entity.Msg;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.ChatUI.mq.RabbitEmployee.*;
import static org.ChatUI.mq.RabbitMsg.FROM_SERVICE_MSG_EVENT_QUEUE;
import static org.ChatUI.mq.RabbitMsg.MSG_CREATED_EVENT;

@EnableRabbit //нужно для активации обработки аннотаций @RabbitListener
@Component
public class RabbitMqListener {


    @RabbitListener(queues = FROM_SERVICE_EMPLOYEE_EVENT_QUEUE)
    public void fromServiceEmployee(Message message){
        System.out.println("get message "+message);
        String action = (String) message.getMessageProperties().getHeaders().get("action");
        String body = null;
        try {
            body = new String(message.getBody(), "UTF-8");
            if (EMPLOYEE_CREATED_EVENT.equals(action)) {
                System.out.println("get message with action "+action+" "+body);
                WebSocketMsg wsMsg = new WebSocketMsg(WebSocketMsg.MsgType.EMPLOYEE_CREATE);
                wsMsg.setText(body);
                Broadcaster.broadcast(wsMsg);
            }else if (EMPLOYEE_UPDATED_EVENT.equals(action)) {
                System.out.println("get message with action "+action+" "+body);
                WebSocketMsg wsMsg = new WebSocketMsg(WebSocketMsg.MsgType.EMPLOYEE_UPDATE);
                wsMsg.setText(body);
                Broadcaster.broadcast(wsMsg);
            }else if (EMPLOYEE_DELETED_EVENT.equals(action)) {
                System.out.println("get message with action "+action+" "+body);
                WebSocketMsg wsMsg = new WebSocketMsg(WebSocketMsg.MsgType.EMPLOYEE_DELETE);
                wsMsg.setText(body);
                Broadcaster.broadcast(wsMsg);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }



    @RabbitListener(queues = FROM_SERVICE_MSG_EVENT_QUEUE)
    public String getMsgRequests(Message message) throws JsonProcessingException {
        /*List<Employee> employees = employeeRepository.findAll();
        String jsonEmployees =  new ObjectMapper().writeValueAsString(employees);
        return  jsonEmployees;*/

        System.out.println("get message "+message);
        String action = (String) message.getMessageProperties().getHeaders().get("action");
        String body = null;
        try {
            body = new String(message.getBody(), "UTF-8");
            if (MSG_CREATED_EVENT.equals(action)) {
                System.out.println("get message with action "+action+" "+body);
                //return null;

                WebSocketMsg wsMsg = new WebSocketMsg(WebSocketMsg.MsgType.MSG_CREATE);
                wsMsg.setText(body);
                Broadcaster.broadcast(wsMsg);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



}

