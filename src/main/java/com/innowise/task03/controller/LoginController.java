package com.innowise.task03.controller;

import com.innowise.task03.Controller;
import com.innowise.task03.RequestMapping;
import com.innowise.task03.handler.HttpMethod;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.task03.entity.UserDAO;
import com.innowise.task03.entity.UserDTO;
import com.innowise.task03.entity.UserMapper;
import com.innowise.task03.service.JwtService;
import org.mapstruct.factory.Mappers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
public class LoginController {

    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final UserDAO userDAO = UserDAO.getInstance();

    @RequestMapping(url = "/login", method = HttpMethod.POST)
    public void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserDTO dto = jsonMapper.readValue(request.getReader(), UserDTO.class);
        if (userDAO.verifyUserbyLoginAndPasword(dto.getLogin(),dto.getPassword())) {
            JwtService jwtService = JwtService.getInstance();
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.println(jwtService.buildJwtForUser(userDAO.getByLogin(dto.getLogin()).get()));
        } else {
            sendError(401, "Login and/or password is incorrect", response);
        }


    }

    @RequestMapping(url = "/register", method = HttpMethod.POST)
    public void register(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserDTO dto = jsonMapper.readValue(request.getReader(), UserDTO.class);
        if (userDAO.save(userMapper.userDTOToUser(dto))) {
            response.setContentType("application/json");
            response.setStatus(201);
        } else {
            sendError(409, "Login already taken", response);
        }
    }

    private void sendError(int errorCode, String errorReason, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setStatus(errorCode);
        PrintWriter out = response.getWriter();
        out.println(errorReason);
    }
}
