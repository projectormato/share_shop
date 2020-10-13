package projectormato.ss.controller

import WithMockOAuth2User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.*

@SpringBootTest
@WithMockOAuth2User
internal class ShareControllerTest : ControllerTestBase() {

    @Test
    fun お店共有ページにアクセスするとレスポンス200と想定したviewが返ること() {
        val mvcResult = mockMvc
                .perform(MockMvcRequestBuilders.get("/share"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()
        assertEquals("share", Objects.requireNonNull(mvcResult.modelAndView).viewName)
    }
}
