package projectormato.ss.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import projectormato.ss.entity.User

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findFirstByUserId(userId: String): User?
}
