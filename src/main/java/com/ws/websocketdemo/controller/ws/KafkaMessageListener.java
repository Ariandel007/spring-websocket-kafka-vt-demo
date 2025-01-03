package com.ws.websocketdemo.controller.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessageListener {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "${KAFKA_MESSAGE_CHAT_TOPIC}", groupId = "${KAFKA_GROUP_ID}")
    public void listen(String message) {
        // Enviar mensaje a todos los suscriptores WebSocket
        messagingTemplate.convertAndSend("/topic/messages", message);
    }
}