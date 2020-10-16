package projectormato.ss.controller

import com.google.maps.model.LatLng
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import projectormato.ss.entity.Shop
import projectormato.ss.oauth2.WithMockOAuth2User

@SpringBootTest
@WithMockOAuth2User
internal class ShareControllerTest : ControllerTestBase() {

    @Test
    fun お店共有ページにアクセスするとレスポンス200と想定したviewが返ること() {
        val mvcResult = mockMvc
                .perform(get("/share"))
                .andDo(print())
                .andExpect(status().isOk)
                .andReturn()

        assertEquals("share", mvcResult.modelAndView?.viewName)
    }

    @Test
    fun お店共有ページにアクセスすると自分の共有用URLが返ること() {
        val mvcResult = mockMvc
                .perform(get("/share"))
                .andDo(print())
                .andExpect(status().isOk)
                .andReturn()
        val url = mvcResult.modelAndView!!.model["url"]

        assertEquals(mvcResult.request.requestURL.toString() + "/" + userId, url)
    }

    @Test
    fun お店共有ページにアクセスすると共有中のemailリストが返ること() {
        userRepository.save(createUser(anotherUserId, anotherUserEmail))
        shareRepository.save(createShare(userId, anotherUserId))

        val mvcResult = mockMvc
                .perform(get("/share"))
                .andDo(print())
                .andExpect(status().isOk)
                .andReturn()
        val sharedEmailList = mvcResult.modelAndView!!.model["shareEmailList"]

        if (sharedEmailList is List<*>) {
            assertEquals(anotherUserEmail, sharedEmailList[0])
        }
    }

    @Test
    fun お店をshareするとshared情報が入ること() {
        // Given
        userRepository.save(createUser(anotherUserId, anotherUserEmail))

        mockMvc.perform(post("/share")
                .param("email", anotherUserEmail)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andDo(print())
                .andExpect(status().isFound)
        val sharedList = shareRepository.findAll()

        assertEquals(userId, sharedList[0].shareId)
        assertEquals(anotherUserId, sharedList[0].sharedId)
    }

    @Test
    fun お店をshareしてくれているユーザのお店一覧が見れること() {
        // Given
        val expectedShopList = setUpShare()

        // When
        val mvcResult = mockMvc
                .perform(get("/share/$anotherUserId"))
                .andDo(print())
                .andExpect(status().isOk)
                .andReturn()
        val shopList = mvcResult.modelAndView!!.model["shopList"]

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
    fun お店をshareしてくれているユーザのお店MAPが見れること() {
        // Given
        setUpShare()

        // When
        val mvcResult = mockMvc
                .perform(get("/share/$anotherUserId/maps"))
                .andDo(print())
                .andExpect(status().isOk)
                .andReturn()
        val locationList = mvcResult.modelAndView!!.model["locationList"]

        // Then
        if (locationList is List<*>) {
            assertEquals(2, locationList.size)
            locationList.forEach {
                if (it is LatLng?) {
                    // NOTE: doubleで比べると値が変わったりするのでintで比較する
                    assertEquals(35, it?.lat?.toInt())
                    assertEquals(139, it?.lng?.toInt())
                }
            }
        }
    }

    @Test
    fun お店をshareしてくれているユーザのお店詳細が見れること() {
        // Given
        setUpShare()
        val expectedShop = createShop(anotherUserId)
        shopRepository.save(expectedShop)

        //When
        val mvcResult = mockMvc
                .perform(get("/share/$anotherUserId/" + expectedShop.id))
                .andDo(print())
                .andExpect(status().isOk)
                .andReturn()
        val actualShop = mvcResult.modelAndView!!.model["shop"]

        // Then
        assertEquals("detail", mvcResult.modelAndView?.viewName)
        if (actualShop is Shop) {
            assertEquals(expectedShop.name, actualShop.name)
            assertEquals(expectedShop.address, actualShop.address)
            assertEquals(expectedShop.hours, actualShop.hours)
            assertEquals(expectedShop.userId, actualShop.userId)
        }
    }

    @Test
    fun お店をshareしてくれているユーザに新しいお店を投稿できること() {
        userRepository.save(createUser(anotherUserId, anotherUserEmail))
        shareRepository.save(createShare(anotherUserId, userId))

        mockMvc.perform(post("/share/$anotherUserId/shop")
                .param("url", "https://tabelog.com/tokyo/A1321/A132101/13137795/")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
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
    fun 自分が誰にリストを共有してもらっているか確認できること() {
        setUpShare()

        val mvcResult = mockMvc
                .perform(get("/shared"))
                .andDo(print())
                .andExpect(status().isOk)
                .andReturn()
        val sharedUrlList = mvcResult.modelAndView!!.model["sharedUrlList"]
        val sharedEmailList = mvcResult.modelAndView!!.model["sharedEmailList"]

        if (sharedUrlList is List<*>) {
            if (sharedUrlList[0] is String) {
                assertEquals("http://localhost/share/$anotherUserId", sharedUrlList[0])
            }
        }

        if (sharedEmailList is List<*>) {
            if (sharedEmailList[0] is String) {
                assertEquals(anotherUserEmail, sharedEmailList[0])
            }
        }
    }

    private fun setUpShare(): List<Shop> {
        userRepository.save(createUser(anotherUserId, anotherUserEmail))
        shareRepository.save(createShare(anotherUserId, userId))
        val expectedShopList = listOf(createShop(anotherUserId), createShop(anotherUserId))
        shopRepository.saveAll(expectedShopList)
        return expectedShopList
    }
}
