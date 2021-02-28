package projectormato.ss.entity;

import javax.persistence.*;

@Entity
@Table(name = "share_shared")
data class Share(
    @Id
    @Column(name = "share_shared_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "share_id", nullable = false)
    val shareId: String = "",

    @Column(name = "shared_id", nullable = false)
    val sharedId: String = ""
)


