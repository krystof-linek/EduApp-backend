package cz.mendelu.xlinek.eduapp.config;

import cz.mendelu.xlinek.eduapp.filter.MyTokenFilter;
import cz.mendelu.xlinek.eduapp.utils.CustomAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyTokenFilter myTokenFilter;

    @Autowired
    private CustomAuth customAuth;

    @Autowired
    private RestAuthenticationEntryPoint authEntryPoint;


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.
                cors()
                .and()//.httpBasic()..and()
                    .authorizeRequests()
                    .antMatchers("/").permitAll().anyRequest().authenticated()
                .and()
                    .exceptionHandling()
                .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                //.antMatchers("/**").hasRole("USER")
                //.antMatchers("/admin").hasRole("ADMIN").and()
                .addFilterBefore(myTokenFilter, UsernamePasswordAuthenticationFilter.class);
        /*
                    and().
                authorizeRequests().anyRequest().authenticated();
        /*
                    .and()
                .oauth2Login()
                  .userInfoEndpoint()
                  .oidcUserService(oidcUserService);

         */

        //http.logout().logoutSuccessUrl("http://your-auth-server/exit");

        http.exceptionHandling().authenticationEntryPoint(authEntryPoint);

        http.csrf().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(customAuth);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource()
    {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
