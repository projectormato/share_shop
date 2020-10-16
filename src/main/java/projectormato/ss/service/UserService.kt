package projectormato.ss.service

import org.springframework.stereotype.Service
import projectormato.ss.entity.User
import projectormato.ss.repository.UserRepository

@Service
class UserService(private val userRepository: UserRepository) {
    fun findByUserId(id: String): User? = userRepository.findFirstByUserId(id)

    fun save(user: User): User = userRepository.save(user)
    fun findByEmail(email: String): User? = userRepository.findFirstByEmail(email)
    fun findByUserIds(userIdList: List<String>): List<User> = userRepository.findByUserIdIn(userIdList)
}

