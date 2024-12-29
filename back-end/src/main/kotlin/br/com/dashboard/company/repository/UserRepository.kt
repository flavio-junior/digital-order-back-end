package br.com.dashboard.company.repository

import br.com.dashboard.company.entities.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User?, Long?> {

    fun findByEmail(email: String?): UserDetails?

    @Query("SELECT u FROM User u WHERE u.email =:email")
    fun fetchByEmail(email: String?): User?

    @Modifying
    @Query("UPDATE User u SET u.name =:name, u.surname =:surname WHERE u.id =:id")
    fun changeInformationUser(
        @Param("id") userId: Long,
        @Param("name") name: String,
        @Param("surname") surname: String
    )

    @Modifying
    @Query("UPDATE User u SET u.email =:email WHERE u.id =:id")
    fun changeEmailUserLogged(
        @Param("id") userId: Long,
        @Param("email") email: String
    )

    @Modifying
    @Query("UPDATE User u SET u.password =:password WHERE u.id =:id")
    fun changePasswordUserLogged(
        @Param("id") userId: Long,
        @Param("password") password: String
    )
}