package com.thinkauth.thinkfusionauth.repository

import com.thinkauth.thinkfusionauth.entities.TotalHoursEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TotalHourRepository:MongoRepository<TotalHoursEntity,String>{
}