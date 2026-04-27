package com.novelverse.novelverse.repository.impl;

import com.novelverse.novelverse.domain.Bookmark;
import com.novelverse.novelverse.repository.BookmarkRepository;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BookmarkRepositoryImpl implements BookmarkRepository {

    private final DataSource dataSource;
    public BookmarkRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public void save(Bookmark bookmark) {

        String query = "INSERT INTO bookmarks (novel_id, chapter_id) VALUES (?, ?)";

        try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, bookmark.getUserId());
            preparedStatement.setLong(2, bookmark.getChapterId());
            preparedStatement.executeUpdate();


        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    @Override
    public void delete(Long userId,  Long chapterId) {
        String query = "DELETE FROM bookmarks WHERE user_id = ? AND chapter_id = ?";

        try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, userId);
            preparedStatement.setLong(2, chapterId);
            preparedStatement.executeUpdate();


        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    @Override
    public List<Bookmark> findByUserId(Long novelId) {
        List<Bookmark> bookmarks = new ArrayList<>();
        String query = "SELECT * FROM bookmarks WHERE user_id = ?";

        try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, novelId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Bookmark bookmark = new Bookmark();
                bookmark.setUserId(resultSet.getLong("user_id"));
                bookmark.setChapterId(resultSet.getLong("chapter_id"));
                bookmarks.add(bookmark);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        return bookmarks;
    }





}
