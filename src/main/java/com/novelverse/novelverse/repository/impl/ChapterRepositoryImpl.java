package com.novelverse.novelverse.repository.impl;

import com.novelverse.novelverse.domain.Chapter;
import com.novelverse.novelverse.repository.ChapterRepository;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ChapterRepositoryImpl implements ChapterRepository {
    private final DataSource dataSource;

    public ChapterRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Chapter> findByNovelId(long novelId) {
        List<Chapter> chapters = new ArrayList<>();

        String query = "SELECT * FROM chapter WHERE novel_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)){

            stmt.setLong(1, novelId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Chapter chapter = new Chapter();
                chapter.setId(rs.getLong("chapter_id"));
                chapter.setNovelId(rs.getLong("novel_id"));
                chapter.setTitle(rs.getString("title"));
                chapter.setContent(rs.getString("content"));

                chapters.add(chapter);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        return chapters;
    }

    @Override
    public Chapter findById(long chapterId) {
        String query = "SELECT * FROM chapter WHERE chapter_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setLong(1, chapterId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Chapter chapter = new Chapter();
                chapter.setId(rs.getLong("chapter_id"));
                chapter.setNovelId(rs.getLong("novel_id"));
                chapter.setTitle(rs.getString("title"));
                chapter.setContent(rs.getString("content"));
                return chapter;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void save(Chapter chapter) {
        String query = "INSERT INTO chapter (novel_id, title, content) VALUES (?, ?, ?)";


        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)){

            stmt.setLong(1, chapter.getNovelId());
            stmt.setString(2, chapter.getTitle());
            stmt.setString(3, chapter.getContent());
            stmt.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void update(Chapter chapter) {
        String query = "UPDATE chapter SET title = ?, content = ? WHERE chapter_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, chapter.getTitle());
            stmt.setString(2, chapter.getContent());
            stmt.setLong(3, chapter.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
