package projectormato.ss.entity;

import javax.persistence.*;

@Entity
@Table(name = "user")
data class User(
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "user_id", nullable = false)
    val userId: String = "",

    @Column(name = "email", nullable = false)
    val email: String = ""
)
