/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ChatUI.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vaadin.ui.*;
import org.ChatUI.entity.Employee;
import org.ChatUI.mq.RabbitMqPublisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.lang.reflect.Field;

import static com.vaadin.ui.UI.getCurrent;


public class EmployeeWindow extends Window{
    private Button actionBtn = new Button();
    private Button cancelBtn = new Button("Отмена", e-> this.close());
    private VerticalLayout mainLayout= new VerticalLayout();
    private VerticalLayout vLayout = new VerticalLayout();
    private TextField fioTField = new TextField("ФИО:");

    //upd
    public EmployeeWindow(Employee employee) {
        actionBtn.setCaption("Применить");
        setCommonContent();
        fioTField.setValue(employee.getFullName());
        actionBtn.addClickListener(e -> changeObj(employee));
    }
    
    //new
    public EmployeeWindow() {
        actionBtn.setCaption("Создать пользователя");
        setCommonContent();
        
        actionBtn.addClickListener(e -> addObj());
    }
    
    private void addObj() {

        Employee employee = new Employee();
        employee.setFullName(fioTField.getValue());
        
        /*RestTemplate restTemplate = (RestTemplate) ((NavigatorUI) getCurrent()).restTemplate;
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Data attached to the request.
        HttpEntity<Employee> requestBody = new HttpEntity<>(employee, headers);
        // Send request with POST method.
        Employee e = restTemplate.postForObject("http://localhost:8888/api/addEmployee", requestBody, Employee.class);
*/
        RabbitTemplate rabbitTemplate = ((NavigatorUI) getCurrent()).getRabbitTemplate();
        try {
            new RabbitMqPublisher().sendCreateEmployeeMessage(rabbitTemplate, employee);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        this.close();
    }
    
    private void changeObj(Employee oldEmployee) {
        Employee employee = new Employee(oldEmployee);
        employee.setFullName(fioTField.getValue());
        
        /*RestTemplate restTemplate = (RestTemplate) ((NavigatorUI) getCurrent()).restTemplate;
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Data attached to the request.
        HttpEntity<Employee> requestBody = new HttpEntity<>(employee, headers);
        // Send request with POST method.
        restTemplate.put("http://localhost:8888/api/changeEmployee/"+ oldEmployee.getId(), requestBody, Employee.class);*/
        RabbitTemplate rabbitTemplate = ((NavigatorUI) getCurrent()).getRabbitTemplate();
        try {

            new RabbitMqPublisher().sendUpdateEmployeeMessage(rabbitTemplate, oldEmployee, employee);

            //rabbitTemplate.convertAndSend("", jsonEmployeeForRemove);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        this.close();
    }

    
    
    private void setCommonContent(){
        
        Field[] fields = Employee.class.getDeclaredFields();

        vLayout.addComponents(fioTField);
        HorizontalLayout hLayout = new HorizontalLayout(actionBtn,cancelBtn);
        mainLayout.addComponents(vLayout, hLayout);
        
        this.setClosable(false);
        this.setModal(true);
        this.setResizable(false);
        this.setContent(mainLayout);
        
    }




}
