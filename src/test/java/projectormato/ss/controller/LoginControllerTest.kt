package projectormato.ss.controller

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
internal class LoginControllerTest : ControllerTestBase() {

    @Test
    fun loginページにアクセスしたらレスポンス200と想定したviewが返ること() {
        val mvcResult = mockMvc
                .perform(get("/login"))
                .andDo(print())
                .andExpect(status().isOk)
                .andReturn()

        Assertions.assertEquals("login", mvcResult.modelAndView?.viewName)
    }
}
