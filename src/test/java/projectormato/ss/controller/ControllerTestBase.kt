package projectormato.ss.controller;

import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import projectormato.ss.entity.Share
import projectormato.ss.entity.Shop
import projectormato.ss.entity.User
import projectormato.ss.repository.*

@SpringBootTest
@AutoConfigureMockMvc
open class ControllerTestBase {
    protected val userId = "1"
    protected val anotherUserId = "2"
    protected val userEmail = "tomato@example.com"
    protected val anotherUserEmail = "tomato2@example.com"

    protected lateinit var mockMvc: MockMvc

    // TODO: Autowiredなくしたい by tomato
    @Autowired
    protected lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    protected lateinit var shopRepository: ShopRepository

    @Autowired
    protected lateinit var userRepository: UserRepository

    @Autowired
    protected lateinit var shareRepository: ShareRepository

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
        shopRepository.deleteAll()
        userRepository.deleteAll()
        shareRepository.deleteAll()
    }

    protected fun createShop(userId: String): Shop = Shop.builder().url("https://tabelog.com/tokyo/A1321/A132101/13137795/").name("shop1").address("tokyo").hours("all time").userId(userId).build()

    protected fun createUser(userId: String, email: String): User = User.builder().userId(userId).email(email).build()

    protected fun createShare(shareId: String, sharedId: String): Share = Share.builder().shareId(shareId).sharedId(sharedId).build()
}
