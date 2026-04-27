package com.novelverse.novelverse.repository.impl;

import com.novelverse.novelverse.domain.Novel;
import com.novelverse.novelverse.repository.NovelRepository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

@Repository
public class NovelRepositoryImpl implements NovelRepository {
    private final DataSource dataSource;

    public NovelRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Novel> findAll() {
        List<Novel> novels = new ArrayList<>();

        String query = "SELECT * FROM novels";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()){

            while (rs.next()){
                Novel novel = new Novel();
                novel.setId(rs.getLong("id"));
                novel.setTitle(rs.getString("title"));
                novel.setDescription(rs.getString("description"));

                novels.add(novel);

            }
        } catch (SQLException e){
            e.printStackTrace();
        }


        return novels;
    }

    @Override
    public Optional<Novel> findById(long id) {

        String query = "SELECT * FROM novels WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)){

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                Novel novel = new Novel();
                novel.setId(rs.getLong("id"));
                novel.setTitle(rs.getString("title"));
                novel.setDescription(rs.getString("description"));

                return Optional.of(novel);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        return Optional.empty();
    }


    @Override
    public void save(Novel novel) {
        String query = "INSERT INTO novels (title, description) VALUES (?, ?)";


        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);){

            stmt.setString(1, novel.getTitle());
            stmt.setString(2, novel.getDescription());
            stmt.executeUpdate();

        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
