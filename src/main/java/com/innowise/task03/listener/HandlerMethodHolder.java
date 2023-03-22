package com.innowise.task03.listener;


import com.innowise.task03.Controller;
import com.innowise.task03.RequestMapping;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;

import static org.reflections.scanners.Scanners.*;

public class HandlerMethodHolder {
    private static volatile HandlerMethodHolder instance;

    private HashMap<HttpMapping,HttpHandler> controllerMap = new HashMap<>();

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

    public HashMap<HttpMapping,HttpHandler> getControllerMap() {
        if (controllerMap.isEmpty()) {
            try {
                updateControllerMap();
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return controllerMap;
    }

    protected void updateControllerMap() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        Reflections reflections = new Reflections("com.innowise.task03.controller");

        Set<Class<?>> annotated = reflections.get(SubTypes.of(TypesAnnotated.with(Controller.class)).asClass());

        for (Class clazz : annotated) {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                Annotation[] annotations = method.getDeclaredAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation instanceof RequestMapping) {
                        RequestMapping myAnnotation = (RequestMapping) annotation;
                        HttpMapping newMapping = HttpMapping.builder().path(myAnnotation.url()).method(myAnnotation.method()).build();
                        if (!this.controllerMap.containsKey(newMapping)) {
                            this.controllerMap.put(newMapping, HttpHandler.builder()
                                    .method(method)
                                    .clazz(clazz)
                                    .handlerObject(clazz.getDeclaredConstructor().newInstance())
                                    .build());
                        } else {
                            throw new RuntimeException(); //TODO: replace with my own exception
                        }

                        }
                    }
                }
            }
        }


}
