package com.thinkauth.thinkfusionauth.controllers


import com.thinkauth.thinkfusionauth.entities.Business
import com.thinkauth.thinkfusionauth.models.requests.BusinessRequest
import com.thinkauth.thinkfusionauth.services.BusinessService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/business")
class BusinessController(
    private val businessService: BusinessService,
) {

    @Operation(
        summary = "Add a business", description = "adds a business", tags = ["Business"]
    )
    @PostMapping("/addBusiness")
    @PreAuthorize("hasAuthority('editor') or hasAuthority('admin')")
    fun addBusiness(
        @RequestBody() businessRequest: BusinessRequest
    ):ResponseEntity<Business>{
        if(!businessService.businessExists(businessRequest)){

            return ResponseEntity(businessService.addBusiness(businessRequest),HttpStatus.OK)
        }
        return ResponseEntity(HttpStatus.CONFLICT)
    }
    @Operation(
        summary = "Add a business profile picture", description = "adds a business profile image", tags = ["Business"]
    )
    @PostMapping("/{businessId}/addBusinessProfileImage" , consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun addBusinessProfileImage(
        @PathVariable("businessId") businessId: String,
        @RequestPart("file") file:MultipartFile): ResponseEntity<String> {

        if(businessService.businessExistsById(businessId)){
            businessService.addBusinessProfilePicture(businessId,file)
            return ResponseEntity("Uploaded",HttpStatus.OK)
        }
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }
    @Operation(
        summary = "Get all business", description = "gets all business", tags = ["Business"]
    )
    @GetMapping("/businesses")
    fun getBusinesses(

    ): ResponseEntity<MutableList<Business>>{
        return ResponseEntity(businessService.getBusinesses(),HttpStatus.OK)
    }

    @Operation(
        summary = "Get a business image", description = "gets a business image", tags = ["Business"]
    )
    @GetMapping("/{businessId}")
    fun getBusinessProfileImage(
        @PathVariable("businessId") businessId: String
    ):Resource{
        return businessService.getBusinessProfilePicture(businessId)
    }
    @Operation(
        summary = "Update a business", description = "Updates a business", tags = ["Business"]
    )
    @PutMapping("/updateBusiness/{businessId}")
    fun updateBusiness(
        @PathVariable("businessId") businessId:String,
        @RequestBody() businessRequest: BusinessRequest
    ):ResponseEntity<Business?>{
        if(businessService.businessExistsById(businessId)) {
            return ResponseEntity(businessService.updateBusiness(businessId, businessRequest), HttpStatus.OK)
        }
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @Operation(
        summary = "Delete a business", description = "Deletes a business", tags = ["Business"]
    )
    @DeleteMapping("/deleteBusiness/{businessId}")
    @PreAuthorize("hasAuthority('editor') or hasAuthority('admin')")
    fun deleteBusiness(
        @PathVariable("businessId") businessId: String
    ): ResponseEntity<HttpStatus> {

        businessService.deleteBusinessById(businessId)
        return ResponseEntity(HttpStatus.GONE)
    }
}