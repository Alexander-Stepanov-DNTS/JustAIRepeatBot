package ru.stepanov.repeatBotJustAi.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.stepanov.repeatBotJustAi.service.VkBotService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@RestController
@RequestMapping("/callback")
class VkCallbackController(
    private val vkBotService: VkBotService,
    @Value("\${vk.confirmation-code}") private val confirmationCode: String
) {

    private val logger = LoggerFactory.getLogger(VkCallbackController::class.java)

    companion object {
        private const val TYPE_CONFIRMATION = "confirmation"
        private const val TYPE_MESSAGE_NEW = "message_new"
        private const val TYPE_MESSAGE_REPLY = "message_reply"
        private const val RESPONSE_OK = "ok"
        private const val FIELD_TYPE = "type"
    }

    @PostMapping
    fun handleCallback(@RequestBody payload: Map<String, Any>): String {
        val type = payload[FIELD_TYPE] as? String
        if (type == null) {
            logger.error("Не указан тип события в payload: $payload")
            return RESPONSE_OK
        }

        return when (type) {
            TYPE_CONFIRMATION -> {
                logger.info("Получен запрос на подтверждение сервера от VK")
                confirmationCode
            }

            TYPE_MESSAGE_NEW -> {
                logger.info("Получено новое сообщение: $payload")
                vkBotService.processMessage(payload)
                RESPONSE_OK
            }

            TYPE_MESSAGE_REPLY -> {
                logger.info("Получен message_reply.")
                RESPONSE_OK
            }

            else -> {
                logger.warn("Получено неподдерживаемое событие: $type")
                RESPONSE_OK
            }
        }
    }
}
