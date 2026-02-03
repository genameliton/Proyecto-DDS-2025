package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.models.entities.dto.LoginDto;
import ar.edu.utn.frba.dds.models.entities.dto.NewUserDto;
import ar.edu.utn.frba.dds.models.entities.dto.OAuthLogingDto;
import ar.edu.utn.frba.dds.models.entities.dto.UserRolesAndAuthoritiesDto;
import ar.edu.utn.frba.dds.models.entities.dto.UserTokensDto;
import ar.edu.utn.frba.dds.services.AuthenticacionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthenticacionService authenticationService;

  public AuthController(AuthenticacionService authenticationService) {
    this.authenticationService = authenticationService;
  }

  @PostMapping("/refresh")
  public ResponseEntity<UserTokensDto> refreshTokens(@RequestHeader(HttpHeaders.AUTHORIZATION) String tokenHeader) {
    return ResponseEntity.ok(authenticationService.refresh(tokenHeader));
  }

  @PostMapping("/register")
  public ResponseEntity<UserTokensDto> registerUser(@RequestBody NewUserDto request) {
    if (request.getPassword().length() < 8) {
      throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
    }
    return ResponseEntity.ok(authenticationService.register(request));
  }

  @PostMapping("/login")
  public ResponseEntity<UserTokensDto> logUser(@RequestBody LoginDto request) {
    UserTokensDto resp = authenticationService.login(request);
    return ResponseEntity.ok(resp);
  }

  @PostMapping("/oauth-login")
  public ResponseEntity<UserTokensDto> loginOAuth(@RequestBody OAuthLogingDto request) {
    return ResponseEntity.ok(authenticationService.loginOAuth(request));
  }

  @GetMapping("/user/roles-permisos")
  public ResponseEntity<UserRolesAndAuthoritiesDto> getRolesPermisos(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String reqToken) {
    return ResponseEntity.ok(authenticationService.getRolesAndAuthorities(reqToken));
  }
}
