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
//        messagesImpl.createItem(messageInput)
        val baseUrl = "${botInfo.botUrl}:${botInfo.botPort}/${botInfo.botPath}"
        logger.info("the url: {}", baseUrl)
        return BotCreateMessage(baseUrl, messageInput, botInformationId, conversational!!)
    }

    fun userCreateMessageInConversation(
        conversationId:String,
        message: MessageRequest
    ): List<Message>? {

        val userId = userManagementService.loggedInUser()!!
        val conversational:Conversation?
        val messageInput = Message(
            message.content,
            userId,
            MessageEnum.USER_MESSAGE
        )


        val conversationExists = conversationImpl.existByConversationId(conversationId)
        logger.info("conversation exists: ${conversationExists}")
        if(!conversationExists){
            val conversationModel = Conversation(
                userId,
                conversationId
            )
            logger.info("creating conversation if it does not exist")
            conversational = conversationImpl.createItem(conversationModel)
        } else {
            conversational = conversationImpl.getItemById(conversationId)

//            conversational?.messages?.add(messageItem)
            logger.info("adding message from user: {}", messageInput.toString())
        }
        messageInput.conversationId = conversational?.id

        val botInfo = botInfoImpl.getItemById(conversational.botInformationId!!)
        logger.info("get bot info from conversation id: ${botInfo}")
        val baseUrl = "${botInfo.botUrl}:${botInfo.botPort}/${botInfo.botPath}"
        logger.info("the url: {}", baseUrl)
        return BotCreateMessage(baseUrl, messageInput, botInfo.id!!, conversational!!)
    }


    fun  BotCreateMessage(
        urlString: String,
        messageInput: Message,
        botInformationId: String,
        responseConversation: Conversation
    ): List<Message>? {
        val messageCount = messagesImpl.countAllByConversationId(responseConversation.id!!)
        logger.info("message Count: $messageCount")
        val messageHistory:List<String>
        if(messageCount>1L) {
            messageHistory = messagesImpl.findAllByConversationIdListString(responseConversation.id!!)
        } else{
            messageHistory = emptyList()
        }
        logger.info("message history: $messageHistory")
        logger.info("making bot request")
       val response =  botService.interactWithBot(urlString, ActualBotInput(user_input = messageInput.content, chat_History = messageHistory))
        logger.info("response: $response")
        logger.info("storing input ${messageInput}")
        messagesImpl.createItem(messageInput)
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
            messagesImpl.createItem(mess)
//            responseConversation.messages.add(messItem)
            return messagesImpl.findAllByConversationId(responseConversation.id!!)
        } else {
            logger.error("something went wrong: {}", response.toString())
            val errorMessage = Message(content = response?.body?.response?: "error", sender = "server", messageType = MessageEnum.SYSTEM_MESSAGE)
            messagesImpl.createItem(errorMessage)
            return mutableListOf(errorMessage)
        }
    }

}