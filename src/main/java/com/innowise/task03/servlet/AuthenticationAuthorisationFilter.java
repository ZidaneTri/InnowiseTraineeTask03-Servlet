package com.innowise.task03.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.task03.entity.UserDAO;
import com.innowise.task03.entity.UserDTO;
import com.innowise.task03.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebFilter(filterName = "AuthenticationFilter",
            urlPatterns = {"/api/*"} )
public class AuthenticationAuthorisationFilter extends HttpFilter {

    private final List<String> allowedUnauthenticatedUrl = List.of("/login","/register");

    public void init(FilterConfig config) throws ServletException {
    }

    public void destroy() {
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (allowedUnauthenticatedUrl.contains(pathInfo)){
            chain.doFilter(request, response);
        } else {
            String authorization = request.getHeader("Authorization");
            if (authorization == null || !authorization.matches("Bearer .+")){
                response.setContentType("application/json");
                response.setStatus(401);
                PrintWriter out = response.getWriter();
                out.println("Request token is absent");
                return;
            }
            String token = authorization.replaceAll("(Bearer)", "").trim();
            JwtService jwtService = JwtService.getInstance();
            Jws<Claims> claims;
            try {
                claims = jwtService.verifyJwtForUser(token);
            } catch (JwtException e) {
                response.setContentType("application/json");
                response.setStatus(401);
                PrintWriter out = response.getWriter();
                out.println("Untrusted token");
                return;
            }
            String role = claims.getBody().get("role", String.class);
            if(role.equals("ROLE_CLIENT") & !request.getMethod().equals("GET")) {
                response.setContentType("application/json");
                response.setStatus(403);
                PrintWriter out = response.getWriter();
                out.println("You are not allowed to send this request");
                return;
            }
            chain.doFilter(request, response);

        }


    }
}
