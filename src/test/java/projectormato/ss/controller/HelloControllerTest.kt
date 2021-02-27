package projectormato.ss.controller;

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
internal class HelloControllerTest : ControllerTestBase() {

    @Test
    fun helloページにアクセスしたらレスポンス200と想定したviewが返ること() {
        val mvcResult = mockMvc
                .perform(get("/hello"))
                .andExpect(status().isOk)
                .andReturn()

        assertEquals("hello", mvcResult.modelAndView?.viewName)
    }
}
