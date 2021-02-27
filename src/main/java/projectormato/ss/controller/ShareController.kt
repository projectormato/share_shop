package projectormato.ss.controller

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.DeleteMapping
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
    fun shareShopList(@AuthenticationPrincipal user: OAuth2User, model: Model, request: HttpServletRequest): String {
        val shareList = shareService.findByShareId(user.name)
        val emailList = userService.findByUserIds(shareList.map { it.sharedId }).map { it.email }
        val shareEmailMap = shareList.map { it.id }.zip(emailList).toMap()

        model.addAttribute("url", request.requestURL.toString() + "/" + user.name)
        model.addAttribute("shareEmailMap", shareEmailMap)
        model.addAttribute("shareForm", ShareForm(""))
        return "share"
    }

    @PostMapping(path = ["/share"])
    fun shareShop(
        @AuthenticationPrincipal user: OAuth2User,
        form: ShareForm,
        model: Model,
        request: HttpServletRequest
    ): String {
        if (user.attributes["email"] == form.email) {
            return "redirect:/share"
        }
        if (shareService.findByShareId(user.name).size >= 100) {
            model.addAttribute("error", " 100人以上と同時に共有することは出来ません")
            return shareShopList(user, model, request)
        }
        // NOTE: ユーザ情報が登録されていないemailの場合特に何も返さない(該当emailでユーザが登録しているか分からないようにするため)
        val sharedUser = userService.findByEmail(form.email) ?: return "redirect:/share"
        shareService.save(
            Share.builder()
                .shareId(user.name)
                .sharedId(sharedUser.userId)
                .build()
        )
        return "redirect:/share"
    }

    @DeleteMapping(path = ["/share/{id}"])
    fun deleteShare(@AuthenticationPrincipal user: OAuth2User, @PathVariable id: Long): String {
        shareService.deleteByIdAndShareId(id, user.name)
        return "redirect:/share"
    }

    @DeleteMapping(path = ["/shared/{id}"])
    fun deleteShared(@AuthenticationPrincipal user: OAuth2User, @PathVariable id: Long): String {
        shareService.deleteByIdAndSharedId(id, user.name)
        return "redirect:/shared"
    }

    @GetMapping(path = ["/share/{userId}"])
    fun anotherUserShopList(
        @AuthenticationPrincipal user: OAuth2User,
        model: Model,
        @PathVariable userId: String
    ): String {
        if (shareService.findByShareIdAndSharedId(userId, user.name) == null) {
            return "redirect:/shop"
        }
        model.addAttribute("shopList", shopService.findByUserId(userId))
        model.addAttribute("shopPostForm", ShopPostForm(""))
        model.addAttribute("shareUserId", userId)
        return "index"
    }

    @GetMapping(path = ["/share/{userId}/{id}"])
    fun anotherUserShopDetail(
        @AuthenticationPrincipal user: OAuth2User,
        model: Model,
        @PathVariable userId: String,
        @PathVariable id: Long
    ): String {
        if (shareService.findByShareIdAndSharedId(userId, user.name) == null) {
            return "redirect:/shop"
        }

        val shop = shopService.findByIdAndUserId(id, userId)
        return if (shop != null) {
            model.addAttribute("shop", shop)
            model.addAttribute("isAnotherUser", true)
            "detail"
        } else {
            "redirect:/shop"
        }
    }

    @GetMapping(path = ["/share/{userId}/maps"])
    fun anotherUserShopMap(
        @AuthenticationPrincipal user: OAuth2User,
        model: Model,
        @PathVariable userId: String
    ): String {
        if (shareService.findByShareIdAndSharedId(userId, user.name) == null) {
            return "redirect:/shop"
        }
        val shopList = shopService.findByUserId(userId)
        val locationList = this.shopService.getLocationList(shopList)


        model.addAttribute("shopList", shopList)
        model.addAttribute("locationList", locationList)
        model.addAttribute("shareUserId", userId)
        return "maps"
    }

    @PostMapping(path = ["/share/{userId}/shop"])
    fun anotherUserPostShop(
        @AuthenticationPrincipal user: OAuth2User,
        form: ShopPostForm,
        @PathVariable userId: String
    ): String {
        if (shareService.findByShareIdAndSharedId(userId, user.name) == null) {
            return "redirect:/shop"
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

    @GetMapping(path = ["/shared"])
    fun sharedShopList(@AuthenticationPrincipal user: OAuth2User, model: Model, request: HttpServletRequest): String {
        val sharedList = shareService.findBySharedId(user.name)
        model.addAttribute("sharedList", sharedList)
        model.addAttribute("sharedUrlList", sharedList.map { getShareUrl(request, it.shareId) })
        model.addAttribute("sharedEmailList", userService.findByUserIds(sharedList.map { it.shareId }).map { it.email })
        return "shared"
    }

    private fun getShareUrl(request: HttpServletRequest, shareId: String): String {
        var domain = request.scheme + "://" + request.serverName
        if (request.serverPort != 80 && request.serverPort != 443) {
            domain += ":" + request.serverPort
        }
        return "$domain/share/$shareId"
    }
}

