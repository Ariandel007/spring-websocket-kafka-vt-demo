package com.ws.websocketdemo.config;

import com.ws.websocketdemo.interceptor.JwtStompInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtStompInterceptor jwtStompInterceptor;

    @Autowired
    public WebSocketConfig(JwtStompInterceptor jwtStompInterceptor) {
        this.jwtStompInterceptor = jwtStompInterceptor;
    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // Broker interno
        config.setApplicationDestinationPrefixes("/app"); // Prefix for incoming messages
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // para conectarse con la libreria de WebSockets de forma directa (ws://)
        registry.addEndpoint("/chat")
                .setAllowedOriginPatterns("http://127.0.0.1:5500","http://localhost:4200"); // Replace with specific allowed origins
        // Pata soportar SockJS (http://)
        registry.addEndpoint("/chat")
                .setAllowedOriginPatterns("http://127.0.0.1:5500", "http://localhost:4200") // Replace with specific allowed origins
                .withSockJS(); // WebSocket endpoint
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(jwtStompInterceptor); // Registrar el interceptor STOMP
    }
}

//El servidor expone un endpoint en /chat para conexiones WebSocket.
//Los mensajes de los clientes serán enviados a rutas con prefijo /app.
//Los mensajes salientes se distribuirán a los clientes en /topic.

// In the browser If someone wants to use SockJS client to connect, they can connect using:
//
//var socket = new SockJS('http://localhost:8080/test');
//stompClient = Stomp.over(socket);
//
//If someone wants to use just a plain Websocket object to connect, use the following:
//
//stompClient = Stomp.client('ws://localhost:8080/test');

//Si queremos testear en postman podemos hacer lo siguiente:
//Conectarse a ws://localhost:8345/chat
//Enviando mensajes STOMP manualmente (el espacio antes de ^@ es importante):
//a) Mensaje de Conexión
//CONNECT
//accept-version:1.1,1.0
//heart-beat:10000,10000
//
//^@
//b) Suscripción a un Topic
//SUBSCRIBE
//id:sub-0
//destination:/topic/messages
//
//^@
//c) Enviar un Mensaje
//SEND
//destination:/app/message
//content-length:51
//
//{"sender":"alex","content":"test","timestamp":null}
//^@
//d) Desconexión
//DISCONNECT
//
//^@


