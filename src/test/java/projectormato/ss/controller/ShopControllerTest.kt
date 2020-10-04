package projectormato.ss.controller

import WithMockOAuth2User
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MvcResult
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
        val shop = Shop.builder().name("shop1").address("tokyo").hours("all time").userId("1").build()
        val otherUserShop = Shop.builder().name("shop1").address("tokyo").hours("all time").userId("2").build()
        shopRepository.saveAll(listOf(shop, otherUserShop))

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
        val shop1 = Shop.builder().name("shop1").address("tokyo").hours("all time").userId("1").build()
        val shop2 = Shop.builder().name("shop2").address("kanagawa").hours("all time").userId("1").build()
        val expectedShopList = listOf(shop1, shop2)
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
}
