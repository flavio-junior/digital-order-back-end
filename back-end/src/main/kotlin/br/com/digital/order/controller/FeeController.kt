package br.com.digital.order.controller

import br.com.digital.order.entities.user.User
import br.com.digital.order.exceptions.ForbiddenActionRequestException
import br.com.digital.order.service.FeeService
import br.com.digital.order.utils.common.PercentageRequestVO
import br.com.digital.order.utils.others.ConstantsUtils.EMPTY_FIELDS
import br.com.digital.order.utils.others.MediaType.APPLICATION_JSON
import br.com.digital.order.vo.day.DaysRequestVO
import br.com.digital.order.vo.fee.FeeRequestVO
import br.com.digital.order.vo.fee.FeeResponseVO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/api/digital/order/fees/v1"])
@Tag(name = "Fee", description = "EndPoint For Managing All Fees")
class FeeController {

    @Autowired
    private lateinit var feeService: FeeService

    @GetMapping(produces = [APPLICATION_JSON])
    @Operation(
        summary = "List All Fees", description = "List All Fees",
        tags = ["Fee"], responses = [
            ApiResponse(
                description = "Success", responseCode = "200", content = [
                    Content(array = ArraySchema(schema = Schema(implementation = FeeResponseVO::class)))
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
    fun findAllFees(
        @AuthenticationPrincipal user: User
    ): ResponseEntity<List<FeeResponseVO>> {
        return ResponseEntity.ok(feeService.findAllFees(user = user))
    }

    @PostMapping(
        consumes = ["application/json"],
        produces = ["application/json"]
    )
    @Operation(
        summary = "Create New Fee", description = "Create New Fee",
        tags = ["Fee"],
        responses = [
            ApiResponse(
                description = "Created", responseCode = "201", content = [
                    Content(schema = Schema(implementation = Unit::class))
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
                description = "Conflict", responseCode = "409", content = [
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
    fun createNewFee(
        @AuthenticationPrincipal user: User,
        @RequestBody fee: FeeRequestVO
    ): ResponseEntity<*> {
        require(value = fee.percentage > 0 && fee.assigned != null) {
            throw ForbiddenActionRequestException(exception = EMPTY_FIELDS)
        }
        feeService.createNewFee(user, fee)
        return ResponseEntity.status(HttpStatus.CREATED).build<Any>()
    }

    @PostMapping(
        value = ["add/days/fee/{id}"],
        consumes = ["application/json"],
        produces = ["application/json"]
    )
    @Operation(
        summary = "Add Days New Fee", description = "Add Days New Fee",
        tags = ["Fee"],
        responses = [
            ApiResponse(
                description = "Created", responseCode = "201", content = [
                    Content(schema = Schema(implementation = Unit::class))
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
                description = "Conflict", responseCode = "409", content = [
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
    fun addDaysFee(
        @AuthenticationPrincipal user: User,
        @PathVariable(value = "id") feeId: Long,
        @RequestBody days: DaysRequestVO
    ): ResponseEntity<*> {
        require(value = days.days != null) {
            throw ForbiddenActionRequestException(exception = EMPTY_FIELDS)
        }
        feeService.addDaysFee(user = user, feeId = feeId, days = days)
        return ResponseEntity.status(HttpStatus.CREATED).build<Any>()
    }

    @PatchMapping(
        value = ["update/price/fee/{id}"],
        consumes = [APPLICATION_JSON],
        produces = [APPLICATION_JSON]
    )
    @Operation(
        summary = "Update Price of Fee", description = "Update Price of Fee",
        tags = ["Fee"],
        responses = [
            ApiResponse(
                description = "No Content", responseCode = "204", content = [
                    Content(schema = Schema(implementation = Unit::class))
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
                description = "Conflict", responseCode = "409", content = [
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
    fun updatePriceFee(
        @AuthenticationPrincipal user: User,
        @PathVariable(value = "id") feeId: Long,
        @RequestBody percentage: PercentageRequestVO
    ): ResponseEntity<*> {
        feeService.updatePriceFee(user = user, feeId = feeId, percentage = percentage)
        return ResponseEntity.noContent().build<Any>()
    }

    @DeleteMapping(
        value = ["/{id}"],
        produces = [APPLICATION_JSON]
    )
    @Operation(
        summary = "Delete Fee By Id", description = "Delete Fee By Id",
        tags = ["Fee"],
        responses = [
            ApiResponse(
                description = "No Content", responseCode = "204", content = [
                    Content(schema = Schema(implementation = Unit::class))
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
    fun deleteFee(
        @AuthenticationPrincipal user: User,
        @PathVariable(value = "id") feeId: Long
    ): ResponseEntity<*> {
        feeService.deleteFee(user = user, feeId = feeId)
        return ResponseEntity.noContent().build<Any>()
    }

    @DeleteMapping(
        value = ["/fee/{feeId}/{dayId}"],
        produces = [APPLICATION_JSON]
    )
    @Operation(
        summary = "Delete Day Fee By Id", description = "Delete Day Fee By Id",
        tags = ["Fee"],
        responses = [
            ApiResponse(
                description = "No Content", responseCode = "204", content = [
                    Content(schema = Schema(implementation = Unit::class))
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
    fun deleteFee(
        @AuthenticationPrincipal user: User,
        @PathVariable(value = "feeId") feeId: Long,
        @PathVariable(value = "dayId") dayId: Long
    ): ResponseEntity<*> {
        feeService.deleteDayFee(user = user, feeId = feeId, dayId = dayId)
        return ResponseEntity.noContent().build<Any>()
    }
}
