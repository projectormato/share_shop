package projectormato.ss.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import projectormato.ss.entity.Share

@Repository
interface ShareRepository : JpaRepository<Share, Long> {
    fun findByShareId(userId: String): List<Share>
}
