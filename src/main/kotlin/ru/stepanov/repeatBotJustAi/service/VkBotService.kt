package ru.stepanov.repeatBotJustAi.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.stepanov.repeatBotJustAi.client.VkApiClient
import ru.stepanov.repeatBotJustAi.exeptions.ParseFailedException
import ru.stepanov.repeatBotJustAi.parser.VkMessageParser

@Service
class VkBotService(private val vkApiClient: VkApiClient, private val vkMessageParser: VkMessageParser) {

    private val logger = LoggerFactory.getLogger(VkBotService::class.java)

    fun processMessage(payload: Map<String, Any>) {
        try {
            val message = vkMessageParser.parse(payload)
            val responseText = "Вы сказали: ${message.text}"
            vkApiClient.sendMessage(message.peerId, responseText)
        } catch (e: ParseFailedException) {
            logger.warn("Ошибка при парсинге входящего сообщения: ${e.message}")
        }
    }
}
