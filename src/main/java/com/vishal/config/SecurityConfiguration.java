package com.vishal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
	
	PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
	
	@Bean
    public InMemoryUserDetailsManager userDetailsService() {
		UserDetails user = User.withUsername("user")
			.password(encoder.encode("password"))
			.roles("USER")
			.build();
		return new InMemoryUserDetailsManager(user);
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests(authorize -> authorize
				.antMatchers("/loginGoogle").authenticated()
				.antMatchers("/*").permitAll() // this must be after that url which need oauth2
			)
			.oauth2Login(Customizer.withDefaults())
			.logout(logout -> logout
				.logoutSuccessUrl("/").permitAll()
			);
		return http.build();
	}
	
	@Bean
	public PasswordEncoder encoder() {
	    return new BCryptPasswordEncoder();
	}
	
}
