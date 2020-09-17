package com.nowakArtur97.globalTerrorismAPI.configuration;

import com.nowakArtur97.globalTerrorismAPI.feature.user.CustomUserDetailsService;
import com.nowakArtur97.globalTerrorismAPI.filter.ExceptionHandlerFilter;
import com.nowakArtur97.globalTerrorismAPI.filter.JwtRequestFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailsService customUserDetailsService;

    private final JwtRequestFilter jwtRequestFilter;

    private final ExceptionHandlerFilter exceptionHandlerFilter;

    @Value("${jwt.ignoredAntMatchers}")
    private String[] ignoredAntMatchers;

    @Bean
    BCryptPasswordEncoder getBCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    DaoAuthenticationProvider getDaoAuthenticationProvider() {

        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

        daoAuthenticationProvider.setUserDetailsService(customUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(getBCryptPasswordEncoder());

        return daoAuthenticationProvider;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {

        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .cors()
                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(ignoredAntMatchers)
                .permitAll()
                .antMatchers("/api/v1/**").authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(exceptionHandlerFilter, CorsFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {

        auth.authenticationProvider(getDaoAuthenticationProvider());
    }
}
