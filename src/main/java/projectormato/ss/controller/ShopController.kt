package projectormato.ss.controller

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import projectormato.ss.entity.Shop
import projectormato.ss.form.ShopPostForm
import projectormato.ss.service.ShopInfo
import projectormato.ss.service.ShopService

@Controller
class ShopController(private val shopService: ShopService) {

    @GetMapping(path = ["/"])
    fun shopList(@AuthenticationPrincipal user: OAuth2User, model: Model): String {
        model.addAttribute("shopList", shopService.findByUserId(user.name))
        model.addAttribute("shopPostForm", ShopPostForm(""))
        return "index"
    }

    @GetMapping(path = ["/shop/{id}"])
    fun shopDetail(@AuthenticationPrincipal user: OAuth2User, model: Model, @PathVariable id: Long ): String {
        val shop = shopService.findByIdAndUserId(id, user.name)
        return if (shop != null) {
            model.addAttribute("shop", shop)
            "detail"
        } else {
            "redirect:/"
        }
    }

    @PostMapping(path = ["/shop"])
    fun createProblem(@AuthenticationPrincipal user: OAuth2User, form: ShopPostForm): String {
        if (!form.url.startsWith("https://tabelog.com/")) {
            return "redirect:/"
        }
        val shopInfo: ShopInfo = shopService.scrapingPage(form.url)
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
}
