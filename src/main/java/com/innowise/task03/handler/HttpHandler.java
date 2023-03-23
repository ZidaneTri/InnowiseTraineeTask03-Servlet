package com.innowise.task03.handler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class HttpHandler {

    private final Method method;

    private final Class<?> clazz;

    private final Object handlerObject;

}
