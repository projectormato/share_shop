package projectormato.ss.controller

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import projectormato.ss.entity.Share
import projectormato.ss.form.ShareForm
import projectormato.ss.service.ShareService
import projectormato.ss.service.UserService
import javax.servlet.http.HttpServletRequest

@Controller
class ShareController(
        private val userService: UserService,
        private val shareService: ShareService
) {

    @GetMapping(path = ["/share"])
    fun shopList(@AuthenticationPrincipal user: OAuth2User, model: Model, request: HttpServletRequest): String {
        model.addAttribute("url", request.requestURL.toString() + "/" + user.name)
        model.addAttribute("sharedEmailList", userService.findByUserIds(shareService.findByUserId(user.name).map { it.sharedId }).map { it.email })
        model.addAttribute("shareForm", ShareForm(""))
        return "share"
    }

    @PostMapping(path = ["/share"])
    fun createProblem(@AuthenticationPrincipal user: OAuth2User, form: ShareForm): String {
        // NOTE: ユーザ情報が登録されていないemailの場合特に何も返さない(ユーザが登録しているか分からないようにするため)
        val sharedUser = userService.findByEmail(form.email) ?: return "redirect:/share"
        shareService.save(Share.builder()
                .shareId(user.name)
                .sharedId(sharedUser.userId)
                .build()
        )
        return "redirect:/share"
    }
}
