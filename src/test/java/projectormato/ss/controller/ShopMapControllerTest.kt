package projectormato.ss.controller

import com.google.maps.model.LatLng
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import projectormato.ss.entity.Shop
import projectormato.ss.oauth2.WithMockOAuth2User

@SpringBootTest
@WithMockOAuth2User
internal class ShopMapControllerTest() : ControllerTestBase() {

    @Test
    fun お店MAPページにアクセスするとレスポンス200と想定したviewが返ること() {
        val mvcResult = mockMvc
                .perform(get("/maps"))
                .andExpect(status().isOk)
                .andReturn()
        assertEquals("maps", mvcResult.modelAndView?.viewName)
    }

    @Test
    fun お店MAPページにアクセスするとお店情報と座標情報が返ること() {
        // Given
        val expectedShopList = listOf(createShop(userId), createShop(userId))
        shopRepository.saveAll(expectedShopList)

        // When
        val mvcResult = mockMvc
                .perform(get("/maps"))
                .andExpect(status().isOk)
                .andReturn()
        val shopList = mvcResult.modelAndView?.model?.get("shopList")
        val locationList = mvcResult.modelAndView?.model?.get("locationList")

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
}
