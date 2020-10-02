package projectormato.ss.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import projectormato.ss.entity.Problem;
import projectormato.ss.repository.ProblemRepository;
import projectormato.ss.repository.QuestionRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
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
                .param("statement", "パンはパンでも食べられないパンはなーんだ？").with(csrf()))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/problem/" + problem.getId()));
        final var questionList = questionRepository.findQuestionsByProblem(problem);
        assertEquals(questionList.get(0).getStatement(), "パンはパンでも食べられないパンはなーんだ？");
    }

}
