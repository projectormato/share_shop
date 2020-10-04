package projectormato.ss.controller

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
        shopService.save(
                Shop.builder()
                        .userId(user.name)
                        .url(form.url)
                        .name("固定shopName")
                        .address("固定Address")
                        .hours("固定Hours")
                        .build()
        )
        return "redirect:/"
    }

}
