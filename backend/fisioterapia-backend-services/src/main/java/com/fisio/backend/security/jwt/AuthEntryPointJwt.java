package com.fisio.backend.security.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        logger.error("Error de no autorizado: {}", authException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Construcción manual de JSON para independizarnos de Jackson 2.x/3.x
        String jsonBody = String.format(
            "{\"status\":%d,\"error\":\"Unauthorized\",\"message\":\"%s\",\"path\":\"%s\"}",
            HttpServletResponse.SC_UNAUTHORIZED,
            authException.getMessage().replace("\"", "\\\""),
            request.getServletPath().replace("\"", "\\\"")
        );

        response.getWriter().write(jsonBody);
    }
}
