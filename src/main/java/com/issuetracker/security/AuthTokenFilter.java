package com.issuetracker.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // #region agent log
        try { java.nio.file.Files.write(java.nio.file.Paths.get("/app/debug.log"), (new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(java.util.Map.of("sessionId","debug-session","hypothesisId","A,B,D","location","AuthTokenFilter.java:31","message","Request intercepted","data",java.util.Map.of("requestUri",request.getRequestURI(),"method",request.getMethod(),"hasAuthHeader",request.getHeader("Authorization")!=null),"timestamp",System.currentTimeMillis()))+"\n").getBytes(), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND); } catch(Exception e) {}
        // #endregion
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUsernameFromJwtToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                // #region agent log
                try { java.nio.file.Files.write(java.nio.file.Paths.get("/app/debug.log"), (new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(java.util.Map.of("sessionId","debug-session","hypothesisId","E","location","AuthTokenFilter.java:41","message","Authentication set","data",java.util.Map.of("username",username),"timestamp",System.currentTimeMillis()))+"\n").getBytes(), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND); } catch(Exception e) {}
                // #endregion
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e.getMessage());
            // #region agent log
            try { java.nio.file.Files.write(java.nio.file.Paths.get("/app/debug.log"), (new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(java.util.Map.of("sessionId","debug-session","hypothesisId","E","location","AuthTokenFilter.java:44","message","Authentication error","data",java.util.Map.of("error",e.getMessage()),"timestamp",System.currentTimeMillis()))+"\n").getBytes(), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND); } catch(Exception ex) {}
            // #endregion
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}
