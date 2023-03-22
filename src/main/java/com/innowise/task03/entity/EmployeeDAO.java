package com.innowise.task03.entity;

import com.innowise.task03.jdbc.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeDAO implements DAO<Employee> {

    private static EmployeeDAO instance = null;
    private final ConnectionFactory CONNECTION_FACTORY = ConnectionFactory.getInstance();

    public static EmployeeDAO getInstance() {
        EmployeeDAO localInstance = instance;
        if (localInstance == null) {
            synchronized (EmployeeDAO.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new EmployeeDAO();
                }
            }
        }
        return localInstance;
    }

    @Override
    public Optional<Employee> get(long id) {
        try (Connection connection = CONNECTION_FACTORY.getConnection()) {
            String query = "SELECT * FROM EMPLOYEE WHERE EMPLOYEE_ID = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setLong(1, id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        Employee employee = Employee.builder().id(resultSet.getLong("EMPLOYEE_ID"))
                                .name(resultSet.getString("NAME"))
                                .position(resultSet.getString("POSITION"))
                                .email(resultSet.getString("EMAIL"))
                                .salary(resultSet.getInt("SALARY"))
                                .build();
                        return Optional.of(employee);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<Employee> getAll() {
        List<Employee> employeeList  = new ArrayList<>();
        try (Connection connection = CONNECTION_FACTORY.getConnection()) {
            String query = "SELECT * FROM EMPLOYEE";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        Employee employee = Employee.builder().id(resultSet.getLong("EMPLOYEE_ID"))
                                .name(resultSet.getString("NAME"))
                                .position(resultSet.getString("POSITION"))
                                .email(resultSet.getString("EMAIL"))
                                .salary(resultSet.getInt("SALARY"))
                                .build();
                        employeeList.add(employee);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return employeeList;
    }

    @Override
    public boolean save(Employee employee) {
        try (Connection connection = CONNECTION_FACTORY.getConnection()) {
            String query = "INSERT INTO EMPLOYEE(NAME, POSITION, EMAIL, SALARY) VALUES(?,?,?,?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1,employee.getName());
                statement.setString(2,employee.getPosition());
                statement.setString(3,employee.getEmail());
                statement.setInt(4,employee.getSalary());
                statement.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean update(Employee updatedEmployee) {
        if (checkIfExistById(updatedEmployee.getId())) {
            Employee oldEmployee = this.get(updatedEmployee.getId()).get();
            try (Connection connection = CONNECTION_FACTORY.getConnection()) {
                String query = "UPDATE EMPLOYEE SET NAME=?, POSITION=?, EMAIL=?, SALARY=? WHERE EMPLOYEE_ID=?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, (updatedEmployee.getName() == null) ? oldEmployee.getName() : updatedEmployee.getName());
                    statement.setString(2, (updatedEmployee.getPosition() == null) ? oldEmployee.getPosition() : updatedEmployee.getPosition());
                    statement.setString(3, (updatedEmployee.getEmail() == null) ? oldEmployee.getEmail() : updatedEmployee.getEmail());
                    statement.setInt(4, (updatedEmployee.getSalary() == null) ? oldEmployee.getSalary() : updatedEmployee.getSalary());
                    statement.setLong(5, updatedEmployee.getId());
                    statement.executeUpdate();
                    return true;
                }
            } catch (SQLException e) {
                return false;
            }
        } else {
            return false;
        }

    }

    @Override
    public boolean delete(Employee employee) {
        if (checkIfExistById(employee.getId())) {
            try (Connection connection = CONNECTION_FACTORY.getConnection()) {
                String query = "DELETE FROM EMPLOYEE WHERE EMPLOYEE_ID=?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setLong(1,employee.getId());
                    statement.executeUpdate();
                    return true;
                }
            } catch (SQLException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean deleteById(Long id) {
        if(checkIfExistById(id)){
            return this.delete(get(id).get());
        } else {
            return false;
        }
    }

    private boolean checkIfExistById (Long id) {
        Optional<Employee> employeeOptional = this.get(id) ;
        return employeeOptional.isPresent();
    }

}
