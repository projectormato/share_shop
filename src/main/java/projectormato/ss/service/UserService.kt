package projectormato.ss.service

import org.springframework.stereotype.Service
import projectormato.ss.entity.User
import projectormato.ss.repository.UserRepository

@Service
class UserService(private val userRepository: UserRepository) {
    fun findById(id: String): User? = userRepository.findFirstByUserId(id)
    fun save(user: User) = userRepository.save(user)
}

