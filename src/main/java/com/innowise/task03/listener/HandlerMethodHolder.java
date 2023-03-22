package com.innowise.task03.listener;


import com.innowise.task03.Controller;
import com.innowise.task03.RequestMapping;
import com.innowise.task03.exception.HandlerAlreadyPresentException;
import com.innowise.task03.exception.HandlerCreationException;
import org.reflections.Reflections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;

import static org.reflections.scanners.Scanners.*;

public class HandlerMethodHolder {
    private static volatile HandlerMethodHolder instance;

    private HashMap<HttpMapping,HttpHandler> handlerMapping = new HashMap<>();

    public static HandlerMethodHolder getInstance() {
        HandlerMethodHolder localInstance = instance;
        if (localInstance == null) {
            synchronized (HandlerMethodHolder.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new HandlerMethodHolder();
                }
            }
        }
        return localInstance;
    }

    public HashMap<HttpMapping,HttpHandler> getHandlerMapping() {
        if (handlerMapping.isEmpty()) {
            try {
                updateHandlerMapping();
            }catch (ReflectiveOperationException e) {
                throw new HandlerCreationException("There was an error during handler creation",e);
            }
        }
        return handlerMapping;
    }

    protected void updateHandlerMapping() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        Reflections reflections = new Reflections("com.innowise.task03.controller");

        Set<Class<?>> annotated = reflections.get(SubTypes.of(TypesAnnotated.with(Controller.class)).asClass());

        for (Class clazz : annotated) {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                RequestMapping annotation = method.getAnnotation(RequestMapping.class);
                if(annotation != null) {
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length == 2 & (parameterTypes[0] == HttpServletRequest.class) & (parameterTypes[1] == HttpServletResponse.class)) {
                        HttpMapping newMapping = HttpMapping.builder().path(annotation.url()).method(annotation.method()).build();

                        if (!this.handlerMapping.containsKey(newMapping)) {
                            this.handlerMapping.put(newMapping, HttpHandler.builder()
                                    .method(method)
                                    .clazz(clazz)
                                    .handlerObject(clazz.getDeclaredConstructor().newInstance())
                                    .build());
                        } else {
                            throw new HandlerAlreadyPresentException("Handler for path "+ annotation.url() +"and method " + annotation.method().toString() + "is already present");
                        }
                    }
                }
            }
        }
    }
}
