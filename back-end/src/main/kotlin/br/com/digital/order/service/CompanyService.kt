package br.com.digital.order.service

import br.com.digital.order.entities.company.Company
import br.com.digital.order.entities.user.User
import br.com.digital.order.exceptions.OperationUnauthorizedException
import br.com.digital.order.repository.CompanyRepository
import br.com.digital.order.utils.common.TypeAccount
import br.com.digital.order.utils.others.ConverterUtils.parseObject
import br.com.digital.order.vo.company.CompanyResponseVO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.net.URL
import java.time.LocalDate
import java.time.LocalTime

@Service
class CompanyService {

    @Autowired
    private lateinit var companyRepository: CompanyRepository

    @Autowired
    private lateinit var s3Service: S3Service

    @Autowired
    private lateinit var userService: UserService

    @Transactional
    fun createNewCompany(
        user: User,
        name: String,
        mainImage: MultipartFile
    ): CompanyResponseVO {
        val imageMainSavedUrl: URL = s3Service.uploadFile(file = mainImage, path = PATH_PROFILE, changeNameFile = true)
        val companySaved = companyRepository.save(
            Company(
                identifier = System.currentTimeMillis(),
                date = LocalDate.now(),
                hour = LocalTime.now().withNano(0),
                name = name,
                mainImage = imageMainSavedUrl.toExternalForm(),
                user = userService.findUserById(userId = user.id)
            )
        )
        return parseObject(origin = companySaved, destination = CompanyResponseVO::class.java)
    }

    @Transactional(readOnly = true)
    fun getCompanyByUserLogged(
        user: User
    ): Company {
        return when (user.typeAccount) {
            TypeAccount.ADMIN -> {
                companyRepository.getCompanyByUserLogged(userLoggedId = user.id)
                    ?: throw OperationUnauthorizedException()
            }

            TypeAccount.USER -> {
                val userSaved = userService.findUserById(userId = user.id)
                companyRepository.getCompanyByEmployeeLogged(employeeLoggedId = userSaved?.employee?.id)
                    ?: throw OperationUnauthorizedException()
            }

            else -> {
                throw OperationUnauthorizedException()
            }
        }
    }

    companion object {
        const val PATH_PROFILE = "images/profile/"
    }
}
