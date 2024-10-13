package com.thinkauth.thinkfusionauth.repository

import com.thinkauth.thinkfusionauth.entities.ConstituencyEntity
import org.springframework.data.mongodb.repository.MongoRepository

interface ConstituencyRepository:MongoRepository<ConstituencyEntity,String> {

    fun findAllByCountyId(countyId:Int):List<ConstituencyEntity>

    fun findAllByCountyName(countyName:String):List<ConstituencyEntity>

    fun findAllByConstituencyName(constituencyName:String):List<ConstituencyEntity>
}