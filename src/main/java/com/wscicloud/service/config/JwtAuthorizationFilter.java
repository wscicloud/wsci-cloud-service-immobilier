package com.wscicloud.service.config;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthorizationFilter.class);

    // ✅ JwtParser initialisé une seule fois au démarrage (thread principal, bon classloader)
    //    Évite l'UnknownClassException de jjwt sur les virtual threads Java 21
    private final JwtParser jwtParser;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
        this.jwtParser = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(SecurityConstants.JWT_SECRET.getBytes()))
                .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        if (authentication == null) {
            filterChain.doFilter(request, response);
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(SecurityConstants.TOKEN_HEADER);

        if (StringUtils.isNotBlank(token) && token.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            try {
                // ✅ Réutilisation du JwtParser pré-construit (thread-safe selon la doc jjwt)
                Jws<Claims> parsedToken = jwtParser.parseSignedClaims(token.replace("Bearer ", ""));

                // ✅ JJWT 0.12.x : getPayload() remplace getBody()
                String username = parsedToken.getPayload().getSubject();

                List<SimpleGrantedAuthority> authorities = ((List<?>)
                                parsedToken.getPayload().get("rol"))
                        .stream()
                                .map(authority -> new SimpleGrantedAuthority((String) authority))
                                .collect(Collectors.toList());

                if (StringUtils.isNotBlank(username)) {
                    return new UsernamePasswordAuthenticationToken(username, null, authorities);
                }
            } catch (ExpiredJwtException exception) {
                log.warn("Request to parse expired JWT : {} failed : {}", token, exception.getMessage());
            } catch (UnsupportedJwtException exception) {
                log.warn("Request to parse unsupported JWT : {} failed : {}", token, exception.getMessage());
            } catch (MalformedJwtException exception) {
                log.warn("Request to parse invalid JWT : {} failed : {}", token, exception.getMessage());
            } catch (SignatureException exception) {
                log.warn("Request to parse JWT with invalid signature : {} failed : {}", token, exception.getMessage());
            } catch (IllegalArgumentException exception) {
                log.warn("Request to parse empty or null JWT : {} failed : {}", token, exception.getMessage());
            }
        }

        return null;
    }
}
