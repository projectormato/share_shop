package projectormato.ss.entity;

import javax.persistence.*;

@Entity
@Table(name = "shop")
data class Shop(
    @Id
    @Column(name = "shop_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "user_id", nullable = false)
    val userId: String = "",

    @Column(name = "url", nullable = false)
    val url: String = "",

    @Column(name = "name", nullable = false)
    val name: String = "",

    @Column(name = "address", nullable = false)
    val address: String = "",

    @Column(name = "hours", nullable = false)
    val hours: String = ""
)
