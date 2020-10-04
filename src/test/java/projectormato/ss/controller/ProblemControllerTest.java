package projectormato.ss.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;
import projectormato.ss.entity.Choice;
import projectormato.ss.entity.Problem;
import projectormato.ss.entity.Question;
import projectormato.ss.form.AnswerForm;
import projectormato.ss.repository.ProblemRepository;
import projectormato.ss.repository.QuestionRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class ProblemControllerTest extends ControllerTestBase {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    ProblemRepository problemRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Test
    void タイトルと概要を入れて問題が作成できること() throws Exception {
        this.mockMvc.perform(post("/problem")
                .param("title", "Problem Title")
                .param("description", "Problem Description").with(csrf()))
                .andDo(print())
                .andExpect(status().isFound());
        final var problemList = problemRepository.findAll();
        assertEquals(problemList.get(0).getTitle(), "Problem Title");
        assertEquals(problemList.get(0).getDescription(), "Problem Description");
    }

    @Test
    void 問題詳細ページにアクセスすると設問一覧が返ること() throws Exception {
        final var problem = Problem.builder().title("problem").description("this is Problem").build();
        problemRepository.save(problem);
        final var question1 = Question.builder().statement("パンはパンでも食べられないパンはなーんだ？").problem(problem).build();
        final var question2 = Question.builder().statement("this is question").problem(problem).build();
        final var expectedQuestionList = List.of(question1, question2);
        questionRepository.saveAll(expectedQuestionList);

        final var mvcResult = this.mockMvc.perform(get("/problem/" + problem.getId()))
                .andDo(print()).andExpect(status().isOk()).andReturn();
        final var questionList = (List<Question>) Objects.requireNonNull(mvcResult.getModelAndView()).getModel().get("questionList");

        assertEquals(Objects.requireNonNull(mvcResult.getModelAndView()).getViewName(), "problem");
        for (int i = 0; i < questionList.size(); i++) {
            assertEquals(questionList.get(i).getStatement(), expectedQuestionList.get(i).getStatement());
        }
    }

    @Test
    void 問題を削除できること() throws Exception {
        final var problem = Problem.builder().title("problem").description("this is Problem").build();
        problemRepository.save(problem);
        final var question = Question.builder().statement("パンはパンでも食べられないパンはなーんだ？").problem(problem).build();
        questionRepository.save(question);
        final var choice = Choice.builder().content("フライパン").correctFlag(true).question(question).build();
        choiceRepository.save(choice);
        assertEquals(problemRepository.findAll().size(), 1);

        mockMvc.perform(delete("/problem/" + problem.getId()).with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/"));

        assertTrue(problemRepository.findAll().isEmpty());
    }

    @Test
    void 問題を編集できること() throws Exception {
        final var beforeProblem = Problem.builder().title("Problem Title").description("Problem Description").build();
        problemRepository.save(beforeProblem);

        mockMvc.perform(put("/problem/" + beforeProblem.getId())
                .param("title", "Changed Problem Title")
                .param("description", "Changed Problem Description").with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/problem/" + beforeProblem.getId()));

        final var afterProblem = problemRepository.findById(beforeProblem.getId()).get();
        assertEquals(afterProblem.getTitle(), "Changed Problem Title");
        assertEquals(afterProblem.getDescription(), "Changed Problem Description");
    }

    @Test
    void 問題解答ページにアクセスすると最初の問題が表示されること() throws Exception {
        final var problem = Problem.builder().title("Problem Title").description("Problem Description").build();
        problemRepository.save(problem);
        final var question = Question.builder().statement("パンはパンでも食べられないパンはなーんだ？").problem(problem).build();
        questionRepository.save(question);
        final var choice = Choice.builder().content("フライパン").correctFlag(true).question(question).build();
        choiceRepository.save(choice);

        final MvcResult mvcResult = this.mockMvc.perform(get("/problem/" + problem.getId() + "/answer"))
                .andDo(print()).andExpect(status().isOk()).andReturn();
        final var answerForm = (AnswerForm) Objects.requireNonNull(mvcResult.getModelAndView()).getModel().get("answerForm");

        assertEquals(question.getId(), answerForm.getQuestionId());
        assertEquals(question.getStatement(), answerForm.getQuestionStatement());
        assertEquals(0, answerForm.getNumberOfCorrectAnswers());
    }

    @Test
    void 問題解答ページで正解の選択肢を選ぶと次の問題が表示され正答数が加算されていること() throws Exception {
        final var problem = Problem.builder().title("Problem Title").description("Problem Description").build();
        problemRepository.save(problem);
        final var question1 = Question.builder().statement("パンはパンでも食べられないパンはなーんだ？").problem(problem).build();
        final var question2 = Question.builder().statement("This is Question").problem(problem).build();
        questionRepository.saveAll(Arrays.asList(question1, question2));
        final var choice1_1 = Choice.builder().content("フライパン").correctFlag(true).question(question1).build();
        final var choice1_2 = Choice.builder().content("食パン").correctFlag(false).question(question1).build();
        final var choice2_1 = Choice.builder().content("correct").correctFlag(true).question(question2).build();
        final var choice2_2 = Choice.builder().content("wrong").correctFlag(false).question(question2).build();
        choiceRepository.saveAll(Arrays.asList(choice1_1, choice1_2, choice2_1, choice2_2));

        final MvcResult mvcResult = this.mockMvc.perform(get("/problem/" + problem.getId() + "/answer")
                .param("questionId", String.valueOf(question1.getId()))
                .param("questionStatement", String.valueOf(question1.getStatement()))
                .param("answeredChoiceId", String.valueOf(choice1_1.getId())))
                .andDo(print()).andExpect(status().isOk()).andReturn();
        final var answerForm = (AnswerForm) Objects.requireNonNull(mvcResult.getModelAndView()).getModel().get("answerForm");

        assertEquals(question2.getId(), answerForm.getQuestionId());
        assertEquals(question2.getStatement(), answerForm.getQuestionStatement());
        assertEquals(1, answerForm.getNumberOfCorrectAnswers());
    }

    @Test
    void 問題解答ページで不正解の選択肢を選ぶと次の問題が表示され正答数が加算されないこと() throws Exception {
        Problem problem = Problem.builder().title("Problem Title").description("Problem Description").build();
        problemRepository.save(problem);
        Question question1 = Question.builder().statement("パンはパンでも食べられないパンはなーんだ？").problem(problem).build();
        Question question2 = Question.builder().statement("This is Question").problem(problem).build();
        questionRepository.saveAll(Arrays.asList(question1, question2));
        Choice choice1_1 = Choice.builder().content("フライパン").correctFlag(true).question(question1).build();
        Choice choice1_2 = Choice.builder().content("食パン").correctFlag(false).question(question1).build();
        Choice choice2_1 = Choice.builder().content("correct").correctFlag(true).question(question2).build();
        Choice choice2_2 = Choice.builder().content("wrong").correctFlag(false).question(question2).build();
        choiceRepository.saveAll(Arrays.asList(choice1_1, choice1_2, choice2_1, choice2_2));

        final MvcResult mvcResult = this.mockMvc.perform(get("/problem/" + problem.getId() + "/answer")
                .param("questionId", String.valueOf(question1.getId()))
                .param("questionStatement", String.valueOf(question1.getStatement()))
                .param("answeredChoiceId", String.valueOf(choice1_2.getId())))
                .andDo(print()).andExpect(status().isOk()).andReturn();
        AnswerForm answerForm = (AnswerForm) Objects.requireNonNull(mvcResult.getModelAndView()).getModel().get("answerForm");

        assertEquals(question2.getId(), answerForm.getQuestionId());
        assertEquals(question2.getStatement(), answerForm.getQuestionStatement());
        assertEquals(0, answerForm.getNumberOfCorrectAnswers());
    }

    @Test
    void 問題解答ページで最後の問題に解答すると最終的な正答数が表示されること() throws Exception {
        Problem problem = Problem.builder().title("Problem Title").description("Problem Description").build();
        problemRepository.save(problem);
        Question question1 = Question.builder().statement("パンはパンでも食べられないパンはなーんだ？").problem(problem).build();
        Question question2 = Question.builder().statement("This is Question").problem(problem).build();
        questionRepository.saveAll(Arrays.asList(question1, question2));
        Choice choice1_1 = Choice.builder().content("フライパン").correctFlag(true).question(question1).build();
        Choice choice1_2 = Choice.builder().content("食パン").correctFlag(false).question(question1).build();
        Choice choice2_1 = Choice.builder().content("correct").correctFlag(true).question(question2).build();
        Choice choice2_2 = Choice.builder().content("wrong").correctFlag(false).question(question2).build();
        choiceRepository.saveAll(Arrays.asList(choice1_1, choice1_2, choice2_1, choice2_2));

        final MvcResult mvcResult = this.mockMvc.perform(get("/problem/" + problem.getId() + "/answer")
                .param("questionId", String.valueOf(question2.getId()))
                .param("questionStatement", String.valueOf(question2.getStatement()))
                .param("answeredChoiceId", String.valueOf(choice2_1.getId()))
                .param("numberOfCorrectAnswers", "1"))
                .andDo(print()).andExpect(status().isOk()).andReturn();
        AnswerForm answerForm = (AnswerForm) Objects.requireNonNull(mvcResult.getModelAndView()).getModel().get("answerForm");
        Boolean finishFlag = (Boolean) Objects.requireNonNull(mvcResult.getModelAndView()).getModel().get("finishFlag");

        assertTrue(finishFlag);
        assertNull(answerForm.getQuestionId());
        assertNull(answerForm.getQuestionStatement());
        assertEquals(2, answerForm.getNumberOfCorrectAnswers());
    }
}
