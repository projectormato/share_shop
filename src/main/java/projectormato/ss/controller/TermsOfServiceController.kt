package projectormato.ss.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class TermsOfServiceController {

    @GetMapping(path = ["/terms"])
    fun terms(): String {
        return "terms";
    }
}
