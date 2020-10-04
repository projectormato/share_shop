package projectormato.ss.controller

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.util.*
import kotlin.jvm.Throws

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
internal class LoginControllerTest : ControllerTestBase() {

    @Test
    @Throws(Exception::class)
    fun loginページにアクセスしたらレスポンス200と想定したviewが返ること() {
        val mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/login"))
                .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk).andReturn()
        Assertions.assertEquals(Objects.requireNonNull(mvcResult.modelAndView).viewName, "login")
    }
}
