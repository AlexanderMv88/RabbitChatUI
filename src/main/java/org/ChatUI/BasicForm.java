/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ChatUI;



import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;
import org.ChatUI.entity.Employee;


import static org.ChatUI.ui.NavigatorUI.LOGIN_FORM;


/**
 *
 * @author a.martyushev
 */
public class BasicForm extends CustomComponent implements View, Handler  {   

    public Employee user;

    Panel panel = new Panel();
    Action quitAction = new ShortcutAction("Quit", ShortcutAction.KeyCode.Q, new int[] { ShortcutAction.ModifierKey.CTRL });
    
    HorizontalLayout hLayout = new HorizontalLayout(); 
    public VerticalLayout vLayout = new VerticalLayout();
    HorizontalLayout hLogoutLayout = new HorizontalLayout(); 
    HorizontalLayout hInfoLayout = new HorizontalLayout(); 
    Label userLbl = new Label();
    Label positionLbl = new Label();
    Button logoutBtn = new Button("Выход", e->{
            VaadinSession.getCurrent().close();
            Page.getCurrent().reload();
        });
    public BasicForm() {
        setCompositionRoot(panel);
        
        /*Button logoutBtn = new Button("Выход", e->{
            VaadinSession.getCurrent().close();
            Page.getCurrent().reload();
        });*/
        
        hLogoutLayout.addComponent(logoutBtn);
        hLogoutLayout.setComponentAlignment(logoutBtn, Alignment.MIDDLE_RIGHT);
        hLogoutLayout.setWidth("100%");
        
        hInfoLayout.addComponents(userLbl, positionLbl);
        hInfoLayout.setComponentAlignment(userLbl, Alignment.MIDDLE_LEFT);
        hInfoLayout.setComponentAlignment(positionLbl, Alignment.MIDDLE_LEFT);
        
        hLayout.addComponents(hInfoLayout,hLogoutLayout);
        hLayout.setComponentAlignment(hLogoutLayout, Alignment.MIDDLE_RIGHT);
        hLayout.setWidth("100%");
        
        //checkCurrentUser();
        //addComponent(hLayout);
        vLayout.addComponent(hLayout);
                
        panel.setContent(vLayout);
        panel.addActionHandler(this);
        panel.setWidth("100%");
        panel.setHeight("100%");
        this.setHeight("100%");
        
        
    }
    
    public boolean checkCurrentUser(){
       
        user = VaadinSession.getCurrent().getAttribute(Employee.class);
        
        if (user!=null){
            userLbl.setCaption("Пользователь: "+user.getFullName());
            //positionLbl.setCaption("Должность: "+user.getPosition());
            return true;
        }else{
            getUI().getNavigator().navigateTo(LOGIN_FORM);
            return false;
        }
        
    }

    
    
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        
    }


    public Action[] getActions(Object target, Object sender) {
        System.out.println("getActions()");
        return new Action[] { quitAction };
    }


    public void handleAction(Action action, Object sender, Object target) {
        if (action == quitAction) {
            logoutBtn.click();
        }
    }
    

    
    
}
