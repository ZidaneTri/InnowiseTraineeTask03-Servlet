package com.innowise.task03.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.lang.reflect.InvocationTargetException;

@WebListener
public class HandlerHolderListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        HandlerMethodHolder controllerHolder = HandlerMethodHolder.getInstance();
        try {
            controllerHolder.updateHandlerMapping();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        System.out.println(controllerHolder.getHandlerMapping().toString());
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {

    }
}
