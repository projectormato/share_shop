package projectormato.ss.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HelloController {
    @GetMapping(path = ["/hello"])
    fun hello(): String = "hello"
}
