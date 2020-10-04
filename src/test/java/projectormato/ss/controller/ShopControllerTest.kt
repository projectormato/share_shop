package projectormato.ss.controller

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import projectormato.ss.entity.Shop
import java.util.*
import kotlin.jvm.Throws

internal class ShopControllerTest : ControllerTestBase() {

    @Test
    @Throws(Exception::class)
    fun お店一覧ページにアクセスするとレスポンス200と想定したviewが返ること() {
        val mvcResult: MvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk).andReturn()
        assertEquals(Objects.requireNonNull(mvcResult.modelAndView).viewName, "index")
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun お店一覧ページにアクセスするとお店一覧が返ること_要素1() {
        val shop = Shop.builder().name("shop1").address("tokyo").hours("all time").userId(1).build()
        shopRepository.save(shop)
        val mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk).andReturn()
        val shopList = Objects.requireNonNull(mvcResult.modelAndView).model["shopList"]
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
    @Throws(java.lang.Exception::class)
    fun お店一覧ページにアクセスするとお店一覧が返ること_要素2() {
        val shop1 = Shop.builder().name("shop1").address("tokyo").hours("all time").userId(1).build()
        val shop2 = Shop.builder().name("shop2").address("kanagawa").hours("all time").userId(1).build()
        val expectedShopList = listOf(shop1, shop2)
        shopRepository.saveAll(expectedShopList)
        val mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk).andReturn()
        val shopList = Objects.requireNonNull(mvcResult.modelAndView).model["shopList"]
        if (shopList is List<*>) {
            assertEquals(shopList.size, 2)
            shopList.forEachIndexed {i, shop ->
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
