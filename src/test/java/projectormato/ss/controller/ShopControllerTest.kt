package projectormato.ss.controller

import WithMockOAuth2User
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import projectormato.ss.entity.Shop
import java.util.*

@SpringBootTest
@WithMockOAuth2User
internal class ShopControllerTest : ControllerTestBase() {

    @Test
    fun お店一覧ページにアクセスするとレスポンス200と想定したviewが返ること() {
        val mvcResult: MvcResult = mockMvc.perform(get("/"))
                .andDo(print()).andExpect(status().isOk).andReturn()
        assertEquals(Objects.requireNonNull(mvcResult.modelAndView).viewName, "index")
    }

    @Test
    fun お店一覧ページにアクセスするとお店一覧が返ること_要素1() {
        // Given
        val shop = createShop("1")
        shopRepository.saveAll(listOf(shop, createShop("2")))

        //When
        val mvcResult = mockMvc.perform(get("/"))
                .andDo(print()).andExpect(status().isOk).andReturn()
        val shopList = Objects.requireNonNull(mvcResult.modelAndView).model["shopList"]

        // Then
        assertEquals(Objects.requireNonNull(mvcResult.modelAndView).viewName, "index")
        if (shopList is List<*>) {
            assertEquals(shopList.size, 1)
            shopList.forEach {
                if (it is Shop) {
                    assertEquals(it.name, shop.name)
                    assertEquals(it.address, shop.address)
                    assertEquals(it.hours, shop.hours)
                    assertEquals(it.userId, shop.userId)
                }
            }
        }
    }

    @Test
    fun お店一覧ページにアクセスするとお店一覧が返ること_要素2() {
        // Given
        val expectedShopList = listOf(createShop("1"), createShop("1"))
        shopRepository.saveAll(expectedShopList)

        // When
        val mvcResult = mockMvc.perform(get("/"))
                .andDo(print()).andExpect(status().isOk).andReturn()
        val shopList = Objects.requireNonNull(mvcResult.modelAndView).model["shopList"]

        // Then
        if (shopList is List<*>) {
            assertEquals(shopList.size, 2)
            shopList.forEachIndexed { i, shop ->
                if (shop is Shop) {
                    assertEquals(shop.name, expectedShopList[i].name)
                    assertEquals(shop.address, expectedShopList[i].address)
                    assertEquals(shop.hours, expectedShopList[i].hours)
                    assertEquals(shop.userId, expectedShopList[i].userId)
                }
            }
        }
    }

    @Test
    fun URLを入れてお店が投稿できること() {
        mockMvc.perform(MockMvcRequestBuilders.post("/shop")
                .param("url", "https://tabelog.com/tokyo/A1321/A132101/13137795/")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isFound)
        val shopList = shopRepository.findAll()
        assertEquals(shopList[0].url, "https://tabelog.com/tokyo/A1321/A132101/13137795/")
    }

    private fun createShop(userId: String) = Shop.builder().url("https://tabelog.com/tokyo/A1321/A132101/13137795/").name("shop1").address("tokyo").hours("all time").userId(userId).build()
}
