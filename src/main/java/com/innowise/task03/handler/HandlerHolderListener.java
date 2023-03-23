package com.innowise.task03.handler;

import com.innowise.task03.exception.HandlerCreationException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class HandlerHolderListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        HandlerMethodHolder controllerHolder = HandlerMethodHolder.getInstance();
        try {
            controllerHolder.updateHandlerMapping();
        } catch (Exception e) {
            throw new HandlerCreationException("There was an error during handler creation",e);
        }
        System.out.println(controllerHolder.getHandlerMapping().toString());
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {

    }
}
