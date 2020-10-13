package projectormato.ss.service

import org.springframework.stereotype.Service
import projectormato.ss.entity.Share
import projectormato.ss.repository.ShareRepository

@Service
class ShareService(private val shareRepository: ShareRepository) {
    fun save(share: Share): Share = shareRepository.save(share)
    fun findByUserId(userId: String): List<Share> = shareRepository.findByShareId(userId)
}

