package projectormato.ss.controller

import WithMockOAuth2User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import projectormato.ss.entity.Share
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

    @Test
    fun お店共有ページにアクセスすると自分の共有用URLが返ること() {
        val mvcResult = mockMvc
                .perform(MockMvcRequestBuilders.get("/share"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()
        val url = Objects.requireNonNull(mvcResult.modelAndView).model["url"]

        // Then
        assertEquals(mvcResult.request.requestURL.toString() + "/" + userId, url)
    }

    @Test
    fun お店共有ページにアクセスすると共有中のemailリストが返ること() {
        userRepository.save(createUser(anotherUserId, anotherUserEmail))
        shareRepository.save(Share.builder().shareId(userId).sharedId(anotherUserId).build())

        val mvcResult = mockMvc
                .perform(MockMvcRequestBuilders.get("/share"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()
        val sharedEmailList = Objects.requireNonNull(mvcResult.modelAndView).model["sharedEmailList"]

        // Then
        if (sharedEmailList is List<*>) {
            assertEquals(anotherUserEmail, sharedEmailList[0])
        }
    }

    @Test
    fun お店をshareするとshared情報が入ること() {
        // Given
        userRepository.save(createUser(anotherUserId, anotherUserEmail))

        mockMvc.perform(MockMvcRequestBuilders.post("/share")
                .param("email", anotherUserEmail)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isFound)
        val sharedList = shareRepository.findAll()

        assertEquals(userId, sharedList[0].shareId)
        assertEquals(anotherUserId, sharedList[0].sharedId)
    }
}
