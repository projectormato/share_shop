package projectormato.ss.controller

import WithMockOAuth2User
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import projectormato.ss.entity.Shop
import java.util.*

@SpringBootTest
@WithMockOAuth2User
internal class ShopControllerTest : ControllerTestBase() {

    @Test
    fun お店一覧ページにアクセスするとレスポンス200と想定したviewが返ること() {
        val mvcResult = mockMvc
                .perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk)
                .andReturn()
        assertEquals("index", Objects.requireNonNull(mvcResult.modelAndView).viewName)
    }

    @Test
    fun お店一覧ページにアクセスするとお店一覧が返ること_要素1() {
        // Given
        val shop = createShop("1")
        shopRepository.saveAll(listOf(shop, createShop("2")))

        //When
        val mvcResult = mockMvc.
                perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk)
                .andReturn()
        val shopList = Objects.requireNonNull(mvcResult.modelAndView).model["shopList"]

        // Then
        assertEquals("index", Objects.requireNonNull(mvcResult.modelAndView).viewName)
        if (shopList is List<*>) {
            assertEquals(1, shopList.size)
            shopList.forEach {
                if (it is Shop) {
                    assertEquals(shop.name, it.name)
                    assertEquals(shop.address, it.address)
                    assertEquals(shop.hours, it.hours)
                    assertEquals(shop.userId, it.userId)
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
        val mvcResult = mockMvc
                .perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk)
                .andReturn()
        val shopList = Objects.requireNonNull(mvcResult.modelAndView).model["shopList"]

        // Then
        if (shopList is List<*>) {
            assertEquals(2, shopList.size)
            shopList.forEachIndexed { i, shop ->
                if (shop is Shop) {
                    assertEquals(expectedShopList[i].name, shop.name)
                    assertEquals(expectedShopList[i].address, shop.address)
                    assertEquals(expectedShopList[i].hours, shop.hours)
                    assertEquals(expectedShopList[i].userId, shop.userId)
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
        assertEquals("https://tabelog.com/tokyo/A1321/A132101/13137795/", shopList[0].url)
        assertEquals("ステーキガスト 落合南長崎店", shopList[0].name)
        assertEquals("東京都豊島区南長崎4-5-20 iTerrace落合南長崎 2F 大きな地図を見る 周辺のお店を探す", shopList[0].address)
        assertEquals("営業時間 [月～金] 11:00～24:00 [土・日・祝] 10:00～24:00 日曜営業 定休日 年中無休 ", shopList[0].hours)
    }

    @Test
    fun お店詳細ページでお店の情報が表示されること() {
        // Given
        val shop = createShop("1")
        shopRepository.saveAll(listOf(shop, createShop("2")))

        //When
        val mvcResult = mockMvc
                .perform(get("/shop/" + shop.id))
                .andDo(print())
                .andExpect(status().isOk)
                .andReturn()
        val actualShop = Objects.requireNonNull(mvcResult.modelAndView).model["shop"]

        // Then
        assertEquals("detail", Objects.requireNonNull(mvcResult.modelAndView).viewName)
        if (actualShop is Shop) {
            assertEquals(shop.name, actualShop.name)
            assertEquals(shop.address, actualShop.address)
            assertEquals(shop.hours, actualShop.hours)
            assertEquals(shop.userId, actualShop.userId)
        }
    }

    @Test
    fun 他人のお店詳細ページは表示されないこと() {
        // Given
        val shop = createShop("1")
        val otherShop = createShop("2")
        shopRepository.saveAll(listOf(shop, otherShop))

        //When
        val mvcResult = mockMvc.perform(get("/shop/" + otherShop.id))
                .andDo(print()).andExpect(status().is3xxRedirection)
                .andExpect(header().string("Location", "/"))
                .andReturn()

        // Then
        assertEquals("redirect:/", Objects.requireNonNull(mvcResult.modelAndView).viewName)
    }

    @Test
    fun お店を削除できること() {
        // Given 2件作成
        val shop = createShop("1")
        shopRepository.saveAll(listOf(shop, createShop("2")))

        //When
        mockMvc.perform(delete("/shop/" + shop.id))
                .andDo(print())
                .andExpect(status().isFound)
                .andExpect(header().string("Location", "/"))

        // Then 1件になる
        assertEquals(1, shopRepository.findAll().size)
    }

    @Test
    fun 他人のお店は削除できないこと() {
        // Given 2件作成
        val shop = createShop("1")
        val otherShop = createShop("2")
        shopRepository.saveAll(listOf(shop, otherShop))

        //When
        mockMvc.perform(delete("/shop/" + otherShop.id))
                .andDo(print())
                .andExpect(status().isFound)
                .andExpect(header().string("Location", "/"))

        // Then 2件ある
        assertEquals(2, shopRepository.findAll().size)
    }
}
