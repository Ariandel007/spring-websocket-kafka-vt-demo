package com.ws.websocketdemo.interceptor;

import com.ws.websocketdemo.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtils jwtUtils;

    @Autowired
    public JwtHandshakeInterceptor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        List<String> authHeaders = request.getHeaders().getOrEmpty("Authorization");

        if (authHeaders.size() == 1 && authHeaders.getFirst().startsWith("Bearer ")) {
            String token = authHeaders.getFirst().substring(7);
            try {
                // Validar el token JWT
                boolean isValid = jwtUtils.validateToken(token); // Implementa tu lógica de validación aquí
                if (isValid) {
                    attributes.put("user", jwtUtils.getUsernameFromToken(token)); // Extraer información del token
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return false; // Rechaza la conexión si el token no es válido
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
