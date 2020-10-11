package projectormato.ss.service

import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.model.LatLng
import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import projectormato.ss.entity.Shop
import projectormato.ss.repository.ShopRepository

@Service
class ShopService(private val shopRepository: ShopRepository) {

    @Value("\${ss.geocoding}")
    val geocodingApiKey = ""

    fun findByUserId(userId: String): List<Shop> = shopRepository.findByUserId(userId)

    fun save(shop: Shop): Shop = shopRepository.save(shop)

    fun scrapingPage(url: String): ShopInfo {
        val document = Jsoup.connect(url).get()
        val shopInfoTr = document.select(".rstinfo-table__table").first().select("tbody").first().select("tr")
        var shopName = ""
        var address = ""
        var hours = ""
        shopInfoTr.forEach {
            when (it.select("th").first().text()) {
                "店名" -> {
                    shopName = it.select("td").first().text()
                }
                "住所" -> {
                    address = it.select("td").first().text()
                }
                "営業時間・ 定休日" -> {
                    hours = it.select("td").first().text().split("営業時間・")[0];
                }
            }
        }
        return ShopInfo(shopName, address, hours)
    }

    fun findByIdAndUserId(id: Long, userId: String): Shop? = shopRepository.findByIdAndUserId(id, userId)

    fun deleteById(id: Long, userId: String): Unit {
        val shop = this.findByIdAndUserId(id, userId)
        if (shop != null) {
            shopRepository.delete(shop)
        }
    }

    fun getLocationList(shopList: List<Shop>): List<LatLng?> {
        // TODO: 毎回生成しないようにする
        val context = geoApiContext()
        return shopList.map {
            val await = GeocodingApi.geocode(context, it.address).await()
            if (await.isNotEmpty()) await[0].geometry.location else null
        }
    }

    private fun geoApiContext(): GeoApiContext {
        return GeoApiContext.Builder()
                .apiKey(geocodingApiKey)
                .build()
    }
}

data class ShopInfo(
        val name: String,
        val address: String,
        val hours: String
)
