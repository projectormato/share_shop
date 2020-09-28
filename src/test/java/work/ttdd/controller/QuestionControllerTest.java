package work.ttdd.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import work.ttdd.entity.Problem;
import work.ttdd.entity.Question;
import work.ttdd.repository.ProblemRepository;
import work.ttdd.repository.QuestionRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class QuestionControllerTest extends ControllerTestBase {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    ProblemRepository problemRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Test
    void statementを入れて設問が作成できること() throws Exception {
        final var problem = Problem.builder().title("problem").description("this is Problem").build();
        problemRepository.save(problem);
        this.mockMvc.perform(post("/problem/" + problem.getId() + "/question")
                .param("statement", "パンはパンでも食べられないパンはなーんだ？"))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/problem/" + problem.getId()));
        final var questionList = questionRepository.findQuestionsByProblem(problem);
        assertEquals(questionList.get(0).getStatement(), "パンはパンでも食べられないパンはなーんだ？");
    }

}