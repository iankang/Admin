package com.thinkauth.thinkfusionauth.repository


import com.thinkauth.thinkfusionauth.entities.Business
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface BusinessRepository :MongoRepository<Business,String> {
    fun existsByBusinessName(businessName:String):Boolean
//    fun findAllByBusinessId(businessId: String, pageable: Pageable): Page<Surveys>
//
//    fun existsBySurveyName(surveyName: String):Boolean
//
}