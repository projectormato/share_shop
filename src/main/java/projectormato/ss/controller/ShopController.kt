package projectormato.ss.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import projectormato.ss.service.ShopService

@Controller
class ShopController(private val shopService: ShopService) {

    @GetMapping(path = ["/"])
    fun shopList(model: Model): String {
        model.addAttribute("shopList", shopService.findAll())
        return "index"
    }

}
