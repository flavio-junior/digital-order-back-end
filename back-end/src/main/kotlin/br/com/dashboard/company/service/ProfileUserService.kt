package br.com.dashboard.company.service

import br.com.dashboard.company.entities.user.User
import br.com.dashboard.company.exceptions.ObjectDuplicateException
import br.com.dashboard.company.exceptions.ResourceNotFoundException
import br.com.dashboard.company.repository.UserRepository
import br.com.dashboard.company.service.AuthService.Companion.EMAIL_DUPLICATE_EXCEPTION
import br.com.dashboard.company.service.UserService.Companion.USER_NOT_FOUND
import br.com.dashboard.company.vo.user.ChangeInformationUserVO
import br.com.dashboard.company.vo.user.ChangePasswordVO
import br.com.dashboard.company.vo.user.EmailVO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProfileUserService {

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var userRepository: UserRepository

    @Transactional
    fun changeInformationUserLogged(
        user: User,
        information: ChangeInformationUserVO
    ) {
        val userLogged: User? = userRepository.fetchByEmail(email = user.email)
        if (userLogged != null) {
            userRepository.changeInformationUser(
                userId = userLogged.id,
                name = information.name,
                surname = information.surname
            )
        } else {
            throw ResourceNotFoundException(message = USER_NOT_FOUND)
        }
    }

    @Transactional
    fun changeEmailUserLogged(
        user: User,
        emailVO: EmailVO
    ) {
        val userLogged: User? = userRepository.fetchByEmail(email = user.email)
        if (userLogged != null) {
            val userSaved: User? = userRepository.fetchByEmail(email = emailVO.email)
            if (userSaved != null) {
                throw ObjectDuplicateException(message = EMAIL_DUPLICATE_EXCEPTION)
            } else {
                userRepository.changeEmailUserLogged(userId = user.id, email = emailVO.email)
            }
        } else {
            throw ResourceNotFoundException(message = USER_NOT_FOUND)
        }
    }

    @Transactional
    fun changePasswordUserLogged(
        user: User,
        changePasswordVO: ChangePasswordVO
    ) {
        val userLogged: User? = userRepository.fetchByEmail(email = user.email)
        if (userLogged != null) {
            userRepository.changePasswordUserLogged(
                userId = user.id,
                password = passwordEncoder.encode(changePasswordVO.password)
            )
        } else {
            throw ResourceNotFoundException(message = USER_NOT_FOUND)
        }
    }
}
