package br.com.fiap3ess.autoescola3ess.adapter.in.controller;

import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.usuario.DadosAtualizacaoUsuario;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.usuario.DadosCadastroUsuario;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.response.usuario.DadosDetalhamentoUsuario;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.response.usuario.DadosListagemUsuario;
import br.com.fiap3ess.autoescola3ess.application.port.in.StdFeaturesPort;
import br.com.fiap3ess.autoescola3ess.application.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/usuarios")
@SecurityRequirement(name = "bearer-key")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Usuários", description = "CRUD de usuários da API (somente ADMIN)")
@RequiredArgsConstructor
public class UsuarioController implements StdFeaturesPort<
        DadosCadastroUsuario,
        DadosListagemUsuario,
        DadosAtualizacaoUsuario,
        Void,
        DadosDetalhamentoUsuario,
        Long> {
    private final UsuarioService service;

    @Override
    @PostMapping
    @Operation(summary = "Cadastrar usuário")
    @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou login já cadastrado", content = @Content)
    public ResponseEntity<DadosDetalhamentoUsuario> cadastrar(
            @RequestBody @Valid DadosCadastroUsuario dados,
            UriComponentsBuilder uriBuilder) {
        DadosDetalhamentoUsuario dto = service.cadastrarUsuario(dados);
        URI uri = uriBuilder.path("/usuarios/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @Override
    @GetMapping
    @Operation(summary = "Listar usuários ativos", description = "Listagem paginada, ordenada por login.")
    public ResponseEntity<Page<DadosListagemUsuario>> listar(
            @ParameterObject @PageableDefault(size = 10, sort = "login") Pageable paginacao) {
        return ResponseEntity.ok(service.listarUsuarios(paginacao));
    }

    @Override
    @GetMapping("/{id}")
    @Operation(summary = "Detalhar usuário")
    @ApiResponse(responseCode = "200", description = "Usuário encontrado")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    public ResponseEntity<DadosDetalhamentoUsuario> detalhar(@PathVariable Long id) {
        return ResponseEntity.ok(service.detalharUsuario(id));
    }

    @Override
    @PutMapping
    @Operation(summary = "Atualizar usuário", description = "Permite trocar a senha e/ou o perfil.")
    @ApiResponse(responseCode = "200", description = "Usuário atualizado")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    public ResponseEntity<DadosDetalhamentoUsuario> atualizar(@RequestBody @Valid DadosAtualizacaoUsuario dados) {
        return ResponseEntity.ok(service.atualizarUsuario(dados));
    }

    @Override
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir usuário", description = "Exclusão lógica: o usuário fica inativo e não consegue mais fazer login.")
    @ApiResponse(responseCode = "204", description = "Usuário excluído")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluirUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
