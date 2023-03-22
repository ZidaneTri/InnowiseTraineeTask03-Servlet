package com.innowise.task03.controller;

import com.innowise.task03.Controller;
import com.innowise.task03.RequestMapping;
import com.innowise.task03.entity.Employee;
import com.innowise.task03.entity.EmployeeDAO;
import com.innowise.task03.entity.EmployeeDTO;
import com.innowise.task03.entity.EmployeeMapper;
import com.innowise.task03.listener.HttpMethod;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.factory.Mappers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

@Controller
public class EmployeeController {

    private final EmployeeMapper employeeMapper = Mappers.getMapper(EmployeeMapper.class);
    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final EmployeeDAO employeeDAO = EmployeeDAO.getInstance();

    @RequestMapping(url = "/employee", method = HttpMethod.GET)
    public void getEmployee(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getParameter("id") == null) {
            List<Employee> employeeList = employeeDAO.getAll();
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.println(jsonMapper.writeValueAsString(employeeList));
        } else {
            Optional<Employee> employeeOptional = employeeDAO.get(Long.parseLong(request.getParameter("id")));
            Employee employee;
            if (employeeOptional.isPresent()) {
                employee = employeeOptional.get();
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.print(jsonMapper.writeValueAsString(employee));
            } else {
                sendError(404,"No employee found",response);
            }
        }
    }

    @RequestMapping(url = "/employee", method = HttpMethod.POST)
    public void saveEmployee(HttpServletRequest request, HttpServletResponse response) throws IOException {
        EmployeeDTO dto = jsonMapper.readValue(request.getReader(), EmployeeDTO.class);
        employeeDAO.save(employeeMapper.employeeDTOToEmployee(dto));
        response.setContentType("application/json");
        response.setStatus(201);
    }

    @RequestMapping(url = "/employee", method = HttpMethod.PUT)
    public void updateEmployee(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getParameter("id") != null) {
            EmployeeDTO dto = jsonMapper.readValue(request.getReader(), EmployeeDTO.class);
            if (employeeDAO.update(employeeMapper.employeeDTOWithExternalIDToEmployee(dto,Long.parseLong(request.getParameter("id"))))) {
                return;
            }
            else {
                sendError(404,"No employee found to update",response);
            }
        } else {
            sendError(400,"No parameter present",response);
        }
    }



    @RequestMapping(url = "/employee", method = HttpMethod.DELETE)
    public void deleteEmployee(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getParameter("id") != null) {
            if (employeeDAO.deleteById(Long.parseLong(request.getParameter("id")))) {
                return;
            }
            else {
                sendError(404,"No employee found",response);
            }
        } else {
            sendError(400,"No parameter present",response);
        }
    }

    private void sendError(int errorCode, String errorReason, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setStatus(errorCode);
        PrintWriter out = response.getWriter();
        out.println(errorReason);
    }


}
