package br.com.fiap3ess.autoescola3ess.adapter.in.controller;

import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.usuario.DadosLogin;
import br.com.fiap3ess.autoescola3ess.adapter.out.security.UsuarioAutenticado;
import br.com.fiap3ess.autoescola3ess.config.security.service.dto.DadosTokenJWT;
import br.com.fiap3ess.autoescola3ess.config.security.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
@Tag(name = "Autenticação", description = "Login e geração do token JWT")
public class LoginController {
    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService;

    @PostMapping
    @Operation(
            summary = "Efetuar login",
            description = "Retorna um token JWT válido por 30 minutos. Use o botão Authorize do Swagger para enviá-lo nas demais requisições.")
    @ApiResponse(responseCode = "200", description = "Login efetuado com sucesso")
    @ApiResponse(responseCode = "401", description = "Credenciais inválidas", content = @Content)
    public ResponseEntity<DadosTokenJWT> efetuarLogin(@RequestBody @Valid DadosLogin dados) {
        var token = new UsernamePasswordAuthenticationToken(dados.login(), dados.senha());
        Authentication authentication = manager.authenticate(token);
        UsuarioAutenticado usuarioAutenticado = (UsuarioAutenticado) authentication.getPrincipal();
        String tokenJWT = tokenService.generateToken(usuarioAutenticado.getUsuario());
        return ResponseEntity.ok(new DadosTokenJWT(tokenJWT));
    }
}