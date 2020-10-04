package projectormato.ss.controller

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import projectormato.ss.service.ShopService

@Controller
class ShopController(private val shopService: ShopService) {

    @GetMapping(path = ["/"])
    fun shopList(@AuthenticationPrincipal user: OAuth2User, model: Model): String {
        model.addAttribute("shopList", shopService.findByUserId(user.name))
        return "index"
    }

}
