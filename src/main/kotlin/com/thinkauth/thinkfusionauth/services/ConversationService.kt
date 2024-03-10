package com.thinkauth.thinkfusionauth.services

import com.thinkauth.thinkfusionauth.entities.Conversation
import com.thinkauth.thinkfusionauth.entities.Message
import com.thinkauth.thinkfusionauth.models.requests.ActualBotInput
import com.thinkauth.thinkfusionauth.models.requests.MessageEnum
import com.thinkauth.thinkfusionauth.models.requests.MessageRequest
import com.thinkauth.thinkfusionauth.repository.impl.BotInfoImpl
import com.thinkauth.thinkfusionauth.repository.impl.ConversationImpl
import com.thinkauth.thinkfusionauth.repository.impl.MessagesImpl
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ConversationService(
    private val conversationImpl: ConversationImpl,
    private val userManagementService: UserManagementService,
    private val messagesImpl: MessagesImpl,
    private val botService: BotService,
    private val botInfoImpl: BotInfoImpl
) {

    val logger = LoggerFactory.getLogger(ConversationService::class.java)
    fun UserCreateMessage(
        botInformationId:String,
        message: MessageRequest
    ): List<Message>? {
        val botInfo = botInfoImpl.getItemById(botInformationId)
        val userId = userManagementService.loggedInUser()!!
        val conversational:Conversation?


        val messageInput = Message(
            message.content,
            userId,
            MessageEnum.USER_MESSAGE
        )


        val conversationExists = conversationImpl.existsByUserId(userId)

        if(!conversationExists){
            val conversationModel = Conversation(
                userId,
                botInformationId
            )
            logger.info("creating conversation if it does not exist")
            conversational = conversationImpl.createItem(conversationModel)
        } else {
            conversational = conversationImpl.getConversation(botInformationId, userId)

//            conversational?.messages?.add(messageItem)
            logger.info("adding message from user: {}", messageInput.toString())
        }
        messageInput.conversationId = conversational?.id
        messagesImpl.createItem(messageInput)
        val baseUrl = "http://${botInfo.botUrl}:${botInfo.botPort}/${botInfo.botPath}"
        logger.info("the url: {}", baseUrl)
        return BotCreateMessage(baseUrl, message.content, botInformationId, conversational!!)
    }


    fun  BotCreateMessage(
        urlString: String,
        content: String,
        botInformationId: String,
        responseConversation: Conversation
    ): List<Message>? {
        logger.info("making bot request")
       val response =  botService.interactWithBot(urlString, ActualBotInput(user_input = content))
        if(response.statusCodeValue == 200){
            logger.info("successful response")
            val botAnswer = response.body?.response

            val mess = Message(
                botAnswer ?: "",
                botInformationId,
                MessageEnum.BOT_MESSAGE
            )
            mess.conversationId = responseConversation.id
            logger.info("message to be added to db: {}", mess)
            val messItem = messagesImpl.createItem(mess)
//            responseConversation.messages.add(messItem)
            return messagesImpl.findAllByConversationId(responseConversation.id!!)
        } else {
            logger.error("something went wrong: {}", response.toString())
        }
        return null
    }

}