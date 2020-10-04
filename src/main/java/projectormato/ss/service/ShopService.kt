package projectormato.ss.service

import org.springframework.stereotype.Service
import projectormato.ss.entity.Shop
import projectormato.ss.repository.ShopRepository

@Service
class ShopService(private val shopRepository: ShopRepository) {

    fun findAll(): List<Shop> = this.shopRepository.findAll()
}
