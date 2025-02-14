package br.com.dashboard.company.service

import br.com.dashboard.company.exceptions.*
import com.amazonaws.AmazonClientException
import com.amazonaws.AmazonServiceException
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.DeleteObjectRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.io.InputStream
import java.net.URI
import java.net.URL

@Service
class S3Service {

    @Autowired
    private lateinit var s3client: AmazonS3

    @Value("\${s3.bucket}")
    private val bucketName: String? = null

    fun uploadFile(file: MultipartFile): URL {
        try {
            return processFile(
                inputStream = file.inputStream,
                fileName = file.originalFilename
                    ?: throw ForbiddenActionRequestException(exception = NAME_IMAGE_NOT_EMPTY),
                contentType = file.contentType,
                contentLength = file.size
            )
        } catch (e: AmazonServiceException) {
            throw AWSServiceException(e.message)
        } catch (e: AmazonClientException) {
            throw AWSClientException(e.message)
        } catch (e: IOException) {
            throw IllegalArgumentCustomException(e.message)
        }
    }

    private fun processFile(
        inputStream: InputStream,
        fileName: String,
        contentType: String?,
        contentLength: Long
    ): URL {
        val objectMetadata = ObjectMetadata()
        objectMetadata.contentType = contentType
        objectMetadata.contentLength = contentLength
        s3client.putObject(bucketName, fileName, inputStream, objectMetadata)
        return s3client.getUrl(bucketName, fileName)
    }

    fun updateFile(url: String, multipartFile: MultipartFile): URL {
        try {
            val uri = URI(url)
            val bucketNameFind = uri.host.split(".")[0]
            val nativeImage = uri.path.substring(1)
            if (s3client.doesObjectExist(bucketNameFind, nativeImage)) {
                deleteFile(url)
                return uploadFile(multipartFile)
            } else {
                throw ResourceNotFoundException(message = FILE_NOT_FOUND)
            }
        } catch (a: AmazonClientException) {
            throw RuntimeException(a.message)
        }
    }

    fun deleteFile(url: String) {
        try {
            val uri = URI(url)
            val bucketNameFind = uri.host.split(".")[0]
            val nativeImage = uri.path.substring(1)
            if (bucketName == bucketNameFind) {
                s3client.deleteObject(DeleteObjectRequest(bucketName, nativeImage))
            }
        } catch (e: Exception) {
            throw ResourceNotFoundException(message = FILE_NOT_FOUND)
        }
    }

    companion object {
        const val NAME_IMAGE_NOT_EMPTY = "Image not empty"
        const val FILE_NOT_FOUND = "File Not Found"
    }
}
