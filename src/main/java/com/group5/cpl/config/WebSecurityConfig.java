package com.group5.cpl.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.group5.cpl.service.serviceImp.UserDetailServiceImp;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailServiceImp();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(requests -> requests
                .requestMatchers("/*").permitAll()
                .requestMatchers("/css/**").permitAll()
                .requestMatchers("/js/**").permitAll()
                .requestMatchers("/images/**").permitAll()
                .requestMatchers("/web_background/**").permitAll()
                .requestMatchers("/icons/**").permitAll()
                .requestMatchers("/page/**").permitAll()
                .requestMatchers("/movies/movie_details/**").permitAll()
                .requestMatchers("/movies/**").hasAnyAuthority("MANAGER", "EMPLOYEE")
                .requestMatchers("/movies/manage/**").hasAnyAuthority("MANAGER", "EMPLOYEE")
                .requestMatchers("/account/manage").hasAnyAuthority("MANAGER")
                .requestMatchers("/account/manage/all").hasAnyAuthority("MANAGER")
                .requestMatchers("/account/manage/all/page/**").hasAnyAuthority("MANAGER")
                .requestMatchers("/account/manage/active").hasAnyAuthority("MANAGER")
                .requestMatchers("/account/manage/active/page/**").hasAnyAuthority("MANAGER")
                .requestMatchers("/account/manage/ban").hasAnyAuthority("MANAGER")
                .requestMatchers("/account/manage/ban/page/**").hasAnyAuthority("MANAGER")
                .requestMatchers("/account/manage/**").hasAnyAuthority("MANAGER")
                .requestMatchers("employee/manage").hasAnyAuthority("MANAGER")
                .requestMatchers("employee/manage/remove/**").hasAnyAuthority("MANAGER")
                .requestMatchers("employee/manage/add/**").hasAnyAuthority("MANAGER")
                .requestMatchers("/employee/manage/search").hasAnyAuthority("MANAGER")
                .requestMatchers("/employee/manage/search-add").hasAnyAuthority("MANAGER")
                .requestMatchers("/addCinemaRoom/").hasAuthority("MANAGER")// change link
                .requestMatchers("/cinemaroom/**").hasAuthority("MANAGER")
                .requestMatchers("/cinemarooms/**").hasAuthority("MANAGER")
                .requestMatchers("/cinemarooms").hasAuthority("MANAGER")
                .requestMatchers("/updateCinemaRoom/**").hasAuthority("MANAGER")
                .requestMatchers("/invoice/**").hasAnyAuthority("MANAGER", "EMPLOYEE")
                .requestMatchers("/invoice/confirm/**").hasAnyAuthority("MANAGER", "EMPLOYEE")
                .requestMatchers("/invoice/confirm").hasAnyAuthority("MANAGER", "EMPLOYEE")
                .requestMatchers("/score_search/**").permitAll()
                .requestMatchers("/score/**").permitAll()
                .requestMatchers("/promo/add").hasAnyAuthority("MANAGER")
                .requestMatchers("/promo/manage").hasAnyAuthority("MANAGER")
                .requestMatchers("/promo/available").hasAnyAuthority("MANAGER")
                .requestMatchers("/promo/unavailable").hasAnyAuthority("MANAGER")
                .requestMatchers("/promo/update/**").hasAnyAuthority("MANAGER")
                .requestMatchers("/promo/list").permitAll()
                .requestMatchers("/seat/seatsConfig/**").hasAuthority("MANAGER")
                .requestMatchers("/seat/saveSeat/**").hasAuthority("MANAGER")

                .anyRequest().authenticated())

                .formLogin(login -> login.loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll())

                .logout(logout -> logout.logoutUrl("/logout").deleteCookies("JSESSIONID").logoutSuccessUrl("/"))

                .exceptionHandling(exception -> exception.accessDeniedPage("/denied"));
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/css/**");
    }

}
