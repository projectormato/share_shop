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
    fun shopList(@AuthenticationPrincipal user: OAuth2User, model: Model): String {
        //TODO: お店一覧取得

        // TODO: お店住所から座標取得

        // TODO: 座標情報をフロントに返す
        return "maps"
    }
}
