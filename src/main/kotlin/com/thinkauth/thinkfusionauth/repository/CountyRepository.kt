package com.thinkauth.thinkfusionauth.repository

import com.thinkauth.thinkfusionauth.entities.CountyEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface CountyRepository:MongoRepository<CountyEntity,String> {
}