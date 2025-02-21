package br.com.dashboard.company.service

import br.com.dashboard.company.entities.version.Version
import br.com.dashboard.company.exceptions.ResourceNotFoundException
import br.com.dashboard.company.repository.VersionRepository
import br.com.dashboard.company.utils.others.ConverterUtils.parseObject
import br.com.dashboard.company.vo.version.VersionResponseVO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.net.URL

@Service
class SettingsService {

    @Autowired
    private lateinit var s3Service: S3Service

    @Autowired
    private lateinit var versionRepository: VersionRepository

    @Transactional(readOnly = true)
    fun findAllVersions(): List<Version> {
        return versionRepository.findAll()
    }

    @Transactional(readOnly = true)
    fun checkActualVersion(version: String): VersionResponseVO {
        val versionSaved: Version? = versionRepository.findByVersion(version = version)
        return if (versionSaved != null) {
            VersionResponseVO(status = true)
        } else {
            val versionsSaved = findAllVersions()
            if (versionsSaved.isNotEmpty()) {
                val objectSaved =
                    parseObject(origin = versionsSaved.first(), destination = VersionResponseVO::class.java)
                objectSaved.status = false
                objectSaved
            } else {
                throw ResourceNotFoundException(message = NOT_FOUND_VERSION)
            }
        }
    }

    @Transactional
    fun uploadNewVersion(
        file: MultipartFile,
        version: String
    ): VersionResponseVO {
        val imageMainSavedUrl: URL = s3Service.uploadFile(file = file, path = PATH_UPDATE)
        val versionSaved = versionRepository.save(
            Version(
                version = version,
                url = imageMainSavedUrl.toExternalForm()
            )
        )
        return parseObject(origin = versionSaved, destination = VersionResponseVO::class.java)
    }


    @Transactional
    fun updateFile(
        versionId: Long,
        newVersion: String,
        file: MultipartFile
    ) {
        val version: Version = versionRepository.findById(versionId)
            .orElseThrow { ResourceNotFoundException(NOT_FOUND_VERSION) }
       val urlSaved = s3Service.updateFile(url = version.url, multipartFile = file, path = PATH_UPDATE)
        versionRepository.updateUrlVersion(versionId = versionId, version = newVersion, url =  urlSaved.toExternalForm())
    }

    companion object {
        const val NOT_FOUND_VERSION = "Version Not Found!"
        const val PATH_UPDATE = "downloads/updates/"
    }
}
