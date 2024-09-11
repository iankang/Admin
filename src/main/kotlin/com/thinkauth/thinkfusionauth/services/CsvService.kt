package com.thinkauth.thinkfusionauth.services

import com.opencsv.bean.CsvToBean
import com.opencsv.bean.CsvToBeanBuilder
import com.thinkauth.thinkfusionauth.exceptions.BadRequestException
import com.thinkauth.thinkfusionauth.models.responses.SentenceDocumentCSV
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

@Service
class CsvService {

    fun uploadCsvFile(file: MultipartFile): List<SentenceDocumentCSV> {
        throwIfFileEmpty(file)
        var fileReader: BufferedReader? = null

        try {
            fileReader = BufferedReader(InputStreamReader(file.inputStream))
            val csvToBean = createCSVToBean(fileReader)

            return csvToBean.parse()
        } catch (ex: Exception) {
            throw Exception("Error during csv import")
        } finally {
            closeFileReader(fileReader)
        }
    }

    private fun throwIfFileEmpty(file: MultipartFile) {
        if (file.isEmpty) throw BadRequestException("Empty file")
    }

    private fun createCSVToBean(fileReader: BufferedReader?): CsvToBean<SentenceDocumentCSV> =
        CsvToBeanBuilder<SentenceDocumentCSV>(fileReader)
            .withType(SentenceDocumentCSV::class.java)
            .withSkipLines(1)
            .withIgnoreLeadingWhiteSpace(true).build()

    private fun closeFileReader(fileReader: BufferedReader?) {
        try {
            fileReader!!.close()
        } catch (ex: IOException) {
            throw Exception("Error during csv import")
        }
    }
}