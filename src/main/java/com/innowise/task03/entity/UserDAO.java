package com.innowise.task03.entity;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.innowise.task03.jdbc.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO implements DAO<User>{

    private static UserDAO instance = null;
    private final ConnectionFactory CONNECTION_FACTORY = ConnectionFactory.getInstance();

    public static UserDAO getInstance() {
        UserDAO localInstance = instance;
        if (localInstance == null) {
            synchronized (UserDAO.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new UserDAO();
                }
            }
        }
        return localInstance;
    }

    @Override
    public Optional<User> get(long id) {
        try (Connection connection = CONNECTION_FACTORY.getConnection()) {
            String query = "SELECT * FROM USER_TABLE WHERE USER_ID = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setLong(1, id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        User user = User.builder().id(resultSet.getLong("USER_ID"))
                                .login(resultSet.getString("LOGIN"))
                                .password(resultSet.getString("PASSWORD"))
                                .role(Role.valueOf(resultSet.getString("ROLE")))
                                .build();
                        return Optional.of(user);
                    }
                }
            }
        } catch (SQLException e) {
            return Optional.empty();
        }
        return Optional.empty();
    }


    public Optional<User> getByLogin(String login) {
        try (Connection connection = CONNECTION_FACTORY.getConnection()) {
            String query = "SELECT * FROM USER_TABLE WHERE LOGIN = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, login);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        User user = User.builder().id(resultSet.getLong("USER_ID"))
                                .login(resultSet.getString("LOGIN"))
                                .password(resultSet.getString("PASSWORD"))
                                .role(Role.valueOf(resultSet.getString("ROLE")))
                                .build();
                        return Optional.of(user);
                    }
                }
            }
        } catch (SQLException e) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    @Override
    public List<User> getAll() {
        List<User> userList  = new ArrayList<>();
        try (Connection connection = CONNECTION_FACTORY.getConnection()) {
            String query = "SELECT * FROM USER_TABLE";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        User user = User.builder().id(resultSet.getLong("USER_ID"))
                                .login(resultSet.getString("LOGIN"))
                                .password(resultSet.getString("POSITION"))
                                .role(Role.valueOf(resultSet.getString("ROLE")))
                                .build();
                        userList.add(user);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userList;
    }

    @Override
    public boolean save(User user) {
        try (Connection connection = CONNECTION_FACTORY.getConnection()) {
            String query = "INSERT INTO USER_TABLE(LOGIN, PASSWORD, ROLE) VALUES(?,?,?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1,user.getLogin());
                statement.setString(2,BCrypt.withDefaults().hashToString(12, user.getPassword().toCharArray()));
                statement.setString(3,user.getRole().name());
                statement.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean update(User user) {
        return false;
    }

    @Override
    public boolean delete(User user) {
        return false;
    }

    public boolean verifyUserbyLoginAndPasword(String login, String password){
        Optional<User> optionalUser = getByLogin(login);
        if (optionalUser.isPresent()) {
            return BCrypt.verifyer().verify(password.toCharArray(), optionalUser.get().getPassword()).verified;
        } else {
            return false;
        }

    }


}
