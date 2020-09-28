package work.ttdd.controller;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import work.ttdd.repository.ChoiceRepository;
import work.ttdd.repository.ProblemRepository;
import work.ttdd.repository.QuestionRepository;

@SpringBootTest
@AutoConfigureMockMvc
class ControllerTestBase {
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

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        choiceRepository.deleteAll();
        questionRepository.deleteAll();
        problemRepository.deleteAll();
    }

}