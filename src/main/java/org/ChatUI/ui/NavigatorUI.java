/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ChatUI.ui;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;

import org.ChatUI.Broadcaster;
import org.ChatUI.WebSocketMsg;
import org.ChatUI.entity.Employee;
import org.ChatUI.entity.Msg;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.ChatUI.mq.RabbitEmployee.EMPLOYEE_SELECT_EVENT;
import static org.ChatUI.mq.RabbitEmployee.TO_SERVICE_EMPLOYEE_FANOUT_EXCHANGE;
import static org.ChatUI.mq.RabbitMqPublisher.createMessage;


/**
 *
 * @author user
 */
@Component
@Push
@SpringUI
@Theme("valo")
public class NavigatorUI extends UI implements Broadcaster.BroadcastListener {


    private MainMenuForm mainMenuForm;
    private EmployeeForm employeeForm;
    private LoginForm loginForm;

    private List<Employee> employees;

    public List<Employee> getEmployees() {
        return employees;
    }

    Navigator navigator;

    public static final String EMPLOYEE_FORM = "employeeForm";
    public static final String LOGIN_FORM = "loginForm";
    public static final String MAIN_MENU_FORM = "mainMenuForm";

    public RestTemplate restTemplate = new RestTemplate();

    private RabbitTemplate rabbitTemplate;

    public RabbitTemplate getRabbitTemplate() {
        return rabbitTemplate;
    }

    @Autowired
    public NavigatorUI(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    protected void init(VaadinRequest request) {
        requestAllEmployee();

        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor("Alexander", "12345"));
        navigator = new Navigator(this, this);

        mainMenuForm = new MainMenuForm();
        employeeForm = new EmployeeForm();
        loginForm = new LoginForm();

        navigator.addView(MAIN_MENU_FORM, mainMenuForm);
        navigator.addView(EMPLOYEE_FORM, employeeForm);
        navigator.addView(LOGIN_FORM, loginForm);
        navigator.navigateTo(MAIN_MENU_FORM);
        Broadcaster.register(this);



    }

    private void requestAllEmployee() {
        RabbitTemplate rabbitTemplate = ((NavigatorUI) UI.getCurrent()).getRabbitTemplate();
        rabbitTemplate.setExchange(TO_SERVICE_EMPLOYEE_FANOUT_EXCHANGE);
        Message msgRequest = createMessage(EMPLOYEE_SELECT_EVENT, "");
        Message msg = rabbitTemplate.sendAndReceive(msgRequest);
        String jsonEntities = new String(msg.getBody());
        //String jsonEntities = (String) rabbitTemplate.convertSendAndReceive("", "getAll");


        ObjectMapper mapper = new ObjectMapper();
        CollectionType javaType = mapper.getTypeFactory()
                .constructCollectionType(List.class, Employee.class);

        try {
            employees = mapper.readValue(jsonEntities, javaType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    private void destroy() {
        Broadcaster.unregister(this);
        this.destroy();
    }

    public Navigator getNavigator() {
        return navigator;
    }


    @Override
    public void receiveBroadcast(WebSocketMsg message) {
        getUI().access(() -> {
            System.out.println("Получил сообщение в UI = " + message);
            String jsonEmployee;
            Employee employee = null;
            Msg msg = null;
            switch (message.getMsgType()) {

                case EMPLOYEE_DELETE:


                    try {
                        jsonEmployee = message.getText();
                        employee = new ObjectMapper().readValue(jsonEmployee, Employee.class);
                        employeeForm.employees.remove(employee);
                        employeeForm.employeeGrid.setItems(employeeForm.employees);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case EMPLOYEE_CREATE:


                    try {
                        jsonEmployee = message.getText();
                        employee = new ObjectMapper().readValue(jsonEmployee, Employee.class);
                        employees.add(employee);
                        employeeForm.employeeGrid.setItems(employees);
                        loginForm.userCBox.setItems(employees);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;

                case EMPLOYEE_UPDATE:


                    try {
                        String jsonEmployees = message.getText();

                        ObjectMapper mapper = new ObjectMapper();
                        CollectionType javaType = mapper.getTypeFactory()
                                .constructCollectionType(List.class, Employee.class);

                        List<Employee> employees = mapper.readValue(jsonEmployees, javaType);
                        Employee oldEmployee = employees.get(0);
                        Employee newEmployee = employees.get(1);
                        employeeForm.employees.remove(oldEmployee);
                        employeeForm.employees.add(newEmployee);

                        employeeForm.employees.sort(
                                Comparator.comparing(Employee::getId)
                        );

                        employeeForm.employeeGrid.setItems(employeeForm.employees);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;


                case MSG_CREATE:
                    try {
                        String jsonMsg = message.getText();

                        ObjectMapper mapper = new ObjectMapper();
                        msg = new ObjectMapper().readValue(jsonMsg, Msg.class);
                        mainMenuForm.chatTArea.setValue(mainMenuForm.chatTArea.getValue() + "\n" + msg.getSender()+": "+msg.getText());
                        //employeeForm.employeeGrid.setItems(employeeForm.employees);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
            }
        });
    }
}
