package com.taskpulse.app.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object ReportExportHelper {
    fun createPdfReport(context: Context, title: String, content: String): Uri? {
        return try {
            val file = File(context.cacheDir, "taskpulse_report.pdf")
            if (file.exists()) file.delete()
            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = document.startPage(pageInfo)
            val canvas: Canvas = page.canvas
            val paint = Paint().apply {
                textSize = 24f
                isFakeBoldText = true
            }
            canvas.drawText(title, 40f, 60f, paint)
            paint.textSize = 14f
            paint.isFakeBoldText = false
            val lines = content.split("\n")
            var y = 100f
            for (line in lines) {
                canvas.drawText(line, 40f, y, paint)
                y += 24f
            }
            document.finishPage(page)
            file.outputStream().use { document.writeTo(it) }
            document.close()
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        } catch (exception: Exception) {
            null
        }
    }

    fun sharePdfReport(context: Context, uri: Uri) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share TaskPulse report").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}
