package br.com.dashboard.company.service

import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.io.IOException

@Service
class QRCodeService {

    @Throws(WriterException::class, IOException::class)
    fun generateQRCodeImage(value: String?, width: Int, height: Int): ByteArray {
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(value, BarcodeFormat.QR_CODE, width, height)
        val pngOutputStream = ByteArrayOutputStream()
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream)
        return pngOutputStream.toByteArray()
    }
}
