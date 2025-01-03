package com.ws.websocketdemo.controller.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ws.websocketdemo.dto.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;

@Controller
class ChatController {

//    @MessageMapping("/message")
//    @SendTo("/topic/messages")
//    public ChatMessage handleMessage(ChatMessage message, @Header("simpSessionAttributes") Map<String, Object> sessionAttributes) {
//        String user = (String) sessionAttributes.get("user");
//        message.setSender(user);
//        // Aquí podrías agregar lógica para procesar el mensaje, como agregar un timestamp
//        message.setTimestamp(String.valueOf(System.currentTimeMillis()));
//        return message; // Difundir el mensaje procesado
//    }

    private static final String TOPIC = "chat_messages_demo";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @MessageMapping("/message")
    public void handleMessage(ChatMessage message, @Header("simpSessionAttributes") Map<String, Object> sessionAttributes) {
        String user = (String) sessionAttributes.get("user");
        message.setSender(user);
        // Aquí podrías agregar lógica para procesar el mensaje, como agregar un timestamp
        message.setTimestamp(String.valueOf(System.currentTimeMillis()));
        // Publicar el mensaje en Kafka
        kafkaTemplate.send(TOPIC, convertMessageToJson(message));
    }

    public String convertMessageToJson(Object message) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}