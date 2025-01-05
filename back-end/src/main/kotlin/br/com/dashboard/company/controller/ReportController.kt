package br.com.dashboard.company.controller

import br.com.dashboard.company.entities.user.User
import br.com.dashboard.company.exceptions.ForbiddenActionRequestException
import br.com.dashboard.company.service.ReportService
import br.com.dashboard.company.utils.others.ConstantsUtils.EMPTY_FIELDS
import br.com.dashboard.company.utils.others.MediaType.APPLICATION_JSON
import br.com.dashboard.company.vo.report.ReportRequestVO
import br.com.dashboard.company.vo.report.ReportResponseVO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI

@RestController
@RequestMapping(value = ["/api/dashboard/company/report/v1"])
@Tag(name = "Reports", description = "EndPoint For Managing All Reports")
class ReportController(private val reportService: ReportService) {

    @PostMapping(
        consumes = [APPLICATION_JSON],
        produces = [APPLICATION_JSON]
    )
    @Operation(
        summary = "Create New Report", description = "Create New Report",
        tags = ["Report"],
        responses = [
            ApiResponse(
                description = "Created", responseCode = "201", content = [
                    Content(schema = Schema(implementation = ReportResponseVO::class))
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
                description = "Internal Error", responseCode = "500", content = [
                    Content(schema = Schema(implementation = Unit::class))
                ]
            )
        ]
    )
    fun createNewReport(
        @AuthenticationPrincipal user: User,
        @RequestBody report: ReportRequestVO
    ): ResponseEntity<ReportResponseVO> {
        require(
            value = report.resume.isNotBlank() && report.resume.isNotEmpty() && report.author.isNotBlank() && report.author.isNotBlank()
        ) {
            throw ForbiddenActionRequestException(exception = EMPTY_FIELDS)
        }
        val entity: ReportResponseVO = reportService.createNewReport(user = user, report = report)
        val uri: URI = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
            .buildAndExpand(entity.id).toUri()
        return ResponseEntity.created(uri).body(entity)
    }
}
