package com.thinkauth.thinkfusionauth.services

import com.thinkauth.thinkfusionauth.entities.Dialect
import com.thinkauth.thinkfusionauth.models.requests.DialectRequest
import com.thinkauth.thinkfusionauth.models.responses.PagedResponse
import com.thinkauth.thinkfusionauth.repository.impl.DialectImpl
import com.thinkauth.thinkfusionauth.utils.toStandardCase
import org.springframework.stereotype.Service

@Service
class DialectService(
    private val dialectImpl: DialectImpl,
    private val languageService: LanguageService
) {

    fun addDialect(
        dialectRequest: DialectRequest
    ): Dialect {

        val language = languageService.getLanguageByLanguageId(dialectRequest.languageId!!)
        val dialect = Dialect(dialectRequest.dialectName?.toStandardCase(), language)
        return dialectImpl.createItem(dialect)
    }

    fun getDialects(
        page:Int?,
        size:Int?
    ): PagedResponse<List<Dialect>> {
        return dialectImpl.findEverythingPaged(page ?: 0,size ?: 100)
    }

    fun getDialectById(
        id:String
    ): Dialect {
        return dialectImpl.getItemById(id)
    }

    fun updateDialect(
        dialectId:String,
        item:Dialect
    ): Dialect? {
        return dialectImpl.updateItem(dialectId,item)
    }

    fun deleteDialectById(id:String){
        dialectImpl.deleteItemById(id)
    }

    fun getDialectByLanguageName(
        languageName:String
    ): List<Dialect> {
        return dialectImpl.getDialectsByLanguageName(languageName)
    }

    fun getDialectByDialectName(
        dialectName: String
    ): List<Dialect>? {
        return dialectImpl.getDialectByDialectName(dialectName)
    }

    fun countDialects():Long{
        return dialectImpl.dialectCount()
    }

    fun getDialectCountByLanguageName(
        languageName:String
    ): Long {
        return dialectImpl.countDialectsByLanguageName(languageName)
    }

    fun existsByDialectName(
        dialectName:String
    ): Boolean {
        return dialectImpl.existsByDialectName(dialectName.toStandardCase())
    }

    fun existsByDialectId(dialectId:String):Boolean{
        return dialectImpl.itemExistsById(dialectId)
    }

    fun deleteAllDialects(){
        return dialectImpl.deleteAllItems()
    }
}