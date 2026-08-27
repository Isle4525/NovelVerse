package com.novelverse.novelverse.repository.impl;


import com.novelverse.novelverse.domain.User;
import com.novelverse.novelverse.repository.UserRepository;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final DataSource dataSource;


    public UserRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public Optional<User> findById(Long username) {

        String query = "SELECT * FROM users WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)){
            stmt.setLong(1, username);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setName(rs.getString("username"));
                user.setPassword(rs.getString("password"));

                return Optional.of(user);
            }


        }catch (SQLException e){
            e.printStackTrace();
        }


        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsername(String username) {

        String query = "SELECT * FROM users WHERE username = ?";

        try (Connection connection = dataSource.getConnection();
        PreparedStatement stmt = connection.prepareStatement(query)){
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setName(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                return Optional.of(user);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<User> findAll() {

        List<User> users = new ArrayList<>();

        String query = "SELECT * FROM users";

        try (Connection connection = dataSource.getConnection();
        PreparedStatement stmt = connection.prepareStatement(query);
        ResultSet rs = stmt.executeQuery()){

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setName(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                users.add(user);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        return users;
    }

    @Override
    public void save(User user) {

        String query = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection connection = dataSource.getConnection();
        PreparedStatement stmt = connection.prepareStatement(query)){

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getPassword());

            stmt.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
        }

    }
}
