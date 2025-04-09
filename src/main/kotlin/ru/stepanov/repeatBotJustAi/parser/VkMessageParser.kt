package ru.stepanov.repeatBotJustAi.parser

import org.springframework.stereotype.Component
import ru.stepanov.repeatBotJustAi.dto.IncomingVkMessage
import ru.stepanov.repeatBotJustAi.exeptions.ParseFailedException

@Component
class VkMessageParser {
    fun parse(payload: Map<String, Any>): IncomingVkMessage {
        val dat = payload["object"] as? Map<*, *>
        val message = dat?.get("message") as? Map<*, *>
        val text = message?.get("text") as? String
        val peerIdRaw = message?.get("peer_id") ?: message?.get("from_id")
        val peerId = (peerIdRaw as? Number)?.toLong()

        if (text.isNullOrEmpty() || peerId == null) {
            throw ParseFailedException("Failed to parse VK payload: $payload")
        }

        return IncomingVkMessage(peerId, text)
    }
}
