package br.com.chatbot.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final AuthCookieTokenFilter authCookieTokenFilter;

    @Value("${backend.environment.prod.enabled:true}")
    private boolean environmentProdEnabled;

    @Autowired
    public WebSecurityConfig(AuthCookieTokenFilter authCookieTokenFilter) {
        this.authCookieTokenFilter = authCookieTokenFilter;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        if (environmentProdEnabled) {
            httpSecurity.requiresChannel()
                    .requestMatchers(r -> r.getHeader("X-Forwarded-Proto") != null)
                    .requiresSecure();
        }

        httpSecurity.csrf()
                    .disable()
                    .cors()
                    .and()
                    .authorizeRequests()
                        .antMatchers("/js/**", "/css/**", "/img/**", "/web/**", "/auth", "/forgot-password",
                                "/reset-password", "/api/monitoring/create-log/**", "/api/message/create-log/**",
                                "/api/user/create-user")
                            .permitAll()
                        .antMatchers("/validate-token", "/do-logout", "/api/user/get-own-user/**",
                                "/api/user/edit-own-user", "/api/user/edit-own-password", "/api/content/**", "/api/log/**",
                                "/api/monitoring/**", "/api/sector/get-all-active-sectors", "/api/topic/**", "/api/user/report/**")
                            .hasAnyAuthority("Administrador", "Especialista")
                        .antMatchers("/api/user/**", "/api/profile/**", "/api/sector/**")
                            .hasAuthority("Administrador")
                    .anyRequest().authenticated()
                    .and()
                    .addFilter(authCookieTokenFilter)
                    .addFilterBefore(new ExceptionTranslationFilter(new Http403ForbiddenEntryPoint()),
                            authCookieTokenFilter.getClass());
    }
}
