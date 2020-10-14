package projectormato.ss.controller

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import projectormato.ss.entity.Share
import projectormato.ss.entity.Shop
import projectormato.ss.form.ShareForm
import projectormato.ss.form.ShopPostForm
import projectormato.ss.service.ShareService
import projectormato.ss.service.ShopInfo
import projectormato.ss.service.ShopService
import projectormato.ss.service.UserService
import javax.servlet.http.HttpServletRequest

@Controller
class ShareController(
        private val userService: UserService,
        private val shareService: ShareService,
        private val shopService: ShopService
        ) {

    @GetMapping(path = ["/share"])
    fun shopList(@AuthenticationPrincipal user: OAuth2User, model: Model, request: HttpServletRequest): String {
        model.addAttribute("url", request.requestURL.toString() + "/" + user.name)
        model.addAttribute("sharedEmailList", userService.findByUserIds(shareService.findByShareId(user.name).map { it.sharedId }).map { it.email })
        model.addAttribute("shareForm", ShareForm(""))
        return "share"
    }

    @PostMapping(path = ["/share"])
    fun shareShop(@AuthenticationPrincipal user: OAuth2User, form: ShareForm): String {
        // NOTE: ユーザ情報が登録されていないemailの場合特に何も返さない(ユーザが登録しているか分からないようにするため)
        val sharedUser = userService.findByEmail(form.email) ?: return "redirect:/share"
        shareService.save(
                Share.builder()
                        .shareId(user.name)
                        .sharedId(sharedUser.userId)
                        .build()
        )
        return "redirect:/share"
    }

    @GetMapping(path = ["/share/{userId}"])
    fun anotherUserShopList(@AuthenticationPrincipal user: OAuth2User, model: Model, @PathVariable userId: String): String {
        if (shareService.findByShareId(userId, user.name) == null) {
            return "redirect:/"
        }
        model.addAttribute("shopList", shopService.findByUserId(userId))
        model.addAttribute("shopPostForm", ShopPostForm(""))
        model.addAttribute("shareUserId", userId)
        return "index"
    }

    @GetMapping(path = ["/share/{userId}/{id}"])
    fun anotherUserShopDetail(@AuthenticationPrincipal user: OAuth2User, model: Model, @PathVariable userId: String, @PathVariable id: Long): String {
        if (shareService.findByShareId(userId, user.name) == null) {
            return "redirect:/"
        }

        val shop = shopService.findByIdAndUserId(id, userId)
        return if (shop != null) {
            model.addAttribute("shop", shop)
            model.addAttribute("isAnotherUser", true)
            "detail"
        } else {
            "redirect:/"
        }
    }


    @GetMapping(path = ["/share/{userId}/maps"])
    fun anotherUserShopMap(@AuthenticationPrincipal user: OAuth2User, model: Model, @PathVariable userId: String): String {
        if (shareService.findByShareId(userId, user.name) == null) {
            return "redirect:/"
        }
        val shopList = shopService.findByUserId(userId)
        val locationList = this.shopService.getLocationList(shopList)

        model.addAttribute("shopList", shopList)
        model.addAttribute("locationList", locationList)
        return "maps"
    }

    @PostMapping(path = ["/share/{userId}/shop"])
    fun anotherUserPostShop(@AuthenticationPrincipal user: OAuth2User, form: ShopPostForm, @PathVariable userId: String): String {
        if (shareService.findByShareId(userId, user.name) == null) {
            return "redirect:/"
        }
        if (!form.url.startsWith("https://tabelog.com/")) {
            return "redirect:/share/$userId"
        }
        val shopInfo: ShopInfo = shopService.scrapingPage(form.url)
        shopService.save(
                Shop.builder()
                        .userId(userId)
                        .url(form.url)
                        .name(shopInfo.name)
                        .address(shopInfo.address)
                        .hours(shopInfo.hours)
                        .build()
        )
        return "redirect:/share/$userId"
    }
}
