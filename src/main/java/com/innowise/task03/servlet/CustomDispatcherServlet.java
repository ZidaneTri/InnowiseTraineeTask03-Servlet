package com.innowise.task03.servlet;

import com.innowise.task03.exception.HandlerInvocationException;
import com.innowise.task03.handler.HttpHandler;
import com.innowise.task03.handler.HttpMapping;
import com.innowise.task03.handler.HttpMethod;
import com.innowise.task03.handler.HandlerMethodHolder;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "CustomDispatcherServlet", value = "/api/*")
public class CustomDispatcherServlet extends HttpServlet {

    HandlerMethodHolder controllerHolder = HandlerMethodHolder.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doHandle(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doHandle(request,response);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doHandle(request,response);

    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doHandle(request,response);
    }

    private void doHandle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        HttpHandler handler;
        HttpMapping mapping = HttpMapping.builder().path(pathInfo).method(HttpMethod.valueOf(request.getMethod())).build();
        handler = controllerHolder.getHandlerMapping().get(mapping);
        if (handler == null) {
            handleNotFoundResponce(response);
            return;
        }
        try {
            handler.getMethod().invoke(handler.getHandlerObject(),request,response);
        } catch (ReflectiveOperationException e) {
            throw new HandlerInvocationException(e);
        }

    }

    private HttpServletResponse handleNotFoundResponce(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print("404");
        return response;
    }


}
