package com.thinkauth.thinkfusionauth.models.responses

import com.opencsv.bean.CsvBindByName
import com.opencsv.bean.CsvBindByPosition

data class SentenceDocumentCSV(
    @CsvBindByPosition(position = 0)
    var serialNumber:String? = null,
    @CsvBindByPosition(position = 1)
    var source:String? = null,
    @CsvBindByPosition(position = 2)
    var topic:String? =  null,
    @CsvBindByPosition(position = 3)
    var textTranslation:String? = null,
    @CsvBindByPosition(position = 4)
    var localLanguage:String? = null,
    @CsvBindByPosition(position = 5)
    var language:String? = null,
    @CsvBindByPosition(position = 6)
    var dialect:String? = null,

)
