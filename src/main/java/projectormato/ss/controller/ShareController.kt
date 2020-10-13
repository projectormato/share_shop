package projectormato.ss.controller

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import projectormato.ss.service.ShopService
import javax.servlet.http.HttpServletRequest

@Controller
class ShareController(private val shopService: ShopService) {

    @GetMapping(path = ["/share"])
    fun shopList(@AuthenticationPrincipal user: OAuth2User, model: Model, request: HttpServletRequest): String {
        model.addAttribute("url", request.requestURL.toString() + "/" + user.name)
        return "share"
    }
}
