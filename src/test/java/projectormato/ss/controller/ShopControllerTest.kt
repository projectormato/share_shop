package projectormato.ss.controller

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import projectormato.ss.entity.Shop
import projectormato.ss.oauth2.WithMockOAuth2User

@SpringBootTest
@WithMockOAuth2User
internal class ShopControllerTest : ControllerTestBase() {

    @Test
    fun お店一覧ページにアクセスするとレスポンス200と想定したviewが返ること() {
        val mvcResult = mockMvc
            .perform(get("/"))
            .andExpect(status().isOk)
            .andReturn()
        assertEquals("index", mvcResult.modelAndView?.viewName)
    }

    @Test
    fun お店一覧ページにアクセスするとお店一覧が返ること_要素1() {
        // Given
        val shop = createShop(userId)
        shopRepository.saveAll(listOf(shop, createShop(anotherUserId)))

        //When
        val mvcResult = mockMvc.perform(get("/"))
            .andExpect(status().isOk)
            .andReturn()
        val shopList = mvcResult.modelAndView?.model?.get("shopList")

        // Then
        assertEquals("index", mvcResult.modelAndView?.viewName)
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
        val expectedShopList = listOf(createShop(userId), createShop(userId))
        shopRepository.saveAll(expectedShopList)

        // When
        val mvcResult = mockMvc
            .perform(get("/"))
            .andExpect(status().isOk)
            .andReturn()
        val shopList = mvcResult.modelAndView?.model?.get("shopList")

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
        mockMvc.perform(
            MockMvcRequestBuilders.post("/shop")
                .param("url", "https://tabelog.com/tokyo/A1321/A132101/13137795/")
                .with(csrf())
        )
            .andExpect(status().isFound)
        val shopList = shopRepository.findAll()

        // NOTE: 食べログのサイトの情報をベタで持ってきてテストしている。スクレイピングのもっと良い方法あるかな
        assertEquals("https://tabelog.com/tokyo/A1321/A132101/13137795/", shopList[0].url)
        assertEquals("ステーキガスト 落合南長崎店", shopList[0].name)
        assertEquals("東京都豊島区南長崎4-5-20 iTerrace落合南長崎 2F 大きな地図を見る 周辺のお店を探す", shopList[0].address)
        assertEquals("営業時間 [月～金] 11:00～24:00 [土・日・祝] 10:00～24:00 日曜営業 定休日 年中無休 ", shopList[0].hours)
    }

    @Test
    fun 対応していないURLをshareするとお店が作成されないこと() {
        val mvcResult = mockMvc.perform(
            MockMvcRequestBuilders.post("/shop")
                .param("url", "https://google.com")
                .with(csrf())
        )
            .andExpect(status().isOk)
            .andReturn()

        assertEquals("index", mvcResult.modelAndView?.viewName)

        val shopList = shopRepository.findAll()
        assertEquals(0, shopList.size)
    }

    @Test
    fun お店詳細ページでお店の情報が表示されること() {
        // Given
        val shop = createShop(userId)
        shopRepository.saveAll(listOf(shop, createShop(anotherUserId)))

        //When
        val mvcResult = mockMvc
            .perform(get("/shop/" + shop.id))
            .andExpect(status().isOk)
            .andReturn()
        val actualShop = mvcResult.modelAndView?.model?.get("shop")

        // Then
        assertEquals("detail", mvcResult.modelAndView?.viewName)
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
        val shop = createShop(userId)
        val otherShop = createShop(anotherUserId)
        shopRepository.saveAll(listOf(shop, otherShop))

        //When
        val mvcResult = mockMvc.perform(get("/shop/" + otherShop.id))
            .andExpect(status().is3xxRedirection)
            .andExpect(header().string("Location", "/"))
            .andReturn()

        // Then
        assertEquals("redirect:/", mvcResult.modelAndView?.viewName)
    }

    @Test
    fun お店を削除できること() {
        // Given 2件作成
        val shop = createShop(userId)
        shopRepository.saveAll(listOf(shop, createShop(anotherUserId)))

        //When
        mockMvc.perform(delete("/shop/" + shop.id))
            .andExpect(status().isFound)
            .andExpect(header().string("Location", "/"))

        // Then 1件になる
        assertEquals(1, shopRepository.findAll().size)
    }

    @Test
    fun 他人のお店は削除できないこと() {
        // Given 2件作成
        val shop = createShop(userId)
        val otherShop = createShop(anotherUserId)
        shopRepository.saveAll(listOf(shop, otherShop))

        //When
        mockMvc.perform(delete("/shop/" + otherShop.id))
            .andExpect(status().isFound)
            .andExpect(header().string("Location", "/"))

        // Then 2件ある
        assertEquals(2, shopRepository.findAll().size)
    }

    @Test
    fun お店一覧ページにアクセスするとユーザー情報が格納されること() {
        mockMvc.perform(get("/")).andExpect(status().isOk)

        val user = userRepository.findFirstByUserId(userId)
        assertEquals(userId, user?.userId)
        assertEquals(userEmail, user?.email)
    }
}
