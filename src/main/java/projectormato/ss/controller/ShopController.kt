package projectormato.ss.controller

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import projectormato.ss.entity.Shop
import projectormato.ss.entity.User
import projectormato.ss.form.ShopPostForm
import projectormato.ss.service.ShopInfo
import projectormato.ss.service.ShopService
import projectormato.ss.service.UserService

@Controller
class ShopController(
    private val shopService: ShopService,
    private val userService: UserService
) {
    @GetMapping(path = ["/"])
    fun index(): String {
        return "redirect:/shop"
    }

    @GetMapping(path = ["/shop"])
    fun shopList(@AuthenticationPrincipal user: OAuth2User, model: Model): String {
        // NOTE: userのnameとemailの対応付けを持っておくためにDBに格納しておく
        if (userService.findByUserId(user.name) == null) {
            user.attributes["email"]?.let {
                userService.save(User(userId = user.name, email = it.toString()))
            }
        }

        model.addAttribute("shopList", shopService.findByUserId(user.name))
        model.addAttribute("shopPostForm", ShopPostForm(""))
        return "index"
    }

    @GetMapping(path = ["/shop/{id}"])
    fun shopDetail(@AuthenticationPrincipal user: OAuth2User, model: Model, @PathVariable id: Long): String {
        val shop = shopService.findByIdAndUserId(id, user.name)
        return if (shop != null) {
            model.addAttribute("shop", shop)
            model.addAttribute("isAnotherUser", false)
            "detail"
        } else {
            "redirect:/shop"
        }
    }

    @DeleteMapping(path = ["/shop/{id}"])
    fun deleteShop(@AuthenticationPrincipal user: OAuth2User, @PathVariable id: Long): String {
        shopService.deleteById(id, user.name)
        return "redirect:/shop"
    }

    @PostMapping(path = ["/shop"])
    fun postShop(@AuthenticationPrincipal user: OAuth2User, form: ShopPostForm, model: Model): String {
        if (!form.url.startsWith("https://tabelog.com/")) {
            model.addAttribute("error", "食べログのURLを入力してください")
            return shopList(user, model)
        }
        if (this.shopService.findByUserId(user.name).size >= 50) {
            model.addAttribute("error", "お店を50件を超えて登録は出来ません")
            return shopList(user, model)
        }
        val shopInfo: ShopInfo = shopService.scrapingPage(form.url)
        shopService.save(
            Shop(
                userId = user.name,
                url = form.url,
                name = shopInfo.name,
                address = shopInfo.address,
                hours = shopInfo.hours
            )
        )
        return "redirect:/shop"
    }
}
