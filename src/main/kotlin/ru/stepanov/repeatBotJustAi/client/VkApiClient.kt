package ru.stepanov.repeatBotJustAi.client

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class VkApiClient(
    private val webClient: WebClient.Builder
) {

    @Value("\${vk.access-token}")
    private lateinit var accessToken: String

    @Value("\${vk.api-version}")
    private lateinit var apiVersion: String

    private val logger = LoggerFactory.getLogger(VkApiClient::class.java)

    companion object {
        private const val VK_MESSAGES_SEND_URL = "https://api.vk.com/method/messages.send"
        private const val PARAM_PEER_ID = "peer_id"
        private const val PARAM_MESSAGE = "message"
        private const val PARAM_TOKEN = "access_token"
        private const val PARAM_VERSION = "v"
        private const val PARAM_RANDOM_ID = "random_id"
        private const val PARAM_RESPONSE = "\"response\""
    }

    fun sendMessage(peerId: Long, message: String) {
        val uri = VK_MESSAGES_SEND_URL

        val formData: MultiValueMap<String, String> = LinkedMultiValueMap()
        formData.add(PARAM_PEER_ID, peerId.toString())
        formData.add(PARAM_MESSAGE, message)
        formData.add(PARAM_TOKEN, accessToken)
        formData.add(PARAM_VERSION, apiVersion)
        formData.add(PARAM_RANDOM_ID, System.currentTimeMillis().toInt().toString())

        webClient.build()
            .post()
            .uri(uri)
            .body(BodyInserters.fromFormData(formData))
            .retrieve()
            .bodyToMono(String::class.java)
            .doOnNext { response ->
                if (!response.contains(PARAM_RESPONSE)) {
                    logger.warn("Нет ответа")
                }
            }
            .doOnError { error ->
                logger.error("Ошибка при отправке сообщения в VK", error)
            }
            .onErrorResume { error ->
                logger.warn("Перехваченная ошибка при запросе: ${error.message}")
                Mono.empty()
            }
            .subscribe()
    }
}
