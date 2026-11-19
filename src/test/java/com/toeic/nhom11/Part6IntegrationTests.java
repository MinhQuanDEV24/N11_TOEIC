package com.toeic.nhom11;

import com.toeic.nhom11.reading.service.Part6Service;
import com.toeic.nhom11.reading.service.ReadingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.test.context.support.WithMockUser;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test") @Transactional
class Part6IntegrationTests {
    @Autowired Part6Service service;
    @Autowired ReadingService part5;
    @Autowired JdbcTemplate jdbc;
    @Autowired MockMvc mvc;
    static final String USER="student@n11.local";
    long n(Object x){return ((Number)x).longValue();}
    @SuppressWarnings("unchecked") List<Map<String,Object>> questions(long id){return (List<Map<String,Object>>)service.state(id,USER).get("questions");}
    long option(long q,boolean correct){return jdbc.queryForObject("SELECT MIN(id) FROM practice_options WHERE question_id=? AND is_correct=?",Long.class,q,correct);}
    @Test void fiveLevelsHaveBilingualPassagesAndFourQuestions(){
        for(int level=1;level<=5;level++){
            long id=service.start(level,USER);assertEquals(4,questions(id).size());
            assertEquals(1,n(service.level(level,USER).get("passages")));
            assertTrue(service.state(id,USER).get("passages").toString().contains("glossary"));
            assertTrue(service.state(id,USER).get("passages").toString().contains("lines=[{en="));
            assertTrue(questions(id).stream().noneMatch(q->q.containsKey("correct_option")||q.containsKey("explanation")));
        }
    }
    @Test void noFeedbackOrScoreBeforeLastAnswerAndAllFeedbackAfterLast(){
        long id=service.start(1,USER);var qs=questions(id);
        for(int i=0;i<3;i++){
            long q=n(qs.get(i).get("id"));service.answer(id,q,option(q,true),USER);
            assertTrue(questions(id).stream().noneMatch(item->item.containsKey("correct_option")||item.containsKey("explanation")));
            assertEquals(0,n(service.state(id,USER).get("correct")));
        }
        long last=n(qs.get(3).get("id"));service.answer(id,last,option(last,false),USER);
        assertTrue(questions(id).stream().allMatch(q->q.containsKey("correct_option")&&q.containsKey("explanation")));
        assertEquals(3,n(service.state(id,USER).get("correct")));
        service.finish(id,USER);assertEquals(75.0,service.state(id,USER).get("score"));
    }
    @Test void canChangeBeforeCompleteButNotAfterReview(){
        long id=service.start(1,USER),q=n(questions(id).get(0).get("id"));
        service.answer(id,q,option(q,false),USER);service.answer(id,q,option(q,true),USER);
        assertEquals(option(q,true),n(questions(id).get(0).get("selected")));
        for(var question:questions(id)){long qid=n(question.get("id"));service.answer(id,qid,option(qid,true),USER);}
        service.answer(id,q,option(q,true),USER);
        assertThrows(ResponseStatusException.class,()->service.answer(id,q,option(q,false),USER));
    }
    @Test void cannotRevealPartialPassageByFinishing(){
        long id=service.start(1,USER),q=n(questions(id).get(0).get("id"));service.answer(id,q,option(q,true),USER);
        assertThrows(ResponseStatusException.class,()->service.finish(id,USER));
        assertEquals(false,service.state(id,USER).get("finished"));assertFalse(questions(id).get(0).containsKey("correct_option"));
        assertEquals(id,service.start(1,USER));assertEquals(option(q,true),n(questions(id).get(0).get("selected")));
    }
    @Test void passageFeedbackIsIndependentOfOtherPassages(){
        long set=jdbc.queryForObject("SELECT MIN(id) FROM practice_sets WHERE part_no=6",Long.class);
        jdbc.update("INSERT INTO practice_passages(practice_set_id,passage_text,sort_order) SELECT ?,passage_text,2 FROM practice_passages WHERE practice_set_id=? AND sort_order=1",set,set);
        long passage=jdbc.queryForObject("SELECT id FROM practice_passages WHERE practice_set_id=? AND sort_order=2",Long.class,set);
        var original=jdbc.queryForList("SELECT id FROM practice_questions WHERE practice_set_id=? ORDER BY id",set);
        for(var row:original){
            jdbc.update("INSERT INTO practice_questions(practice_set_id,passage_id,question_text,explanation,sort_order) SELECT practice_set_id,?,question_text,explanation,sort_order+4 FROM practice_questions WHERE id=?",passage,row.get("id"));
            long qid=jdbc.queryForObject("SELECT MAX(id) FROM practice_questions",Long.class);
            jdbc.update("INSERT INTO practice_options(question_id,option_label,option_text,is_correct) SELECT ?,option_label,option_text,is_correct FROM practice_options WHERE question_id=?",qid,row.get("id"));
        }
        long id=service.start(1,USER);var qs=questions(id);assertEquals(8,qs.size());
        for(int i=0;i<4;i++){long q=n(qs.get(i).get("id"));service.answer(id,q,option(q,true),USER);}
        assertTrue(questions(id).subList(0,4).stream().allMatch(q->q.containsKey("correct_option")));
        assertTrue(questions(id).subList(4,8).stream().noneMatch(q->q.containsKey("correct_option")));
    }
    @Test void ownerAndPartAndOptionsAreValidated(){
        long id=service.start(1,USER),other=service.start(2,USER),q=n(questions(id).get(0).get("id")),foreign=n(questions(other).get(0).get("id"));
        assertThrows(ResponseStatusException.class,()->service.state(id,"admin@n11.local"));
        assertThrows(ResponseStatusException.class,()->service.answer(id,q,option(foreign,true),USER));
        assertThrows(ResponseStatusException.class,()->service.answer(id,foreign,option(foreign,true),USER));
        assertThrows(ResponseStatusException.class,()->part5.state(id,USER));
        long p5=part5.start(1,USER);assertThrows(ResponseStatusException.class,()->service.state(p5,USER));
    }
    @Test @WithMockUser(username=USER,roles="STUDENT") void mvcRoutesAndCsrf() throws Exception {
        mvc.perform(get("/reading/part6")).andExpect(status().isOk()).andExpect(view().name("reading/part6-index"));
        mvc.perform(get("/reading/part6/2")).andExpect(status().isOk()).andExpect(view().name("reading/part6-intro"));
        mvc.perform(post("/reading/part6/1/start")).andExpect(status().isForbidden());
        long id=service.start(1,USER);
        mvc.perform(get("/reading/part6/attempts/"+id)).andExpect(status().isOk());
        mvc.perform(get("/reading/api/part6/attempts/"+id)).andExpect(status().isOk()).andExpect(jsonPath("$.questions[0].correct_option").doesNotExist());
        mvc.perform(post("/reading/api/part6/attempts/"+id+"/finish").with(csrf())).andExpect(status().isBadRequest());
    }
    @Test void guestMustLoginAndInvalidLevelsAreRejected() throws Exception {
        mvc.perform(get("/reading/part6/1/ready")).andExpect(status().is3xxRedirection());
        mvc.perform(get("/reading/part6/attempts/1")).andExpect(status().is3xxRedirection());
        mvc.perform(get("/reading/part6/9")).andExpect(status().isNotFound());
    }
}
