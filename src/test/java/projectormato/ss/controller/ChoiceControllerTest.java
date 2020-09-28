package projectormato.ss.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import projectormato.ss.entity.Choice;
import projectormato.ss.entity.Problem;
import projectormato.ss.entity.Question;
import projectormato.ss.repository.ChoiceRepository;
import projectormato.ss.repository.ProblemRepository;
import projectormato.ss.repository.QuestionRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ChoiceControllerTest extends ControllerTestBase {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    ProblemRepository problemRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    ChoiceRepository choiceRepository;

    @Test
    void 内容と正誤を入れて選択肢が作成できること() throws Exception {
        final var problem = Problem.builder().title("problem").description("this is Problem").build();
        problemRepository.save(problem);
        final var question = Question.builder().statement("パンはパンでも食べられないパンはなーんだ？").problem(problem).build();
        questionRepository.save(question);

        this.mockMvc.perform(post("/problem/" + problem.getId() + "/question/" + question.getId() + "/choice")
                .param("content", "フライパン")
                .param("correctFlag", "true"))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/problem/" + problem.getId()));

        final var choiceList = choiceRepository.findChoicesByQuestion(question);
        assertEquals(choiceList.get(0).getContent(), "フライパン");
        assertEquals(choiceList.get(0).getCorrectFlag(), true);
    }

    @Test
    void 選択肢がないとき不正解の選択肢が作成できないこと() throws Exception {
        final var problem = Problem.builder().title("problem").description("this is Problem").build();
        problemRepository.save(problem);
        final var question = Question.builder().statement("パンはパンでも食べられないパンはなーんだ？").problem(problem).build();
        questionRepository.save(question);

        this.mockMvc.perform(post("/problem/" + problem.getId() + "/question/" + question.getId() + "/choice")
                .param("content", "フライパン")
                .param("correctFlag", "false"))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/problem/" + problem.getId()));

        final var choiceList = choiceRepository.findChoicesByQuestion(question);
        assertTrue(choiceList.isEmpty());
    }

    @Test
    void 正解の選択肢がすでにある時正解の選択肢を作成できないこと() throws Exception {
        final var problem = Problem.builder().title("problem").description("this is Problem").build();
        problemRepository.save(problem);
        final var question = Question.builder().statement("パンはパンでも食べられないパンはなーんだ？").problem(problem).build();
        questionRepository.save(question);
        final var choice = Choice.builder().content("フライパン").correctFlag(true).question(question).build();
        choiceRepository.save(choice);

        this.mockMvc.perform(post("/problem/" + problem.getId() + "/question/" + question.getId() + "/choice")
                .param("content", "フライパン2")
                .param("correctFlag", "true"))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/problem/" + problem.getId()));

        final var choiceList = choiceRepository.findChoicesByQuestion(question);
        assertEquals(choiceList.size(), 1);
        assertEquals(choiceList.get(0).getContent(), "フライパン");
        assertEquals(choiceList.get(0).getCorrectFlag(), true);
    }
}
