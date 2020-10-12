package projectormato.ss.controller

import WithMockOAuth2User
import com.google.maps.model.LatLng
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import projectormato.ss.entity.Shop
import java.util.*

@SpringBootTest
@WithMockOAuth2User
internal class ShopMapControllerTest() : ControllerTestBase() {

    @Test
    fun お店MAPページにアクセスするとレスポンス200と想定したviewが返ること() {
        val mvcResult = mockMvc
                .perform(MockMvcRequestBuilders.get("/maps"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()
        assertEquals("maps", Objects.requireNonNull(mvcResult.modelAndView).viewName)
    }

    @Test
    fun お店MAPページにアクセスするとお店情報と座標情報が返ること() {
        // Given
        val expectedShopList = listOf(createShop("1"), createShop("1"))
        shopRepository.saveAll(expectedShopList)

        // When
        val mvcResult = mockMvc
                .perform(MockMvcRequestBuilders.get("/maps"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()
        val shopList = Objects.requireNonNull(mvcResult.modelAndView).model["shopList"]
        val locationList = Objects.requireNonNull(mvcResult.modelAndView).model["locationList"]

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
                    assertEquals(35.6761919, it?.lat)
                    assertEquals(139.6503106, it?.lng)
                }
            }
        }
    }
}
