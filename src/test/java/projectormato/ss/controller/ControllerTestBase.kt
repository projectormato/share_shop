package projectormato.ss.controller;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import projectormato.ss.repository.ChoiceRepository;
import projectormato.ss.repository.ProblemRepository;
import projectormato.ss.repository.QuestionRepository;
import projectormato.ss.repository.ShopRepository

@SpringBootTest
@AutoConfigureMockMvc
open class ControllerTestBase {
    protected lateinit var mockMvc: MockMvc

    // TODO: Autowiredなくしたい by tomato
    @Autowired
    protected lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    protected lateinit var shopRepository: ShopRepository

    @Autowired
    protected lateinit var problemRepository: ProblemRepository

    @Autowired
    protected lateinit var questionRepository: QuestionRepository

    @Autowired
    protected lateinit var choiceRepository: ChoiceRepository

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
        shopRepository.deleteAll()
        choiceRepository.deleteAll()
        questionRepository.deleteAll()
        problemRepository.deleteAll()
    }

}
