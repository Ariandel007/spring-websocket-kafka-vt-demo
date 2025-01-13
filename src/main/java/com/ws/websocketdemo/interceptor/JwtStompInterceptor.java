package com.ws.websocketdemo.interceptor;

import com.ws.websocketdemo.utils.JwtUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
public class JwtStompInterceptor implements ChannelInterceptor {

    private final JwtUtils jwtUtils;

    public JwtStompInterceptor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                if (jwtUtils.validateToken(token)) {
                    String username = jwtUtils.getUsernameFromToken(token);

                    // Establecer el usuario en la conexión
                    accessor.setUser(() -> username);

                    // Almacenar información adicional en simpSessionAttributes
                    accessor.getSessionAttributes().put("username", username); // Agregar el nombre de usuario
//                    accessor.getSessionAttributes().put("roles", jwtUtils.getRolesFromToken(token)); // Agregar roles, si es necesario o existieran

                    return message; // Permitir la conexión
                }
            }

            throw new IllegalArgumentException("Token inválido o no proporcionado");
        }

        return message; // Permitir otros comandos STOMP
    }

}
