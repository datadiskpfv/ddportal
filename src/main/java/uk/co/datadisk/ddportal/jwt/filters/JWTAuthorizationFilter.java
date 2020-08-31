package uk.co.datadisk.ddportal.jwt.filters;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import uk.co.datadisk.ddportal.jwt.JWTTokenProvider;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static uk.co.datadisk.ddportal.constants.SecurityConstant.OPTIONS_HTTP_METHOD;
import static uk.co.datadisk.ddportal.constants.SecurityConstant.TOKEN_PREFIX;

@Component
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private JWTTokenProvider jwtTokenProvider;

    public JWTAuthorizationFilter(JWTTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // OPTIONS is used to collect information about a servers application, hence why we ignore this
        if (request.getMethod().equalsIgnoreCase(OPTIONS_HTTP_METHOD)) {
            response.setStatus(HttpStatus.OK.value());
        } else {
            // Try to get the authorization header
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

            // if header is null or does not start with Bearer, move on
            if(authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_PREFIX)) {
                System.out.println("No JWT sent with request");
                chain.doFilter(request, response);
                return;
            }

            // Now we have a JWT token lets verify it
            String token = authorizationHeader.substring(TOKEN_PREFIX.length());
            String username = jwtTokenProvider.getSubject(token);

            // Make sure they don't have  security authentication already, you only have one when you login the first time
            // then we make use of JWT tokens
            if(jwtTokenProvider.isTokenValid(username, token) &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {
                List<GrantedAuthority> authorities = jwtTokenProvider.getAuthorities(token);
                Authentication authentication = jwtTokenProvider.getAuthentication(username, authorities, request);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                System.out.println("JWT is invalid for username " + username);
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(request, response);
    }
}
