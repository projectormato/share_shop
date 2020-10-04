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
    fun URLをshareするとお店情報を取得してお店が作成されること() {
        mockMvc.perform(MockMvcRequestBuilders.post("/shop")
                .param("url", "https://tabelog.com/tokyo/A1321/A132101/13137795/")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isFound)
        val shopList = shopRepository.findAll()

        // NOTE: 食べログのサイトの情報をベタで持ってきてテストしている。スクレイピングのもっと良い方法あるかな
        assertEquals(shopList[0].url, "https://tabelog.com/tokyo/A1321/A132101/13137795/")
        assertEquals(shopList[0].name, "ステーキガスト 落合南長崎店")
        assertEquals(shopList[0].address, "東京都豊島区南長崎4-5-20 iTerrace落合南長崎 2F 大きな地図を見る 周辺のお店を探す")
        assertEquals(shopList[0].hours, "営業時間 [月～金] 11:00～24:00 [土・日・祝] 10:00～24:00 日曜営業 定休日 年中無休 ")
    }

    private fun createShop(userId: String) = Shop.builder().url("https://tabelog.com/tokyo/A1321/A132101/13137795/").name("shop1").address("tokyo").hours("all time").userId(userId).build()
}
