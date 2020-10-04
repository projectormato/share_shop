package projectormato.ss.service

import org.jsoup.Jsoup
import org.springframework.stereotype.Service
import projectormato.ss.entity.Shop
import projectormato.ss.repository.ShopRepository

@Service
class ShopService(private val shopRepository: ShopRepository) {

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

}

data class ShopInfo(
        val name: String,
        val address: String,
        val hours: String
)
