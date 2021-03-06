package projectormato.ss.controller

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import projectormato.ss.service.ShopService

@Controller
class ShopMapController(private val shopService: ShopService) {
    @GetMapping(path = ["/maps"])
    fun shopMap(@AuthenticationPrincipal user: OAuth2User, model: Model): String {
        val shopList = shopService.findByUserId(user.name)
        val locationList = this.shopService.getLocationList(shopList)

        model.addAttribute("shopList", shopList)
        model.addAttribute("locationList", locationList)
        return "maps"
    }
}
