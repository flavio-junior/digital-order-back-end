package br.com.dashboard.company.repository

import br.com.dashboard.company.entities.employee.Employee
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface EmployeeRepository: JpaRepository<Employee, Long> {

    @Query(
        value = """
        SELECT e FROM Employee e
            WHERE e.user.id = :userId
        AND (:name IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')))
    """
    )
    fun findAllEmployees(
        @Param("userId") userId: Long,
        @Param("name") name: String?,
        pageable: Pageable
    ): Page<Employee>?

    @Query(value = "SELECT e FROM Employee e WHERE e.user.id = :userId AND e.id = :employeeId")
    fun findEmployeeById(
        @Param("userId") userId: Long,
        @Param("employeeId") employeeId: Long
    ): Employee?

    @Modifying
    @Query(value = "DELETE FROM Employee e WHERE e.id = :employeeId AND e.user.id = :userId")
    fun deleteEmployeeById(
        @Param("userId") userId: Long,
        @Param("employeeId") employeeId: Long
    ): Int
}
