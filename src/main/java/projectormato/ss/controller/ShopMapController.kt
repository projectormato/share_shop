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
import projectormato.ss.form.ShopPostForm
import projectormato.ss.service.ShopInfo
import projectormato.ss.service.ShopService

@Controller
class ShopMapController(private val shopService: ShopService) {

    @GetMapping(path = ["/maps"])
    fun shopList(@AuthenticationPrincipal user: OAuth2User, model: Model): String {
        return "map"
    }
}
