package com.thinkauth.thinkfusionauth.services

import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.thinkauth.thinkfusionauth.models.requests.ActualBotInput

import com.thinkauth.thinkfusionauth.models.requests.BotInput
import com.thinkauth.thinkfusionauth.models.responses.GeneralBotResponse
import org.springframework.http.*
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.client.RestTemplate
import java.net.URI

@Service
class BotService(
    private val restTemplate: RestTemplate = RestTemplate()
) {

    init {
        val converter = MappingJackson2HttpMessageConverter()
        converter.supportedMediaTypes = listOf(
            MediaType.APPLICATION_OCTET_STREAM
        )
        converter.objectMapper = ObjectMapper().configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES,true)
        restTemplate.messageConverters.add(converter)

    }
//    fun interactWithBot(s: String, @RequestBody input: ActualBotInput): ResponseEntity<GeneralBotResponse> {
//        val headers =  HttpHeaders()
//        headers.contentType = MediaType.APPLICATION_JSON
//        val entity = HttpEntity<ActualBotInput>(input,headers)
//
//        return restTemplate.postForEntity(s,  entity, GeneralBotResponse::class.java)
//    }
    fun interactWithBot(s: String, @RequestBody input: ActualBotInput): ResponseEntity<GeneralBotResponse> {
        val headers =  HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity<ActualBotInput>(input,headers)

        return ResponseEntity( restTemplate.postForObject(URI.create(s) ,  input, GeneralBotResponse::class.java), HttpStatus.OK)
    }
}