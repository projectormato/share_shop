package projectormato.ss.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    @GetMapping(path = "/hello")
    public String hello(@AuthenticationPrincipal OidcUser user, Model model) {
//        model.addAttribute("username", user.getFullName());
        return "hello";
    }

}
