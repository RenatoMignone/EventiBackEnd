package it.Security.Utente;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration //File di configurazione
@EnableWebSecurity //dove metteremo le nostre configurazioni di sicurezza
@Import(UserDetailsServiceCustom.class)
public class SecurityConfig {
    private final UserDetailsServiceCustom userDetailService;
    @Autowired
    public SecurityConfig(UserDetailsServiceCustom userDetailService) {
        this.userDetailService = userDetailService;
    }

//-------------------------------------------------------------------------------------------------------------------------------------------

    @Bean // questo metodo serve per effettuare un encoding sulla nostra password per andarla a crittare nel database
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

//-------------------------------------------------------------------------------------------------------------------------------------------

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception{
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

//-------------------------------------------------------------------------------------------------------------------------------------------

    @Bean //Vado a istanziare una catena di filtri attraverso la quale le nostre chiamate http verranno autorizzate ad accedere al server
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
            .cors().and().csrf().disable()
            .authorizeHttpRequests()

            .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

            .requestMatchers("/api/v1/utente").permitAll()
            .requestMatchers("/api/v1/utente/**").permitAll()

            .requestMatchers("/api/v1/login").permitAll()
            .requestMatchers("/api/v1/login/**").permitAll()

            .requestMatchers("/api/v1/parcheggio").permitAll()
            .requestMatchers("/api/v1/parcheggio/**").permitAll()

            .requestMatchers("/api/v1/recensione").permitAll()
            .requestMatchers("/api/v1/recensione/**").permitAll()

            .requestMatchers("/api/v1/biglietto").permitAll()
            .requestMatchers("/api/v1/biglietto/**").permitAll()

            .requestMatchers("/api/v1/evento").permitAll()
            .requestMatchers("/api/v1/evento/**").permitAll()


            .requestMatchers("api/v1/register/verifica/{id}").permitAll()
            .requestMatchers("api/v1/login/recovery").permitAll()
            .requestMatchers("api/v1/recovery/recovery/{id}").permitAll()
//            .requestMatchers("api/v1/evento/biglietto/crea_biglietti").permitAll()
            .requestMatchers("api/v1/immagine/**").permitAll()
            .requestMatchers("api/v1/immagine/{id}").permitAll()
            .requestMatchers("api/v1/test/").authenticated()
            .requestMatchers("api/v1/test/{id}").authenticated()
            .requestMatchers("api/v1/{username}").permitAll()
            .requestMatchers("api/v1/categoria").permitAll()
            .requestMatchers("api/v1/programma/**").permitAll()

            .requestMatchers("/swagger-ui/**", "/api-docs/**", "/v3/**").permitAll()
            .and().httpBasic();

        return http.build();
    }
}
//-------------------------------------------------------------------------------------------------------------------------------------------
