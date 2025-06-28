package com.doctortsab.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {    @Override
    protected void configure(HttpSecurity http) throws Exception {        http
            .authorizeRequests()                .antMatchers("/", "/home", "/css/**", "/js/**", "/images/**", "/h2-console/**", 
                             "/*.js", "/firebase-*.js", "/auth-*.js", "/firestore-*.js", "/cursor.js", 
                             "/login", "/signup", "/dashboard/**", "/profile", "/symptom-check", "/first-aid", 
                             "/health-tips").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
            .logout()
                .permitAll();
        
        // For H2 Console
        http.csrf().disable();
        http.headers().frameOptions().disable();
    }
}
