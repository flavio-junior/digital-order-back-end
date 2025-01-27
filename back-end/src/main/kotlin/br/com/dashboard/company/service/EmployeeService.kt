package br.com.dashboard.company.service

import br.com.dashboard.company.entities.employee.Employee
import br.com.dashboard.company.entities.user.User
import br.com.dashboard.company.exceptions.InternalErrorClient
import br.com.dashboard.company.exceptions.ResourceNotFoundException
import br.com.dashboard.company.repository.EmployeeRepository
import br.com.dashboard.company.utils.common.StatusEmployee
import br.com.dashboard.company.utils.common.TypeAccount
import br.com.dashboard.company.utils.others.ConverterUtils.parseListObjects
import br.com.dashboard.company.utils.others.ConverterUtils.parseObject
import br.com.dashboard.company.vo.employee.EmployeeResponseVO
import br.com.dashboard.company.vo.employee.RegisterEmployeeRequestVO
import br.com.dashboard.company.vo.user.SignUpVO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class EmployeeService {

    @Autowired
    private lateinit var employeeRepository: EmployeeRepository

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var authService: AuthService

    @Transactional(readOnly = true)
    fun finAllEmployees(
        user: User,
        name: String?,
        pageable: Pageable
    ): Page<EmployeeResponseVO> {
        val employees: Page<Employee>? =
            employeeRepository.findAllEmployees(userId = user.id, name = name, pageable = pageable)
        return employees?.map { employee -> parseObject(employee, EmployeeResponseVO::class.java) }
            ?: throw ResourceNotFoundException(message = EMPLOYEE_NOT_FOUND)
    }

    @Transactional(readOnly = true)
    fun findEmployeeById(
        user: User,
        employeeId: Long
    ): EmployeeResponseVO {
        val employee = getEmployee(userId = user.id, employeeId = employeeId)
        return parseObject(employee, EmployeeResponseVO::class.java)
    }

    @Transactional(readOnly = true)
    fun findEmployeeByName(
        user: User,
        name: String
    ): List<EmployeeResponseVO> {
        val employees = employeeRepository.findEmployeeByName(userId = user.id, name = name)
        return parseListObjects(employees, EmployeeResponseVO::class.java)
    }

    @Transactional(readOnly = true)
    fun checkEmployeeAlreadyExists(
        user: User,
        employeeName: String,
        saveAuthor: () -> Unit = {}
    ) {
        val employeeSaved: Employee? =
            employeeRepository.checkEmployeeAlreadyExists(userId = user.id, name = employeeName)
        return if (employeeSaved != null) {
            saveAuthor()
        } else {
            throw ResourceNotFoundException(EMPLOYEE_NOT_FOUND)
        }
    }

    fun getEmployee(
        userId: Long,
        employeeId: Long
    ): Employee {
        val employeeSaved: Employee? = employeeRepository.findEmployeeById(userId = userId, employeeId = employeeId)
        if (employeeSaved != null) {
            return employeeSaved
        } else {
            throw ResourceNotFoundException(EMPLOYEE_NOT_FOUND)
        }
    }

    @Transactional
    fun createNewEmployee(
        user: User,
        employee: RegisterEmployeeRequestVO
    ) {
        val createNewEmployee = Employee()
        createNewEmployee.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        createNewEmployee.name = employee.name
        createNewEmployee.function = employee.function
        createNewEmployee.status = StatusEmployee.ENABLED
        createNewEmployee.user = userService.findUserById(userId = user.id)
        employeeRepository.save(createNewEmployee)
        val createAccountToEmployee = SignUpVO(
            name = employee.name,
            surname = employee.surname,
            email = employee.email,
            password = employee.password,
            type = TypeAccount.USER
        )
        authService.signUp(data = createAccountToEmployee)
    }

    @Transactional
    fun disabledProfileEmployee(
        employeeId: Long
    ) {
        val employeeSaved = userService.findUserById(userId = employeeId)
        userService.disabledProfileEmployee(userId = employeeSaved?.id ?: 0, email = employeeSaved?.email ?: "")
        changeStatusEmployee(employeeId = employeeId, boolean = false)
    }

    @Transactional
    fun enabledProfileEmployee(
        employeeId: Long
    ) {
        val employeeSaved = userService.findUserById(userId = employeeId)
        userService.enabledProfileEmployee(userId = employeeSaved?.id ?: 0, email = employeeSaved?.email ?: "")
        changeStatusEmployee(employeeId = employeeId, boolean = true)
    }

    @Transactional
    fun changeStatusEmployee(
        employeeId: Long,
        boolean: Boolean
    ) {
        employeeRepository.changeStatusEmployee(
            employeeId = employeeId,
            status = if (boolean) StatusEmployee.ENABLED else StatusEmployee.DISABLED
        )
    }

    @Transactional
    fun deleteEmployee(
        user: User,
        employeeId: Long
    ) {
        val employeeSaved = getEmployee(userId = user.id, employeeId = employeeId)
        if (employeeSaved.status == StatusEmployee.ENABLED) {
            throw InternalErrorClient(message = EMPLOYEE_WITH_ACTIVE_PROFILE)
        }
        employeeRepository.deleteEmployeeById(userId = user.id, employeeId = employeeId)
        userService.deleteMyAccount(userId = employeeSaved.id)
    }

    companion object {
        const val EMPLOYEE_NOT_FOUND = "Employee Not Found"
        const val EMPLOYEE_WITH_ACTIVE_PROFILE = "Employee with active profile!"
    }
}
