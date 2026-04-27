package com.novelverse.novelverse.repository.impl;

import com.novelverse.novelverse.domain.Comment;
import com.novelverse.novelverse.repository.CommentRepository;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CommentRepositoryImpl implements CommentRepository {

    private final DataSource dataSource;

    public CommentRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void save(Comment comment) {
        String query = "INSERT INTO comments(user_id,novel_id,text) VALUES (?,?,?)";

        try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, comment.getUserId());
            preparedStatement.setLong(2, comment.getNovelId());
            preparedStatement.setString(3, comment.getText());

            preparedStatement.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public List<Comment> findByNovelId(Long novelId) {
        List<Comment> comments = new ArrayList<>();
        String query = "SELECT * FROM comments WHERE novel_id = ?";

        try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, novelId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Comment comment = new Comment();
                comment.setNovelId(resultSet.getLong("novel_id"));
                comment.setUserId(resultSet.getLong("user_id"));
                comment.setText(resultSet.getString("text"));
                comments.add(comment);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        return comments;
    }

}
