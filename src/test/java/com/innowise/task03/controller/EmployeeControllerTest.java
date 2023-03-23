package com.innowise.task03.controller;

import com.innowise.task03.entity.Employee;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeControllerTest {

    private static final String DRIVER_CLASS_NAME = "org.h2.Driver";
    private static final String CONNECTION_URL = "jdbc:h2:~/InnowiseTest;AUTO_SERVER=TRUE";
    private static final String LOGIN = "admin";
    private static final String PASSWORD = "adminpass";

    private static final String ADMIN_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJaaWRhbmUiLCJ1c2VyIjoiQWRtaW4iLCJyb2xlIjoiQURNSU4iLCJpYXQiOjE0NjY3OTY4MjIsImV4cCI6NDYyMjQ3MDQyMn0.J264WUrRySpKiClFCin7zN8XW4spc2PVVLwkqN7_ipY";
    private static final String CLIENT_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJaaWRhbmUiLCJ1c2VyIjoiQ2xpZW50Iiwicm9sZSI6IkNMSUVOVCIsImlhdCI6MTQ2Njc5NjgyMiwiZXhwIjo0NjIyNDcwNDIyfQ.GfqS0Rr4W3-fAsz8gXssbBVR3pqOIuUaM9r5BI0dWZs";
    private static Connection conn;

    @BeforeAll
    public static void getConnectionToDatabase() throws ClassNotFoundException, SQLException {
        Class.forName(DRIVER_CLASS_NAME);
        conn = DriverManager.getConnection(CONNECTION_URL, LOGIN, PASSWORD);
    }

    @BeforeEach
    public void populateDatabase() throws SQLException, ClassNotFoundException {
     /*   Class.forName(DRIVER_CLASS_NAME);
        Connection conn = null;
        conn = DriverManager.getConnection(CONNECTION_URL, LOGIN, PASSWORD);*/
        String query = "TRUNCATE TABLE EMPLOYEE RESTART IDENTITY;\n" +
                "INSERT INTO EMPLOYEE (NAME, POSITION, EMAIL, SALARY) VALUES ('Mr. Smith', 'Killer', 'kill@isnot.good', '100000');\n" +
                "INSERT INTO EMPLOYEE (NAME, POSITION, EMAIL, SALARY) VALUES ('Scruffy Scruffington', 'Janitor', 'scruffy@planetexpress.gal', '1');\n" +
                "INSERT INTO EMPLOYEE (NAME, POSITION, EMAIL, SALARY) VALUES ('Okabe Rintaro', 'Mad Scientist', 'elPsyCongroo@Lab.Mem', '0');\n" +
                "INSERT INTO EMPLOYEE (NAME, POSITION, EMAIL, SALARY) VALUES ('Susan Calvin', 'Robopsychologist', 'threeLaws@robots.us', '300000');";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.execute();
        statement.close();
    }


    @Test
    void getSingleEmployeeSuccessAsClient() throws Exception {
        HttpResponse<String> response = sendGet("http://localhost:8080/api/employee?id=2",CLIENT_TOKEN);
        assertEquals(200,response.statusCode());
        assertEquals("application/json",response.headers().map().get("Content-Type").get(0).split(";")[0]);
        assertEquals("{\"id\":2,\"name\":\"Scruffy Scruffington\",\"position\":\"Janitor\",\"email\":\"scruffy@planetexpress.gal\",\"salary\":1}", response.body().trim());
    }

    @Test
    void getAllEmployeeSuccessAsClient() throws Exception {
        HttpResponse<String> response = sendGet("http://localhost:8080/api/employee",CLIENT_TOKEN);
        assertEquals(200,response.statusCode());
        assertEquals("application/json",response.headers().map().get("Content-Type").get(0).split(";")[0]);
        assertEquals("[{\"id\":1,\"name\":\"Mr. Smith\",\"position\":\"Killer\",\"email\":\"kill@isnot.good\",\"salary\":100000},{\"id\":2,\"name\":\"Scruffy Scruffington\",\"position\":\"Janitor\",\"email\":\"scruffy@planetexpress.gal\",\"salary\":1},{\"id\":3,\"name\":\"Okabe Rintaro\",\"position\":\"Mad Scientist\",\"email\":\"elPsyCongroo@Lab.Mem\",\"salary\":0},{\"id\":4,\"name\":\"Susan Calvin\",\"position\":\"Robopsychologist\",\"email\":\"threeLaws@robots.us\",\"salary\":300000}]",
                response.body().trim());
    }

    @Test
    void getSingleEmployeeSuccessAsAdmin() throws Exception {
        HttpResponse<String> response = sendGet("http://localhost:8080/api/employee?id=2",ADMIN_TOKEN);
        assertEquals(200,response.statusCode());
        assertEquals("application/json",response.headers().map().get("Content-Type").get(0).split(";")[0]);
        assertEquals("{\"id\":2,\"name\":\"Scruffy Scruffington\",\"position\":\"Janitor\",\"email\":\"scruffy@planetexpress.gal\",\"salary\":1}", response.body().trim());
    }

    @Test
    void getAllEmployeeSuccessAsAdmin() throws Exception {
        HttpResponse<String> response = sendGet("http://localhost:8080/api/employee",ADMIN_TOKEN);
        assertEquals(200,response.statusCode());
        assertEquals("application/json",response.headers().map().get("Content-Type").get(0).split(";")[0]);
        assertEquals("[{\"id\":1,\"name\":\"Mr. Smith\",\"position\":\"Killer\",\"email\":\"kill@isnot.good\",\"salary\":100000},{\"id\":2,\"name\":\"Scruffy Scruffington\",\"position\":\"Janitor\",\"email\":\"scruffy@planetexpress.gal\",\"salary\":1},{\"id\":3,\"name\":\"Okabe Rintaro\",\"position\":\"Mad Scientist\",\"email\":\"elPsyCongroo@Lab.Mem\",\"salary\":0},{\"id\":4,\"name\":\"Susan Calvin\",\"position\":\"Robopsychologist\",\"email\":\"threeLaws@robots.us\",\"salary\":300000}]",
                response.body().trim());
    }

    @Test
    void getSingleWrongEmployee404Error() throws Exception {
        HttpResponse<String> response = sendGet("http://localhost:8080/api/employee?id=10",ADMIN_TOKEN);
        assertEquals(404,response.statusCode());
    }

    @Test
    void saveNewEmployeeAsAdminSucsess() throws Exception {
        HttpResponse<String> response = sendPost("http://localhost:8080/api/employee",
                ADMIN_TOKEN,
                "{\"name\":\"Rand'alThor\",\"position\":\"Dragon Reborn\",\"email\":\"LordDragon@blacktower.an\",\"salary\":29900000}");
        assertEquals(201,response.statusCode());
    }

    @Test
    void saveNewEmployeeAsClient403Recieved() throws Exception {
        HttpResponse<String> response = sendPost("http://localhost:8080/api/employee",
                CLIENT_TOKEN,
                "{\"name\":\"Rand'alThor\",\"position\":\"Dragon Reborn\",\"email\":\"LordDragon@blacktower.an\",\"salary\":29900000}");
        assertEquals(403,response.statusCode());
    }

    @Test
    void updateEmployeeAllFieldsAsAdminSucsess() throws Exception {
        HttpResponse<String> response = sendPut("http://localhost:8080/api/employee?id=2",
                ADMIN_TOKEN,
                "{\"name\":\"Philip J. Fry\",\"position\":\"Freak\",\"email\":\"phil.j.fru@planetexpress.gal\",\"salary\":2}");
        assertEquals(200,response.statusCode());
    }

    @Test
    void updateEmployeeSingleFieldAsAdminSucsess() throws Exception {
        HttpResponse<String> response = sendPut("http://localhost:8080/api/employee?id=1",
                ADMIN_TOKEN,
                "{\"position\":\"Peacemaker\"}");
        assertEquals(200,response.statusCode());
    }

    @Test
    void updateEmployeeAsAdminWrongId() throws Exception {
        HttpResponse<String> response = sendPut("http://localhost:8080/api/employee?id=10",
                ADMIN_TOKEN,
                "{\"position\":\"Peacemaker\"}");
        assertEquals(404,response.statusCode());
    }

    @Test
    void updateEmployeeAsAdminParameterAbsent() throws Exception {
        HttpResponse<String> response = sendPut("http://localhost:8080/api/employee",
                ADMIN_TOKEN,
                "{\"position\":\"Peacemaker\"}");
        assertEquals(400,response.statusCode());
    }

    @Test
    void updateEmployeeAsClient403Recieved() throws Exception {
        HttpResponse<String> response = sendPut("http://localhost:8080/api/employee?id=2",
                CLIENT_TOKEN,
                "{\"name\":\"Philip J. Fry\",\"position\":\"Freak\",\"email\":\"phil.j.fru@planetexpress.gal\",\"salary\":2}");
        assertEquals(403,response.statusCode());
    }

    @Test
    void deleteEmployeeAsAdminSucsess() throws Exception {
        HttpResponse<String> response = sendDelete("http://localhost:8080/api/employee?id=3",ADMIN_TOKEN);
        assertEquals(200,response.statusCode());
    }

    @Test
    void deleteEmployeeAsAdminWrongId404() throws Exception {
        HttpResponse<String> response = sendDelete("http://localhost:8080/api/employee?id=30",ADMIN_TOKEN);
        assertEquals(404,response.statusCode());
    }

    @Test
    void deleteEmployeeAsAdminNoParameter400() throws Exception {
        HttpResponse<String> response = sendDelete("http://localhost:8080/api/employee",ADMIN_TOKEN);
        assertEquals(400,response.statusCode());
    }

    @Test
    void deleteEmployeeAsClientNoAccess() throws Exception {
        HttpResponse<String> response = sendDelete("http://localhost:8080/api/employee?id=3",CLIENT_TOKEN);
        assertEquals(403,response.statusCode());
    }

    @AfterAll
    public static void closeConnectionToDatabase() throws ClassNotFoundException, SQLException {
        conn.close();
    }

    private HttpResponse<String> sendGet(String url, String token) throws Exception {
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .setHeader("Authorization", token)
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendDelete(String url, String token) throws Exception {
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .setHeader("Authorization", token)
                .DELETE()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendPost(String url, String token, String body) throws Exception {
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .setHeader("Authorization", token)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendPut(String url, String token, String body) throws Exception {
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .setHeader("Authorization", token)
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }



}