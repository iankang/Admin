package com.thinkauth.thinkfusionauth.services


import com.thinkauth.thinkfusionauth.entities.Business
import com.thinkauth.thinkfusionauth.events.OnMediaUploadItemEvent
import com.thinkauth.thinkfusionauth.models.requests.BusinessRequest
import com.thinkauth.thinkfusionauth.repository.BusinessRepository
import com.thinkauth.thinkfusionauth.utils.BucketName
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.data.rest.core.mapping.ResourceType
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.absolutePathString
import kotlin.io.path.name

@Service
class BusinessService(
    private val businessRepository: BusinessRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
    @Value("\${minio.bucket}")
    private val thinkResources: String,
    private val minioService: StorageService
) {
    fun addBusiness(businessRequest: BusinessRequest): Business {
        val business = Business(businessRequest.businessName)
        return businessRepository.save(business)
    }

    fun getBusinesses(
    ): MutableList<Business> {

        return businessRepository.findAll()
    }

    fun businessExists(businessRequest: BusinessRequest): Boolean {
        if (businessRepository.existsByBusinessName(businessRequest.businessName ?: "")) {
            return true
        }
        return false
    }

    fun businessExistsById(businessId: String): Boolean {
        if (businessRepository.existsById(businessId)) {
            return true
        }
        return false
    }

    fun deleteBusinessById(businessId: String) {
        return businessRepository.deleteById(businessId)
    }

    fun updateBusiness(businessId: String, businessRequest: BusinessRequest): Business? {

        val fetchedBusiness = businessRepository.findById(businessId)
        fetchedBusiness.map { biz ->
            biz.businessName = businessRequest.businessName
        }
        return businessRepository.save(fetchedBusiness.get())
    }

    fun getSingleBusiness(businessId: String): Business {
        return businessRepository.findById(businessId).get()
    }

//    fun countBySurveys(businessId: String): Int {
//        return businessRepository.findById(businessId).get().surveys?.size ?: 0
//    }

    fun saveBusiness(business: Business): Business {
        return businessRepository.save(business)
    }

    fun addBusinessProfilePicture(businessId:String, file: MultipartFile): Business {

        val bizniz = getSingleBusiness(businessId)

        val path = Paths.get(thinkResources+ File.separator+BucketName.BUSINESS_PROFILE_PIC.name+File.separator+file.originalFilename)
        bizniz.businessImageProfile = path.absolutePathString()
        val onMediaUploadItemEvent = OnMediaUploadItemEvent(file,path,BucketName.BUSINESS_PROFILE_PIC)
        applicationEventPublisher.publishEvent(onMediaUploadItemEvent)
        return saveBusiness(bizniz)
    }

    fun getBusinessProfilePicture(businessId:String):Resource{

        val business = getSingleBusiness(businessId)
        val filePath: Path = Paths
            .get(
                thinkResources + File.separator+ BucketName.BUSINESS_PROFILE_PIC.name + File.separator + StringUtils.cleanPath(
                    business.businessImageProfile!!
                )
            )

        val inputStreamResource = minioService.getObject(thinkResources,filePath.name)
        return if (inputStreamResource?.exists() == true) {
            inputStreamResource
        } else {
            throw FileNotFoundException("File not found ${business.businessImageProfile}")
        }
    }

    fun getBusinessCount():Long{
        return businessRepository.count()
    }
}