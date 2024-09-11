package com.thinkauth.thinkfusionauth.repository.impl

import com.mongodb.BasicDBObject
import com.mongodb.DBObject
import com.mongodb.client.gridfs.model.GridFSFile
import com.thinkauth.thinkfusionauth.entities.SentenceDocumentEntity
import com.thinkauth.thinkfusionauth.events.OnSentenceDocumentMediaUploadItemEvent
import com.thinkauth.thinkfusionauth.interfaces.DataOperations
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.repository.SentenceDocumentRepository
import com.thinkauth.thinkfusionauth.services.BusinessService
import com.thinkauth.thinkfusionauth.services.DialectService
import com.thinkauth.thinkfusionauth.services.LanguageService
import org.apache.commons.io.IOUtils
import org.springframework.context.ApplicationEventPublisher
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.gridfs.GridFsOperations
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.IOException


@Component
class SentenceDocumentImpl(
    private val sentenceDocumentRepository: SentenceDocumentRepository,
    private val dialectService: DialectService,
    private val languageService: LanguageService,
    private val businessService: BusinessService,
    private val template: GridFsTemplate,
    private val operations: GridFsOperations,
    private val applicationEventPublisher: ApplicationEventPublisher
):DataOperations<SentenceDocumentEntity> {
    override fun itemExistsById(id: String): Boolean {
       return sentenceDocumentRepository.existsById(id)
    }

    override fun findEverythingPaged(page: Int, size: Int): PagedResponse<List<SentenceDocumentEntity>> {
        val paged = PageRequest.of(page, size)
        val section = sentenceDocumentRepository.findAll(paged)
        return PagedResponse(
            section.content,
            section.number,
            section.totalElements,
            section.totalPages
        )
    }

    override fun getItemById(id: String): SentenceDocumentEntity {
       return sentenceDocumentRepository.findById(id).get()
    }

    override fun deleteItemById(id: String) {
        sentenceDocumentRepository.deleteById(id)
    }

    override fun deleteAllItems() {
        sentenceDocumentRepository.deleteAll()
    }

    override fun updateItem(id: String, item: SentenceDocumentEntity): SentenceDocumentEntity? {
       val sentenceDoc = getItemById(id)
        sentenceDoc.sentenceDocumentState = item.sentenceDocumentState
        sentenceDoc.documentUploadId = item.documentUploadId
        sentenceDoc.dialectId = item.dialectId
        sentenceDoc.languageId = item.languageId
        return createItem(sentenceDoc)
    }

    override fun createItem(item: SentenceDocumentEntity): SentenceDocumentEntity {
        return sentenceDocumentRepository.save(item)
    }

    fun countSentenceDocuments(): Long {
        return sentenceDocumentRepository.count()
    }

    @Throws(IOException::class)
    fun addDBFile(
        languageId:String,
        dialectId:String,
        businessId:String,
        upload: MultipartFile
    ): SentenceDocumentEntity {
        val metadata: DBObject = BasicDBObject()
        metadata.put("fileSize", upload.size)

        val dialect = dialectService.getDialectById(dialectId)
        val language = languageService.getLanguageByLanguageId(languageId)
        val business = businessService.getSingleBusiness(businessId)

        val fileID: Any = template.store(upload.inputStream, upload.originalFilename, upload.contentType, metadata)
        val sentenceDocumentEntity = SentenceDocumentEntity(
            fileID.toString(),
            languageId = languageId,
            dialectId = dialectId,
            dialectName = dialect.dialectName,
            languageName = language.languageName,
            businessId = businessId,
            businessName = business.businessName
        )
        downloadFile(sentenceDocumentEntity)
        createItem(sentenceDocumentEntity)
        val onDocumentUploadEvent = OnSentenceDocumentMediaUploadItemEvent(
            file = upload,
            business = business,
            language = language,
            dialect = dialect,
            fileId = fileID.toString()
        )
        applicationEventPublisher.publishEvent(onDocumentUploadEvent)
        return sentenceDocumentEntity
    }


    @Throws(IOException::class)
    fun downloadFile(loadFile: SentenceDocumentEntity): SentenceDocumentEntity {
        val gridFSFile: GridFSFile = template.findOne(Query(Criteria.where("_id").`is`(loadFile.documentUploadId)))


        if (gridFSFile?.metadata != null) {
            loadFile.fileName =gridFSFile.filename

            loadFile.fileType = gridFSFile.metadata!!["_contentType"].toString()

            loadFile.fileSize = gridFSFile.metadata!!["fileSize"].toString()

            loadFile.file = IOUtils.toByteArray(operations.getResource(gridFSFile).inputStream)
        }

        return loadFile
    }

    fun getSentenceDocumentByFileId(fileId:String): SentenceDocumentEntity {
        return sentenceDocumentRepository.findByDocumentUploadId(fileId)
    }
}