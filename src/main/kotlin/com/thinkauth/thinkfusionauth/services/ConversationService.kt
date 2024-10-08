package com.thinkauth.thinkfusionauth.services

import com.thinkauth.thinkfusionauth.entities.BotInformation
import com.thinkauth.thinkfusionauth.entities.BotTypeEnum
import com.thinkauth.thinkfusionauth.entities.Conversation
import com.thinkauth.thinkfusionauth.entities.Message
import com.thinkauth.thinkfusionauth.models.requests.ActualBotInput
import com.thinkauth.thinkfusionauth.models.requests.MessageEnum
import com.thinkauth.thinkfusionauth.models.requests.MessageRequest
import com.thinkauth.thinkfusionauth.models.requests.NoHistoryBotInput
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

    fun userCreateMessageInConversation(
        conversationId: String, message: MessageRequest
    ): List<Message>? {

        val userId = userManagementService.loggedInUser()!!
        val messageInput = Message(
            message.content, userId, MessageEnum.USER_MESSAGE
        )


        val conversationExists = conversationImpl.existByConversationId(conversationId)
        logger.info("conversation exists: ${conversationExists}")

        val conversational = conversationImpl.getItemById(conversationId)
        messageInput.conversationId = conversational.id

        val botInfo = botInfoImpl.getItemById(conversational.botInformationId)
        logger.info("get bot info from conversation id: ${botInfo}")
        val baseUrl = "${botInfo.botUrl}:${botInfo.botPort}/${botInfo.botPath}"
        logger.info("the url: {}", baseUrl)
        return botCreateMessage(baseUrl, messageInput, botInfo, conversational, userId)
    }


    fun botCreateMessage(
        urlString: String,
        messageInput: Message,
        botInformation: BotInformation,
        responseConversation: Conversation,
        userInformation: String
    ): List<Message>? {
        val messageCount = messagesImpl.countAllByConversationId(responseConversation.id!!)
        logger.info("message Count: $messageCount")
        val messageHistory: List<String>
        if (messageCount > 1L) {
            messageHistory = messagesImpl.findAllByConversationIdListString(responseConversation.id!!)
        } else {
            messageHistory = emptyList()
        }
        logger.info("message history: $messageHistory")
        logger.info("making bot request")
        when(botInformation.botType){
            BotTypeEnum.HISTORY -> {
                val response = botService.interactWithBot(
                    urlString,
                    ActualBotInput(user_input = messageInput.content, chat_History = messageHistory)
                )

                logger.info("response: $response")
                logger.info("storing input ${messageInput}")
                messagesImpl.createItem(messageInput)
                if (response.statusCodeValue == 200) {


                    logger.info("successful response")
                    val botAnswer = response.body?.response

                    val mess = Message(
                        botAnswer ?: "", botInformation.id!!, MessageEnum.BOT_MESSAGE
                    )
                    mess.conversationId = responseConversation.id
                    logger.info("message to be added to db: {}", mess)
                    messagesImpl.createItem(mess)
//            responseConversation.messages.add(messItem)
                    return messagesImpl.findAllByConversationId(responseConversation.id!!)
                } else {
                    logger.error("something went wrong: {}", response.toString())
                    val errorMessage = Message(
                        content = response.body?.response ?: "error",
                        sender = "server",
                        messageType = MessageEnum.SYSTEM_MESSAGE
                    )
                    messagesImpl.createItem(errorMessage)
                    return mutableListOf(errorMessage)
                }
            }
            BotTypeEnum.NONHISTORY -> {
                val response = botService.interactWithNoHistoryBot(
                    urlString,
                    NoHistoryBotInput(user_info = userInformation, message = messageInput.content)
                )
                logger.info("NHresponse: ${response.toString()}")
                logger.info("NHstoring input ${messageInput}")
                messagesImpl.createItem(messageInput)
                if (response.statusCodeValue == 200) {
                    logger.info("successful response")
                    val botAnswer = response.body?.forEach {
                        val mess = Message(
                            it.message ?: "", botInformation.id!!, MessageEnum.BOT_MESSAGE
                        )
                        mess.conversationId = responseConversation.id
                        logger.info("message to be added to db: {}", mess)
                        messagesImpl.createItem(mess)
                    }
                    return messagesImpl.findAllByConversationId(responseConversation.id!!)
                } else {
                    logger.error("something went wrong: {}", response.toString())
                    val errorMessage = Message(
                        content = response.body?.toString() ?: "error",
                        sender = "server",
                        messageType = MessageEnum.SYSTEM_MESSAGE
                    )
                    messagesImpl.createItem(errorMessage)
                    return mutableListOf(errorMessage)
                }
            }
        }

    }


    fun deleteConversationsByUserEmail(
        email:String
    ){
        conversationImpl.deleteAllByUserEmail(email)
    }

    fun deleteAllBySender(
        sender:String
    ){
        messagesImpl.deleteAllBySender(sender)
    }
}