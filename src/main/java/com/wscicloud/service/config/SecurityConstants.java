package com.wscicloud.service.config;

import org.springframework.http.HttpHeaders;

/**
 * Constantes de securite JWT locales au service cashier.
 *
 * <p>IMPORTANT : Le secret JWT doit etre injecte via une variable d'environnement
 * {@code JWT_SECRET} ou via Spring {@code @ConfigurationProperties}.
 */
public class SecurityConstants {
    // Signing key for HS512 algorithm
    public static final String JWT_SECRET = "n2r5u8x/A%D*G-KaPdSgVkYp3s6v9y$B&E(H+MbQeThWmZq4t7w!z%C*F-J@NcRf";
    /** En-tete HTTP portant le jeton JWT. */
    public static final String TOKEN_HEADER = HttpHeaders.AUTHORIZATION;
    /** Prefixe du jeton Bearer. */
    public static final String TOKEN_PREFIX = "Bearer ";

    public static final String TOKEN_TYPE = "JWT";
    public static final String TOKEN_ISSUER = "secure-api";
    public static final String TOKEN_AUDIENCE = "secure-app";

    private SecurityConstants() {
        throw new IllegalStateException("Cannot create instance of static util class");
    }
}
