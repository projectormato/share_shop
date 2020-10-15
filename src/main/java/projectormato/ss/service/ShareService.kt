package projectormato.ss.service

import org.springframework.stereotype.Service
import projectormato.ss.entity.Share
import projectormato.ss.repository.ShareRepository

@Service
class ShareService(private val shareRepository: ShareRepository) {
    fun save(share: Share): Share = shareRepository.save(share)

    fun findByShareId(shareId: String): List<Share> = shareRepository.findByShareId(shareId)

    fun findBySharedId(sharedId: String): List<Share> = shareRepository.findBySharedId(sharedId)

    fun findByShareIdAndSharedId(shareId: String, sharedId: String): Share? = shareRepository.findByShareIdAndSharedId(shareId, sharedId)
}

