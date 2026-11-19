package com.toeic.nhom11;

import com.toeic.nhom11.reading.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import javax.sql.DataSource;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties="spring.sql.init.data-locations=file:database/seed.sql,file:database/updates/002_quiz_modes.sql,file:database/updates/003_reading_part5.sql,file:database/updates/004_reading_part6.sql,file:database/updates/005_reading_part7.sql,file:database/updates/006_reading_expansion.sql")
@ActiveProfiles("test")
class ReadingExpansionTests {
    @Autowired ReadingService part5;
    @Autowired Part6Service part6;
    @Autowired Part7Service part7;
    @Autowired JdbcTemplate jdbc;
    @Autowired DataSource dataSource;
    static final String USER="student@n11.local";
    long n(Object value){return ((Number)value).longValue();}
    @SuppressWarnings("unchecked") List<Map<String,Object>> questions(Map<String,Object> state){return (List<Map<String,Object>>)state.get("questions");}
    @Test @Transactional void everyLevelHasNewQuestionsAndTranslatedContent(){
        for(int level=1;level<=5;level++){
            var p5=part5.state(part5.start(level,USER),USER);
            assertEquals(8,questions(p5).size());
            for(var q:questions(p5)){
                assertFalse(q.get("translation").toString().isBlank());
                assertFalse(((List<?>)q.get("vocabulary")).isEmpty());
            }
            var p6=part6.state(part6.start(level,USER),USER);
            var p7=part7.state(part7.start(level,USER),USER);
            assertEquals(8,questions(p6).size());
            assertEquals(new int[]{4,6,6,8,10}[level-1],questions(p7).size());
            assertEquals(2,((List<?>)p6.get("passages")).size());
            assertEquals(2,((List<?>)p7.get("passages")).size());
            for(var state:List.of(p6,p7)){
                for(Object p:(List<?>)state.get("passages")){
                    var passage=(Map<?,?>)p;
                    assertFalse(((Map<?,?>)passage.get("glossary")).isEmpty());
                    var documents=passage.containsKey("documents")?(List<?>)passage.get("documents"):List.of(passage);
                    for(Object d:documents)for(Object line:(List<?>)((Map<?,?>)d).get("lines")){
                        assertFalse(((Map<?,?>)line).get("en").toString().isBlank());
                        assertFalse(((Map<?,?>)line).get("vi").toString().isBlank());
                    }
                }
            }
        }
    }
    @Test @Transactional void secondPassageGradesIndependentlyAndPersistsFullScore(){
        long id=part7.start(5,USER);var qs=questions(part7.state(id,USER));
        for(int i=5;i<10;i++){
            long q=n(qs.get(i).get("id"));long option=jdbc.queryForObject("SELECT id FROM practice_options WHERE question_id=? AND is_correct=TRUE",Long.class,q);
            part7.answer(id,q,option,USER);
            if(i<9)assertFalse(questions(part7.state(id,USER)).get(5).containsKey("evidence"));
        }
        qs=questions(part7.state(id,USER));
        for(int i=0;i<5;i++)assertFalse(qs.get(i).containsKey("correct_option"));
        for(int i=5;i<10;i++){
            assertFalse(qs.get(i).get("evidence").toString().isBlank());
            assertEquals(4,((Map<?,?>)qs.get(i).get("analysis")).size());
        }
        for(int i=0;i<5;i++){
            long q=n(qs.get(i).get("id"));long option=jdbc.queryForObject("SELECT id FROM practice_options WHERE question_id=? AND is_correct=TRUE",Long.class,q);
            part7.answer(id,q,option,USER);
        }
        part7.finish(id,USER);assertEquals(100.0,part7.state(id,USER).get("score"));
    }
    @Test void expansionCanBeImportedAgainWithoutDuplicateRows(){
        var before=jdbc.queryForList("SELECT part_no,COUNT(q.id) AS n FROM practice_sets s JOIN practice_questions q ON q.practice_set_id=s.id GROUP BY part_no ORDER BY part_no");
        var populator=new ResourceDatabasePopulator();populator.setSqlScriptEncoding("UTF-8");
        populator.addScript(new FileSystemResource("database/updates/006_reading_expansion.sql"));populator.execute(dataSource);
        assertEquals(before,jdbc.queryForList("SELECT part_no,COUNT(q.id) AS n FROM practice_sets s JOIN practice_questions q ON q.practice_set_id=s.id GROUP BY part_no ORDER BY part_no"));
        assertEquals(114,jdbc.queryForObject("SELECT COUNT(*) FROM practice_questions",Integer.class));
        assertEquals(20,jdbc.queryForObject("SELECT COUNT(*) FROM practice_passages",Integer.class));
    }
}
