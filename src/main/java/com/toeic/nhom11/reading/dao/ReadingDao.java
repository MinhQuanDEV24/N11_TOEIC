package com.toeic.nhom11.reading.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import java.sql.Statement;
import java.util.*;

/** Uses the team's existing practice_* and vocabulary tables. */
@Repository
public class ReadingDao {
    private final JdbcTemplate jdbc;
    public ReadingDao(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<Map<String,Object>> rows(String sql, Object... args) { return jdbc.queryForList(sql, args); }
    public int update(String sql, Object... args) { return jdbc.update(sql, args); }
    public long insert(String sql, Object... args) {
        var key = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (int i=0; i<args.length; i++) statement.setObject(i+1, args[i]);
            return statement;
        }, key);
        return Objects.requireNonNull(key.getKey()).longValue();
    }
    public Optional<Map<String,Object>> level(int level) {
        return rows("SELECT * FROM practice_sets WHERE skill='READING' AND part_no=5 AND is_published=TRUE AND instructions LIKE ? ORDER BY id LIMIT 1",
            "[READING_PART5_LEVEL:" + level + "]%").stream().findFirst();
    }
    public Optional<Map<String,Object>> part6Level(int level) {
        return rows("SELECT * FROM practice_sets WHERE skill='READING' AND part_no=6 AND is_published=TRUE AND instructions LIKE ? ORDER BY id LIMIT 1", "[READING_PART6_LEVEL:"+level+"]%").stream().findFirst();
    }
    public Optional<Map<String,Object>> part7Level(int level) {
        return rows("SELECT * FROM practice_sets WHERE skill='READING' AND part_no=7 AND is_published=TRUE AND instructions LIKE ? ORDER BY id LIMIT 1", "[READING_PART7_LEVEL:"+level+"]%").stream().findFirst();
    }
    public List<Map<String,Object>> questions(long set) {
        return rows("SELECT id,question_text,explanation,sort_order FROM practice_questions WHERE practice_set_id=? ORDER BY sort_order,id", set);
    }
    public List<Map<String,Object>> options(long question) {
        return rows("SELECT id,option_label,option_text,is_correct FROM practice_options WHERE question_id=? ORDER BY option_label", question);
    }
    public List<Map<String,Object>> vocabulary(List<String> words) {
        if (words.isEmpty()) return List.of();
        return rows("SELECT t.id,t.word,t.ipa,t.meaning_vi,t.definition_en,t.example_en,t.example_vi FROM vocabulary_terms t JOIN vocabulary_topics p ON p.id=t.topic_id WHERE p.title='N11 Reading Part 5' AND t.word IN ("
            + String.join(",", Collections.nCopies(words.size(), "?")) + ") ORDER BY t.id", words.toArray());
    }
}
