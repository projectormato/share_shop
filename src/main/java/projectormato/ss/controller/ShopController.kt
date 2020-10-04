package projectormato.ss.controller

import org.jsoup.Jsoup
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import projectormato.ss.entity.Shop
import projectormato.ss.form.ShopPostForm
import projectormato.ss.service.ShopService

@Controller
class ShopController(private val shopService: ShopService) {

    @GetMapping(path = ["/"])
    fun shopList(@AuthenticationPrincipal user: OAuth2User, model: Model): String {
        model.addAttribute("shopList", shopService.findByUserId(user.name))
        model.addAttribute("shopPostForm", ShopPostForm(""))
        return "index"
    }

    @PostMapping(path = ["/shop"])
    fun createProblem(@AuthenticationPrincipal user: OAuth2User, form: ShopPostForm): String {
        if (!form.url.startsWith("https://tabelog.com/")) {
            return "redirect:/"
        }
        val shopInfo: ShopInfo = scrapingPage(form.url)
        shopService.save(
                Shop.builder()
                        .userId(user.name)
                        .url(form.url)
                        .name(shopInfo.name)
                        .address(shopInfo.address)
                        .hours(shopInfo.hours)
                        .build()
        )
        return "redirect:/"
    }

    private fun scrapingPage(url: String): ShopInfo {
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
}

data class ShopInfo(
        val name: String,
        val address: String,
        val hours: String
)
