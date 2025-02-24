package br.com.digital.order.controller

import br.com.digital.order.service.SettingsService
import br.com.digital.order.utils.others.MediaType
import br.com.digital.order.vo.version.VersionResponseVO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI

@RestController
@RequestMapping(value = ["/api/settings/v1"])
@Tag(name = "Settings", description = "EndPoint For Managing Settings of Account")
class SettingsController {

    @Autowired
    private lateinit var settingsService: SettingsService

    @GetMapping(
        value = ["/check/updates/{version}"],
        produces = [MediaType.APPLICATION_JSON]
    )
    @Operation(
        summary = "Check Version of Front End", description = "Check Version of Front End",
        tags = ["Settings"], responses = [
            ApiResponse(
                description = "Success", responseCode = "200", content = [
                    Content(array = ArraySchema(schema = Schema(implementation = Unit::class)))
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
    fun checkActualVersion(
        @PathVariable version: String
    ): ResponseEntity<VersionResponseVO> {
        return ResponseEntity.ok().body(settingsService.checkActualVersion(version = version))
    }

    @PostMapping(
        value = ["create/file"],
        consumes = [MediaType.APPLICATION_MULTI_PART],
        produces = [MediaType.APPLICATION_JSON]
    )
    @Operation(
        summary = "Create New Version", description = "Create New Version",
        tags = ["Settings"],
        responses = [
            ApiResponse(
                description = "Created",
                responseCode = "201",
                content = [Content(schema = Schema(implementation = VersionResponseVO::class))]
            ),
            ApiResponse(
                description = "Bad Request",
                responseCode = "400",
                content = [Content(schema = Schema(implementation = Unit::class))]
            ),
            ApiResponse(
                description = "Internal Error",
                responseCode = "500",
                content = [Content(schema = Schema(implementation = Unit::class))]
            )
        ]
    )
    fun uploadNewVersion(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("version") version: String
    ): ResponseEntity<VersionResponseVO> {
        val versionResponseVo: VersionResponseVO =
            settingsService.uploadNewVersion(file = file, version = version)
        val uri: URI = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
            .buildAndExpand(versionResponseVo.id).toUri()
        return ResponseEntity.created(uri).body(versionResponseVo)
    }

    @PatchMapping(
        value = ["update/file/{id}/{name}"],
        consumes = [MediaType.APPLICATION_MULTI_PART],
        produces = [MediaType.APPLICATION_JSON]
    )
    @Operation(
        summary = "Update File", description = "Update File",
        tags = ["Settings"],
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
                description = "Internal Error", responseCode = "500", content = [
                    Content(schema = Schema(implementation = Unit::class))
                ]
            )
        ]
    )
    fun updateFile(
        @PathVariable("id") versionId: Long,
        @PathVariable("name") newVersion: String,
        @RequestPart("file") file: MultipartFile,
    ): ResponseEntity<*> {
        settingsService.updateFile(versionId = versionId, newVersion = newVersion, file = file)
        return ResponseEntity.noContent().build<Any>()
    }
}
