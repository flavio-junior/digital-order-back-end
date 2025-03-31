package br.com.digital.order.controller

import br.com.digital.order.entities.user.User
import br.com.digital.order.service.CompanyService
import br.com.digital.order.utils.others.MediaType.APPLICATION_JSON
import br.com.digital.order.utils.others.MediaType.APPLICATION_MULTI_PART
import br.com.digital.order.vo.company.CompanyResponseVO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI

@RestController
@RequestMapping(value = ["/api/digital/order/companies/v1"])
@Tag(name = "Company", description = "EndPoint For Managing All Companies")
class CompanyController {

    @Autowired
    private lateinit var companyService: CompanyService

    @PostMapping(
        consumes = [APPLICATION_MULTI_PART],
        produces = [APPLICATION_JSON]
    )
    @Operation(
        summary = "Create New Company", description = "Create New Company",
        tags = ["Company"],
        responses = [
            ApiResponse(
                description = "Created", responseCode = "201", content = [
                    Content(schema = Schema(implementation = CompanyResponseVO::class))
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
    fun createNewCompany(
        @AuthenticationPrincipal user: User,
        @RequestParam("name") name: String,
        @RequestParam("main_image") mainImage: MultipartFile
    ): ResponseEntity<CompanyResponseVO> {
        val entity: CompanyResponseVO = companyService.createNewCompany(user = user, mainImage = mainImage, name = name)
        val uri: URI = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
            .buildAndExpand(entity.id).toUri()
        return ResponseEntity.created(uri).body(entity)
    }
}
