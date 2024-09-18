package com.thinkauth.thinkfusionauth.services

import com.thinkauth.thinkfusionauth.entities.SentenceUserIgnore
import com.thinkauth.thinkfusionauth.exceptions.ResourceNotFoundException
import com.thinkauth.thinkfusionauth.repository.impl.SentenceIgnoreImpl
import org.springframework.stereotype.Service

@Service
class SentenceUserIgnoreService(
    private val userIgnoreImpl: SentenceIgnoreImpl, private val audioCollectionService: AudioCollectionService
) {

    fun addSentenceUserIgnore(
        userId: String, sentenceId: String
    ): SentenceUserIgnore {

        return if (!userIgnoreImpl.existsBySentenceIdAndUserId(sentenceId, userId)) {
            userIgnoreImpl.createItem(SentenceUserIgnore(sentenceId, userId))
        } else {
            userIgnoreImpl.findBySentenceIdAndUserid(sentenceId, userId)
        }
    }


    fun removeSentenceUserIgnore(
        userId: String, sentenceId: String
    ) {
        if (userIgnoreImpl.existsBySentenceIdAndUserId(sentenceId, userId)) {
            val sentenceIgnore = userIgnoreImpl.findBySentenceIdAndUserid(sentenceId, userId)
            userIgnoreImpl.deleteItemById(sentenceIgnore.id!!)
        } else {
            throw ResourceNotFoundException("resource with sentenceId: ${sentenceId} and userId: ${userId} not found")
        }
    }

    fun getAllUserIgnoredSentences(
        userId: String
    ): List<String> {
        return userIgnoreImpl.findAllByUserId(userId).map { it.sentenceId }
    }
}