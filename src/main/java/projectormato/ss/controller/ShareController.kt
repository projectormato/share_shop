package projectormato.ss.controller

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import projectormato.ss.form.ShareForm
import projectormato.ss.service.ShopService
import javax.servlet.http.HttpServletRequest

@Controller
class ShareController(private val shopService: ShopService) {

    @GetMapping(path = ["/share"])
    fun shopList(@AuthenticationPrincipal user: OAuth2User, model: Model, request: HttpServletRequest): String {
        user.attributes["email"]
        model.addAttribute("url", request.requestURL.toString() + "/" + user.name)
        model.addAttribute("shareForm", ShareForm(""))
        return "share"
    }

    @PostMapping(path = ["/share"])
    fun createProblem(@AuthenticationPrincipal user: OAuth2User, form: ShareForm): String {
        // TODO: emailからid(name)を取得する

        // TODO: 取得したid(name)でDBにインサートする

        return "redirect:/share"
    }
}
