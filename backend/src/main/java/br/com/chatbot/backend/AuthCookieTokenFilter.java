package br.com.chatbot.backend;

import br.com.chatbot.backend.dtos.AuthenticationDto;
import br.com.chatbot.backend.services.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.stereotype.Component;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Component
public class AuthCookieTokenFilter extends AbstractPreAuthenticatedProcessingFilter {
    private final AuthenticationService authenticationService;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public AuthCookieTokenFilter(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;

        setAuthenticationManager(authentication -> {
            AuthenticationDto principal = (AuthenticationDto) authentication.getPrincipal();

            if (principal == null) {
                throw new BadCredentialsException("Autenticação inválida ou expirada.");
            }

            authentication.setAuthenticated(true);
            return authentication;
        });
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest httpServletRequest) {
        if (httpServletRequest.getCookies() == null || httpServletRequest.getCookies().length == 0) {
            return null;
        }

        String token = Arrays.stream(httpServletRequest.getCookies())
                            .filter(c -> c.getName().equals("token"))
                            .map(Cookie::getValue)
                            .findFirst()
                            .orElse(null);

        if (token == null) {
            return null;
        }

        try {
            return authenticationService.validateCookie(token);
        } catch (Exception exp) {
            logger.error("Falha na autenticação", exp);
            return null;
        }
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest httpServletRequest) {
        return null;
    }
}
