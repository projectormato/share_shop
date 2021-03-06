package projectormato.ss.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import projectormato.ss.entity.Shop

@Repository
interface ShopRepository : JpaRepository<Shop, Long> {
    fun findByUserId(userId: String): List<Shop>

    fun findByIdAndUserId(id: Long, userId: String): Shop?
}
