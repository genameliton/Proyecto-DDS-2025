package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.entities.Permiso;
import ar.edu.utn.frba.dds.models.entities.Rol;
import ar.edu.utn.frba.dds.models.entities.TipoRol;
import ar.edu.utn.frba.dds.models.entities.User;
import ar.edu.utn.frba.dds.models.entities.dto.LoginDto;
import ar.edu.utn.frba.dds.models.entities.dto.NewUserDto;
import ar.edu.utn.frba.dds.models.entities.dto.OAuthLogingDto;
import ar.edu.utn.frba.dds.models.entities.dto.UserRolesAndAuthoritiesDto;
import ar.edu.utn.frba.dds.models.entities.dto.UserTokensDto;
import ar.edu.utn.frba.dds.models.repositories.UserRepository;
import jakarta.persistence.EntityExistsException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthenticacionService {
  // logica para creacion de administrador
  @Value("${privateAdminName}")
  private String secretAdminName;
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthenticacionService(PasswordEncoder passwordEncoder, UserRepository userRepository, JwtService jwtService,
      AuthenticationManager authenticationManager) {
    this.passwordEncoder = passwordEncoder;
    this.userRepository = userRepository;
    this.jwtService = jwtService;
    this.authenticationManager = authenticationManager;
  }

  public UserTokensDto register(NewUserDto request) {
    Optional<User> userInDb = userRepository.findByUsername(request.getUsername());
    if (userInDb.isPresent()) {
      throw new EntityExistsException("User with same username already exists");
    }
    User user = User.builder()
        .username(request.getUsername())
        .passwordHash(passwordEncoder.encode(request.getPassword()))
        .build();
    // save user
    // controlo si el usuario tiene name master of puppets
    Rol rol = new Rol();
    if (Objects.equals(request.getUsername(), secretAdminName)) {
      // crear admin
      rol.setTiporol(TipoRol.ADMINISTRADOR);
      rol.setPermisos(List.of(Permiso.GESTION_COLECCIONES, Permiso.EDITAR_HECHO, Permiso.GESTIONAR_HECHOS,
          Permiso.GESTIONAR_SOLICITUDES));
    } else {
      rol.setTiporol(TipoRol.CONTRIBUYENTE);
      rol.setPermisos(List.of(Permiso.EDITAR_HECHO));
    }
    user.setRol(rol);
    User registeredUser = userRepository.save(user);
    log.info("{} se ha registrado con rol {}", registeredUser.getUsername(),
        registeredUser.getRol().getTiporol().toString());
    var token = jwtService.generateAccessToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    UserTokensDto resp = UserTokensDto.builder().accessToken(token).refreshToken(refreshToken).build();
    return resp;
  }

  public UserTokensDto login(LoginDto request) {

    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getUsername(), request.getPassword()));
    User user = userRepository.findByUsername(request.getUsername()).orElseThrow(
        () -> new UsernameNotFoundException("Not found"));
    String accessToken = jwtService.generateAccessToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);
    return UserTokensDto.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  public UserTokensDto loginOAuth(OAuthLogingDto request) {
    Optional<User> userOptional = userRepository.findByUsername(request.getUsername());
    User user;

    if (userOptional.isEmpty()) {
      Rol rol = new Rol();
      rol.setTiporol(TipoRol.CONTRIBUYENTE);
      rol.setPermisos(List.of(Permiso.EDITAR_HECHO));

      user = User.builder()
          .username(request.getUsername())
          .passwordHash("")
          .rol(rol)
          .build();

      userRepository.save(user);
      log.info("Usuario OAuth creado: {}", request.getUsername());
    } else {
      user = userOptional.get();
    }

    String accessToken = jwtService.generateAccessToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);

    return UserTokensDto.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  public UserTokensDto refresh(String tokenHeader) {
    User user = getUserByToken(tokenHeader);

    if (!jwtService.isTokenValid(tokenHeader.substring(7), user)) {
      throw new IllegalArgumentException("Token not valid, it's expired or incorrect");
    }

    return UserTokensDto.builder()
        .accessToken(jwtService.generateAccessToken(user))
        .refreshToken(tokenHeader.substring(7))
        .build();
  }

  public UserRolesAndAuthoritiesDto getRolesAndAuthorities(String reqToken) {
    User user = getUserByToken(reqToken);

    if (!jwtService.isTokenValid(reqToken.substring(7), user)) {
      throw new IllegalArgumentException("Token not valid, it's expired or incorrect");
    }

    return UserRolesAndAuthoritiesDto.builder()
        .username(user.getUsername())
        .rol(user.getRol().getTiporol())
        .permisos(user.getRol().getPermisos())
        .build();
  }

  public User getUserByToken(String tokenHeader) {
    if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
      throw new IllegalArgumentException("Token not valid");
    }

    String username = jwtService.extractUsername(tokenHeader.substring(7));
    if (username == null) {
      throw new UsernameNotFoundException("User Not found by token");
    }

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException(username + " not found"));
    return user;
  }
}
