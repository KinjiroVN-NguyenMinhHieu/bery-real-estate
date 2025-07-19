package com.devcamp.bery_real_estate.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.devcamp.bery_real_estate.security.jwt.AuthEntryPointJwt;
import com.devcamp.bery_real_estate.security.jwt.AuthTokenFilter;
import com.devcamp.bery_real_estate.security.services.UserDetailsServiceImpl;

@Configuration
@EnableGlobalMethodSecurity(
    // securedEnabled = true,
    // jsr250Enabled = true,
    prePostEnabled = true)
public class WebSecurityConfig { // extends WebSecurityConfigurerAdapter {
  // Inject service để xác thực người dùng
  @Autowired
  UserDetailsServiceImpl userDetailsService;

  // Inject entry point để xử lý lỗi xác thực không thành công
  @Autowired
  private AuthEntryPointJwt unauthorizedHandler;

  // Bean để tạo ra filter xác thực JWT
  @Bean
  public AuthTokenFilter authenticationJwtTokenFilter() {
    return new AuthTokenFilter();
  }

//  @Override
//  public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
//    authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
//  }
  
  // Bean để cung cấp authentication provider
  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
      DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
       
      authProvider.setUserDetailsService(userDetailsService);
      authProvider.setPasswordEncoder(passwordEncoder());
   
      return authProvider;
  }

//  @Bean
//  @Override
//  public AuthenticationManager authenticationManagerBean() throws Exception {
//    return super.authenticationManagerBean();
//  }
  
  // Bean để cung cấp authentication manager
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }

  // Bean để cung cấp encoder cho password
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
  
  // Bean để cấu hình filter chain cho HTTP security
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.cors().and().csrf().disable()
        .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
        .authorizeRequests()
        .antMatchers("/auth/register").permitAll()
        .antMatchers("/auth/login").permitAll()
        .antMatchers("/auth/forgot-password").permitAll()
        .antMatchers("/auth/reset-password").permitAll()
        .antMatchers(
    "/", "/index.html", "/favicon.ico",
            "/**/*.html", "/**/*.css", "/**/*.js",
            "/**/*.png", "/**/*.jpg", "/**/*.jpeg", "/**/*.gif", "/**/*.svg",
            "/**/*.woff", "/**/*.woff2", "/**/*.ttf", "/**/*.eot",
            "/images/**", "/assets/**", 
            "/admin/dist/**", "/admin/plugins/**" 
        ).permitAll()
        .antMatchers(HttpMethod.GET, "/real-estates/**").permitAll()
        .antMatchers(HttpMethod.GET, "/provinces").permitAll()
        .antMatchers(HttpMethod.GET, "/province/**").permitAll()
        .antMatchers(HttpMethod.GET, "/district/**").permitAll()
        .anyRequest().authenticated();
    
    // Thêm authentication provider và filter JWT vào filter chain
    http.authenticationProvider(authenticationProvider());
    http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    
    return http.build();
  }
}
