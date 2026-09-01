package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import androidx.core.content.FileProvider
import com.example.data.local.CompanyEntity
import com.example.data.local.ReceiptEntity
import com.example.ui.screens.InvoiceGroup
import com.example.ui.screens.StatementMovement
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfInvoiceGenerator {

    /**
     * Generates a PDF File for a given invoice group and company information.
     */
    fun generateInvoicePdf(context: Context, invoice: InvoiceGroup, company: CompanyEntity?): File {
        val pdfDocument = PdfDocument()
        
        val pageWidth = 595
        val pageHeight = 842
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint().apply { isAntiAlias = true }

        val primaryColor = Color.parseColor("#1E88E5")
        val secondaryColor = Color.parseColor("#0D47A1")
        val darkTextColor = Color.parseColor("#1C1B1F")
        val grayTextColor = Color.parseColor("#49454F")
        val lightBgColor = Color.parseColor("#F5F5F5")
        val borderColor = Color.parseColor("#E0E0E0")
        val statusBgColor = if (invoice.status == "paid") Color.parseColor("#E8F5E9") else Color.parseColor("#FFEBEE")
        val statusTextColor = if (invoice.status == "paid") Color.parseColor("#2E7D32") else Color.parseColor("#C62828")

        // 1. Header Banner
        paint.color = primaryColor
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), 90f, paint)

        // Header Title
        paint.color = Color.WHITE
        paint.textSize = 22f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("فاتورة مبيعات", (pageWidth - 25).toFloat(), 42f, paint)

        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        val companyNameStr = company?.companyName ?: "Berbox POS System"
        canvas.drawText(companyNameStr, (pageWidth - 25).toFloat(), 68f, paint)

        // Invoice Number on Left side of header
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 16f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("#${invoice.invoiceNumber}", 25f, 45f, paint)

        val dateFormatted = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH).format(Date(invoice.date))
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("التاريخ: $dateFormatted", 25f, 68f, paint)

        // 2. Info Card Box
        val infoBoxTop = 110f
        val infoBoxBottom = 210f
        paint.color = lightBgColor
        canvas.drawRoundRect(25f, infoBoxTop, (pageWidth - 25).toFloat(), infoBoxBottom, 12f, 12f, paint)

        paint.color = borderColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        canvas.drawRoundRect(25f, infoBoxTop, (pageWidth - 25).toFloat(), infoBoxBottom, 12f, 12f, paint)
        paint.style = Paint.Style.FILL

        // Metadata inside Info Box
        paint.color = darkTextColor
        paint.textSize = 11f
        paint.textAlign = Paint.Align.RIGHT

        // Right Column
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("المتجر / الزبون:", (pageWidth - 40).toFloat(), infoBoxTop + 28f, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText(invoice.customerName, (pageWidth - 130).toFloat(), infoBoxTop + 28f, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("اسم الشركة:", (pageWidth - 40).toFloat(), infoBoxTop + 54f, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText(company?.companyName ?: "-", (pageWidth - 120).toFloat(), infoBoxTop + 54f, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("اسم المندوب:", (pageWidth - 40).toFloat(), infoBoxTop + 80f, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText(company?.repName ?: "-", (pageWidth - 120).toFloat(), infoBoxTop + 80f, paint)

        // Left Column inside Info Box
        paint.textAlign = Paint.Align.LEFT
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("رقم الشركة / المندوب: ", 40f, infoBoxTop + 28f, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText(company?.repPhone ?: "-", 160f, infoBoxTop + 28f, paint)

        // Payment status badge
        paint.color = statusBgColor
        canvas.drawRoundRect(40f, infoBoxTop + 48f, 170f, infoBoxTop + 78f, 8f, 8f, paint)

        paint.color = statusTextColor
        paint.textSize = 11f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER
        val statusStr = if (invoice.status == "paid") "مدفوعة (نقداً)" else "ذمم (على الحساب)"
        canvas.drawText(statusStr, 105f, infoBoxTop + 67f, paint)

        // 3. Products Table Header
        val tableTop = 230f
        paint.color = secondaryColor
        canvas.drawRect(25f, tableTop, (pageWidth - 25).toFloat(), tableTop + 30f, paint)

        paint.color = Color.WHITE
        paint.textSize = 11f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        val colTotalX = 40f
        val colPriceX = 140f
        val colQtyX = 230f
        val colNameX = (pageWidth - 40).toFloat()

        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("الإجمالي (د.أ)", colTotalX, tableTop + 20f, paint)
        canvas.drawText("سعر المنتج", colPriceX, tableTop + 20f, paint)
        canvas.drawText("الكمية", colQtyX, tableTop + 20f, paint)

        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("اسم المنتج", colNameX, tableTop + 20f, paint)

        // Table Rows
        var currentY = tableTop + 30f
        val rowHeight = 28f

        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

        invoice.items.forEachIndexed { index, item ->
            if (index % 2 == 1) {
                paint.color = Color.parseColor("#FAFAFA")
                canvas.drawRect(25f, currentY, (pageWidth - 25).toFloat(), currentY + rowHeight, paint)
            }

            paint.color = darkTextColor
            paint.textAlign = Paint.Align.LEFT
            val lineTotal = item.quantity * item.salePrice
            canvas.drawText(String.format(Locale.ENGLISH, "%.2f", lineTotal), colTotalX, currentY + 18f, paint)
            canvas.drawText(String.format(Locale.ENGLISH, "%.2f", item.salePrice), colPriceX, currentY + 18f, paint)
            canvas.drawText("${item.quantity}", colQtyX, currentY + 18f, paint)

            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText(item.productName, colNameX, currentY + 18f, paint)

            paint.color = borderColor
            paint.strokeWidth = 0.5f
            canvas.drawLine(25f, currentY + rowHeight, (pageWidth - 25).toFloat(), currentY + rowHeight, paint)

            currentY += rowHeight
        }

        // 4. Grand Total Summary Box
        currentY += 15f
        paint.color = lightBgColor
        canvas.drawRoundRect((pageWidth - 250).toFloat(), currentY, (pageWidth - 25).toFloat(), currentY + 45f, 8f, 8f, paint)

        paint.color = primaryColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1.5f
        canvas.drawRoundRect((pageWidth - 250).toFloat(), currentY, (pageWidth - 25).toFloat(), currentY + 45f, 8f, 8f, paint)
        paint.style = Paint.Style.FILL

        paint.color = darkTextColor
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("السعر الإجمالي النهائي:", (pageWidth - 40).toFloat(), currentY + 28f, paint)

        paint.color = secondaryColor
        paint.textSize = 14f
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("${String.format(Locale.ENGLISH, "%.2f", invoice.totalAmount)} د.أ", (pageWidth - 235).toFloat(), currentY + 28f, paint)

        // 5. Recipient Signature Box
        currentY += 70f
        paint.color = borderColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        canvas.drawRoundRect(25f, currentY, (pageWidth - 25).toFloat(), currentY + 90f, 10f, 10f, paint)
        paint.style = Paint.Style.FILL

        paint.color = grayTextColor
        paint.textSize = 11f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("توقيع وختم المستلم (إقرار باستلام البضاعة):", (pageWidth - 40).toFloat(), currentY + 25f, paint)

        paint.color = Color.GRAY
        paint.strokeWidth = 1f
        canvas.drawLine((pageWidth - 220).toFloat(), currentY + 65f, (pageWidth - 40).toFloat(), currentY + 65f, paint)
        canvas.drawLine(40f, currentY + 65f, 220f, currentY + 65f, paint)

        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("التوقيع: _________________", (pageWidth - 40).toFloat(), currentY + 80f, paint)

        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("الختم والتاريخ: ____________", 40f, currentY + 80f, paint)

        // 6. Footer Note
        paint.color = grayTextColor
        paint.textSize = 9f
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("شكراً لتعاملكم معنا • تم إصدار هذه الفاتورة بواسطة نظام Berbox", (pageWidth / 2).toFloat(), (pageHeight - 30).toFloat(), paint)

        pdfDocument.finishPage(page)

        val invoicesDir = File(context.cacheDir, "invoices")
        if (!invoicesDir.exists()) invoicesDir.mkdirs()
        val file = File(invoicesDir, "Invoice_${invoice.invoiceNumber}.pdf")
        FileOutputStream(file).use { out -> pdfDocument.writeTo(out) }
        pdfDocument.close()

        return file
    }

    /**
     * Generates a PDF File for a receipt voucher (سند قبض).
     */
    fun generateReceiptPdf(context: Context, receipt: ReceiptEntity, company: CompanyEntity?): File {
        val pdfDocument = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint().apply { isAntiAlias = true }

        val primaryColor = Color.parseColor("#027A48")
        val darkTextColor = Color.parseColor("#1C1B1F")
        val grayTextColor = Color.parseColor("#49454F")
        val lightBgColor = Color.parseColor("#F5F5F5")
        val borderColor = Color.parseColor("#E0E0E0")

        // 1. Header Banner
        paint.color = primaryColor
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), 90f, paint)

        paint.color = Color.WHITE
        paint.textSize = 22f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("سند قبض", (pageWidth - 25).toFloat(), 42f, paint)

        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        val companyNameStr = company?.companyName ?: "Berbox POS System"
        canvas.drawText(companyNameStr, (pageWidth - 25).toFloat(), 68f, paint)

        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 16f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("#${receipt.receiptNumber}", 25f, 45f, paint)

        val dateFormatted = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH).format(Date(receipt.date))
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("التاريخ: $dateFormatted", 25f, 68f, paint)

        // 2. Info Card Box
        val infoBoxTop = 120f
        val infoBoxBottom = 260f
        paint.color = lightBgColor
        canvas.drawRoundRect(25f, infoBoxTop, (pageWidth - 25).toFloat(), infoBoxBottom, 12f, 12f, paint)

        paint.color = borderColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        canvas.drawRoundRect(25f, infoBoxTop, (pageWidth - 25).toFloat(), infoBoxBottom, 12f, 12f, paint)
        paint.style = Paint.Style.FILL

        paint.color = darkTextColor
        paint.textSize = 12f
        paint.textAlign = Paint.Align.RIGHT

        // Received From
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("استلمنا من السيد/المتجر:", (pageWidth - 40).toFloat(), infoBoxTop + 32f, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText(receipt.customerName, (pageWidth - 190).toFloat(), infoBoxTop + 32f, paint)

        // Amount Box
        paint.color = Color.parseColor("#ECFDF3")
        canvas.drawRoundRect(40f, infoBoxTop + 52f, (pageWidth - 40).toFloat(), infoBoxTop + 98f, 8f, 8f, paint)
        
        paint.color = primaryColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        canvas.drawRoundRect(40f, infoBoxTop + 52f, (pageWidth - 40).toFloat(), infoBoxTop + 98f, 8f, 8f, paint)
        paint.style = Paint.Style.FILL

        paint.color = darkTextColor
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("المبلغ المستلم:", (pageWidth - 55).toFloat(), infoBoxTop + 81f, paint)

        paint.color = primaryColor
        paint.textSize = 15f
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("${String.format(Locale.ENGLISH, "%,.2f", receipt.amount)} دينار أردني", 55f, infoBoxTop + 81f, paint)

        // Notes
        if (receipt.notes.isNotBlank()) {
            paint.color = darkTextColor
            paint.textSize = 11f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText("وذلك عن / ملاحظات:", (pageWidth - 40).toFloat(), infoBoxTop + 122f, paint)
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText(receipt.notes, (pageWidth - 170).toFloat(), infoBoxTop + 122f, paint)
        }

        // 3. Company & Sales Rep Box
        val compBoxTop = 280f
        paint.color = Color.parseColor("#FAFAFA")
        canvas.drawRoundRect(25f, compBoxTop, (pageWidth - 25).toFloat(), compBoxTop + 60f, 8f, 8f, paint)

        paint.color = darkTextColor
        paint.textSize = 10f
        paint.textAlign = Paint.Align.RIGHT
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("الشركة المسلّمة:", (pageWidth - 40).toFloat(), compBoxTop + 25f, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText(company?.companyName ?: "-", (pageWidth - 130).toFloat(), compBoxTop + 25f, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("المندوب المستلم:", (pageWidth - 40).toFloat(), compBoxTop + 45f, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("${company?.repName ?: "-"} (${company?.repPhone ?: "-"})", (pageWidth - 130).toFloat(), compBoxTop + 45f, paint)

        // 4. Signatures Box
        val sigTop = 360f
        paint.color = borderColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        canvas.drawRoundRect(25f, sigTop, (pageWidth - 25).toFloat(), sigTop + 90f, 10f, 10f, paint)
        paint.style = Paint.Style.FILL

        paint.color = grayTextColor
        paint.textSize = 10f

        canvas.drawLine((pageWidth - 220).toFloat(), sigTop + 65f, (pageWidth - 40).toFloat(), sigTop + 65f, paint)
        canvas.drawLine(40f, sigTop + 65f, 220f, sigTop + 65f, paint)

        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("توقيع المندوب المستلم: _________________", (pageWidth - 40).toFloat(), sigTop + 80f, paint)

        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("توقيع/ختم الزبون المسلّم: _________________", 40f, sigTop + 80f, paint)

        // Footer
        paint.color = grayTextColor
        paint.textSize = 9f
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("تم إصدار هذا السند بواسطة نظام Berbox", (pageWidth / 2).toFloat(), (pageHeight - 30).toFloat(), paint)

        pdfDocument.finishPage(page)

        val receiptsDir = File(context.cacheDir, "receipts")
        if (!receiptsDir.exists()) receiptsDir.mkdirs()
        val file = File(receiptsDir, "Receipt_${receipt.receiptNumber}.pdf")
        FileOutputStream(file).use { out -> pdfDocument.writeTo(out) }
        pdfDocument.close()

        return file
    }

    /**
     * Generates a PDF File for an Account Statement (كشف حساب).
     */
    fun generateStatementPdf(
        context: Context,
        customerName: String,
        remainingBalance: Double,
        totalSales: Double,
        totalReceipts: Double,
        movements: List<StatementMovement>,
        company: CompanyEntity?
    ): File {
        val pdfDocument = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint().apply { isAntiAlias = true }

        val primaryColor = Color.parseColor("#1565C0")
        val darkTextColor = Color.parseColor("#1C1B1F")
        val grayTextColor = Color.parseColor("#49454F")
        val lightBgColor = Color.parseColor("#F5F5F5")
        val borderColor = Color.parseColor("#E0E0E0")

        // 1. Header Banner
        paint.color = primaryColor
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), 90f, paint)

        paint.color = Color.WHITE
        paint.textSize = 22f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("كشف حساب عميل", (pageWidth - 25).toFloat(), 42f, paint)

        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        val companyNameStr = company?.companyName ?: "Berbox POS System"
        canvas.drawText(companyNameStr, (pageWidth - 25).toFloat(), 68f, paint)

        val dateFormatted = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH).format(Date())
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("تاريخ الاصدار: $dateFormatted", 25f, 68f, paint)

        // 2. Info Summary Box
        val infoTop = 110f
        paint.color = lightBgColor
        canvas.drawRoundRect(25f, infoTop, (pageWidth - 25).toFloat(), infoTop + 90f, 10f, 10f, paint)

        paint.color = borderColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        canvas.drawRoundRect(25f, infoTop, (pageWidth - 25).toFloat(), infoTop + 90f, 10f, 10f, paint)
        paint.style = Paint.Style.FILL

        paint.color = darkTextColor
        paint.textSize = 11f
        paint.textAlign = Paint.Align.RIGHT

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("اسم العميل / المتجر:", (pageWidth - 40).toFloat(), infoTop + 28f, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText(customerName, (pageWidth - 160).toFloat(), infoTop + 28f, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("إجمالي المبيعات (مدين):", (pageWidth - 40).toFloat(), infoTop + 52f, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("${String.format(Locale.ENGLISH, "%,.2f", totalSales)} د.أ", (pageWidth - 170).toFloat(), infoTop + 52f, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("إجمالي المقبوضات (دائن):", (pageWidth - 40).toFloat(), infoTop + 74f, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("${String.format(Locale.ENGLISH, "%,.2f", totalReceipts)} د.أ", (pageWidth - 170).toFloat(), infoTop + 74f, paint)

        // Left side: Remaining Balance Badge
        val balBg = if (remainingBalance > 0) Color.parseColor("#FFEBEE") else Color.parseColor("#E8F5E9")
        val balTxtColor = if (remainingBalance > 0) Color.parseColor("#C62828") else Color.parseColor("#2E7D32")

        paint.color = balBg
        canvas.drawRoundRect(40f, infoTop + 18f, 200f, infoTop + 72f, 8f, 8f, paint)

        paint.color = balTxtColor
        paint.textSize = 10f
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("الرصيد المتبقي (الذمة)", 120f, infoTop + 36f, paint)
        paint.textSize = 13f
        canvas.drawText("${String.format(Locale.ENGLISH, "%,.2f", remainingBalance)} د.أ", 120f, infoTop + 58f, paint)

        // 3. Movements Table Header
        val tableTop = 220f
        paint.color = primaryColor
        canvas.drawRect(25f, tableTop, (pageWidth - 25).toFloat(), tableTop + 30f, paint)

        paint.color = Color.WHITE
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        val colBalX = 40f
        val colAmountX = 140f
        val colDateX = 230f
        val colTitleX = (pageWidth - 40).toFloat()

        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("الرصيد المتراكم", colBalX, tableTop + 20f, paint)
        canvas.drawText("المبلغ (د.أ)", colAmountX, tableTop + 20f, paint)
        canvas.drawText("التاريخ", colDateX, tableTop + 20f, paint)

        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("البيان / نوع الحركة", colTitleX, tableTop + 20f, paint)

        // Table Rows
        var currentY = tableTop + 30f
        val rowHeight = 26f

        paint.textSize = 9.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

        movements.forEachIndexed { index, m ->
            val isInvoice = m.type == "invoice"
            val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Date(m.date))

            if (index % 2 == 1) {
                paint.color = Color.parseColor("#FAFAFA")
                canvas.drawRect(25f, currentY, (pageWidth - 25).toFloat(), currentY + rowHeight, paint)
            }

            paint.textAlign = Paint.Align.LEFT
            paint.color = darkTextColor
            canvas.drawText(String.format(Locale.ENGLISH, "%,.2f", m.runningBalance), colBalX, currentY + 17f, paint)

            paint.color = if (isInvoice) Color.parseColor("#C62828") else Color.parseColor("#2E7D32")
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            val sign = if (isInvoice) "-" else "+"
            canvas.drawText("$sign ${String.format(Locale.ENGLISH, "%,.2f", m.amount)}", colAmountX, currentY + 17f, paint)

            paint.color = darkTextColor
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText(dateStr, colDateX, currentY + 17f, paint)

            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText(m.title, colTitleX, currentY + 17f, paint)

            paint.color = borderColor
            paint.strokeWidth = 0.5f
            canvas.drawLine(25f, currentY + rowHeight, (pageWidth - 25).toFloat(), currentY + rowHeight, paint)

            currentY += rowHeight
            if (currentY > pageHeight - 120) {
                return@forEachIndexed
            }
        }

        // 4. Signatures / Stamp
        val sigTop = currentY + 30f
        if (sigTop + 70f < pageHeight - 40f) {
            paint.color = borderColor
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 1f
            canvas.drawRoundRect(25f, sigTop, (pageWidth - 25).toFloat(), sigTop + 70f, 8f, 8f, paint)
            paint.style = Paint.Style.FILL

            paint.color = grayTextColor
            paint.textSize = 10f
            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText("توقيع المحاسب / المندوب: _________________", (pageWidth - 40).toFloat(), sigTop + 40f, paint)

            paint.textAlign = Paint.Align.LEFT
            canvas.drawText("الختم والتاريخ: _________________", 40f, sigTop + 40f, paint)
        }

        // Footer
        paint.color = grayTextColor
        paint.textSize = 9f
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("تم إصدار هذا الكشف بواسطة نظام Berbox POS", (pageWidth / 2).toFloat(), (pageHeight - 30).toFloat(), paint)

        pdfDocument.finishPage(page)

        val stmtsDir = File(context.cacheDir, "statements")
        if (!stmtsDir.exists()) stmtsDir.mkdirs()
        val file = File(stmtsDir, "Statement_${customerName.replace(" ", "_")}.pdf")
        FileOutputStream(file).use { out -> pdfDocument.writeTo(out) }
        pdfDocument.close()

        return file
    }

    /**
     * Reusable PDF sharing function.
     */
    fun sharePdf(context: Context, pdfFile: File, chooserTitle: String = "مشاركة المستند PDF") {
        val authority = "${context.packageName}.provider"
        val contentUri = FileProvider.getUriForFile(context, authority, pdfFile)

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, contentUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(shareIntent, chooserTitle)
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    /**
     * Backward compatibility helper for sharing invoices.
     */
    fun shareInvoicePdf(context: Context, invoiceFile: File) {
        sharePdf(context, invoiceFile, "مشاركة الفاتورة PDF")
    }

    /**
     * Reusable PDF printing function using Android PrintManager.
     */
    fun printPdf(context: Context, pdfFile: File, jobName: String = "Print_Document") {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
            ?: return

        val printAdapter = object : PrintDocumentAdapter() {
            override fun onLayout(
                oldAttributes: PrintAttributes?,
                newAttributes: PrintAttributes?,
                cancellationSignal: CancellationSignal?,
                callback: LayoutResultCallback?,
                extras: Bundle?
            ) {
                if (cancellationSignal?.isCanceled == true) {
                    callback?.onLayoutCancelled()
                    return
                }

                val info = PrintDocumentInfo.Builder(jobName)
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(1)
                    .build()

                callback?.onLayoutFinished(info, true)
            }

            override fun onWrite(
                pages: Array<out PageRange>?,
                destination: ParcelFileDescriptor?,
                cancellationSignal: CancellationSignal?,
                callback: WriteResultCallback?
            ) {
                if (destination == null) return

                FileInputStream(pdfFile).use { input ->
                    FileOutputStream(destination.fileDescriptor).use { output ->
                        input.copyTo(output)
                    }
                }

                if (cancellationSignal?.isCanceled == true) {
                    callback?.onWriteCancelled()
                } else {
                    callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                }
            }
        }

        printManager.print(jobName, printAdapter, PrintAttributes.Builder().build())
    }

    /**
     * Backward compatibility helper for printing invoices.
     */
    fun printInvoicePdf(context: Context, invoiceFile: File, jobName: String = "Print_Invoice") {
        printPdf(context, invoiceFile, jobName)
    }
}
