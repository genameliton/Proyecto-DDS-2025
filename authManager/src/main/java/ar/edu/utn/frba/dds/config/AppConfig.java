package ar.edu.utn.frba.dds.config;

import ar.edu.utn.frba.dds.models.entities.User;
import ar.edu.utn.frba.dds.models.repositories.UserRepository;
import java.beans.Encoder;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@AllArgsConstructor
public class AppConfig {
  private final UserRepository userRepository;

  @Bean
  public UserDetailsService userDetailsService() {
    return username -> {
      User user = userRepository.findByUsername(username).orElseThrow(
          () -> new UsernameNotFoundException("Not found")
      );

      return org.springframework.security.core.userdetails.User.builder()
          .username(user.getUsername())
          .password(user.getPasswordHash())
          .authorities(user.getRol().getPermisos().stream()
              .map(Enum::toString) // ejemplo
              .toArray(String[]::new))
          .build();
    };
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setPasswordEncoder(passwordEncoder());
    authProvider.setUserDetailsService(userDetailsService());
    return authProvider;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }
}
