package projectormato.ss.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class LoginController {
    @GetMapping(path = ["/login"])
    fun getLoginPage(): String = "login"
}
