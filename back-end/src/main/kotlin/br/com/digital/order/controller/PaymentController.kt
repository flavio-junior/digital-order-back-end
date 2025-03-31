package br.com.digital.order.controller

import br.com.digital.order.entities.user.User
import br.com.digital.order.service.PaymentService
import br.com.digital.order.utils.others.MediaType.APPLICATION_JSON
import br.com.digital.order.vo.payment.AnaliseDayVO
import br.com.digital.order.vo.payment.PaymentResponseVO
import br.com.digital.order.vo.payment.TypeAnalysisRequestVO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping(value = ["/api/digital/order/payment/v1"])
@Tag(name = "Payment", description = "EndPoint For Managing All Checkouts")
class PaymentController {

    @Autowired
    private lateinit var paymentService: PaymentService

    @GetMapping(
        value = ["/details/all"],
        produces = [APPLICATION_JSON]
    )
    @Operation(
        summary = "Find All Payments", description = "Find All Payments",
        tags = ["Payment"], responses = [
            ApiResponse(
                description = "Success", responseCode = "200", content = [
                    Content(array = ArraySchema(schema = Schema(implementation = PaymentResponseVO::class)))
                ]
            ),
            ApiResponse(
                description = "Bad Request", responseCode = "400", content = [
                    Content(schema = Schema(implementation = Unit::class))
                ]
            ),
            ApiResponse(
                description = "Unauthorized", responseCode = "401", content = [
                    Content(schema = Schema(implementation = Unit::class))
                ]
            ),
            ApiResponse(
                description = "Operation Unauthorized", responseCode = "403", content = [
                    Content(schema = Schema(implementation = Unit::class))
                ]
            ),
            ApiResponse(
                description = "Not Found", responseCode = "404", content = [
                    Content(schema = Schema(implementation = Unit::class))
                ]
            ),
            ApiResponse(
                description = "Internal Error", responseCode = "500", content = [
                    Content(schema = Schema(implementation = Unit::class))
                ]
            )
        ]
    )
    fun getAllPaymentsDay(
        @AuthenticationPrincipal user: User,
        @RequestParam(required = false) code: Long?,
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "12") size: Int,
        @RequestParam(value = "sort", defaultValue = "asc") sort: String
    ): ResponseEntity<Page<PaymentResponseVO>> {
        val sortDirection: Sort.Direction =
            if ("desc".equals(sort, ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val pageable: Pageable = PageRequest.of(page, size, Sort.by(sortDirection, "date"))
        return ResponseEntity.ok(paymentService.getAllPaymentsDay(user = user, code = code, pageable = pageable))
    }

    @PostMapping(
        value = ["/details/analysis"],
        produces = [APPLICATION_JSON]
    )
    @Operation(
        summary = "Get Details of Analysis", description = "Get Details of Analysis",
        tags = ["Payment"], responses = [
            ApiResponse(
                description = "Success", responseCode = "200", content = [
                    Content(array = ArraySchema(schema = Schema(implementation = AnaliseDayVO::class)))
                ]
            ),
            ApiResponse(
                description = "Bad Request", responseCode = "400", content = [
                    Content(schema = Schema(implementation = Unit::class))
                ]
            ),
            ApiResponse(
                description = "Unauthorized", responseCode = "401", content = [
                    Content(schema = Schema(implementation = Unit::class))
                ]
            ),
            ApiResponse(
                description = "Operation Unauthorized", responseCode = "403", content = [
                    Content(schema = Schema(implementation = Unit::class))
                ]
            ),
            ApiResponse(
                description = "Not Found", responseCode = "404", content = [
                    Content(schema = Schema(implementation = Unit::class))
                ]
            ),
            ApiResponse(
                description = "Internal Error", responseCode = "500", content = [
                    Content(schema = Schema(implementation = Unit::class))
                ]
            )
        ]
    )
    fun getDetailsAnalysis(
        @AuthenticationPrincipal user: User,
        @RequestParam(name = "date", required = false) date: String?,
        @RequestParam(name = "start", required = false) start: String?,
        @RequestParam(name = "end", required = false) end: String?,
        @RequestBody type: TypeAnalysisRequestVO
    ): ResponseEntity<AnaliseDayVO> {
        val parsedDate = try {
            if (date.isNullOrEmpty()) LocalDate.now() else LocalDate.parse(date)
        } catch (ex: Exception) {
            LocalDate.now()
        }
        val parsedStart = try {
            start?.let { LocalDate.parse(it) }
        } catch (ex: Exception) {
            null
        }
        val parsedEnd = try {
            end?.let { LocalDate.parse(it) }
        } catch (ex: Exception) {
            null
        }
        return ResponseEntity.ok(
            paymentService.getDetailsAnalysis(
                user = user,
                date = parsedDate,
                start = parsedStart,
                end = parsedEnd,
                type = type.type
            )
        )
    }
}