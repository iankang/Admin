package com.thinkauth.thinkfusionauth.entities

import com.thinkauth.thinkfusionauth.entities.enums.AgeRangeEnum
import com.thinkauth.thinkfusionauth.entities.enums.GenderState
import com.thinkauth.thinkfusionauth.entities.enums.PromptType
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.DocumentReference

@Document
class PromptEntity(
    var title:String? = null,
    var promptType:PromptType? = null,
    var ageRangeEnum: AgeRangeEnum? = null,
    var genderState: GenderState? = null,
    var url:String? = null,
    var description:String? = null,
    @Indexed
    @DocumentReference var language: Language? = null,
    @DocumentReference var dialect: Dialect? = null,
    @DocumentReference var business: Business? = null
):AuditMetadata(){
    var needUploads:Boolean = true
}
