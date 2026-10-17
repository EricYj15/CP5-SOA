package br.com.fiap3ess.autoescola3ess.application.service;

import br.com.fiap3ess.autoescola3ess.adapter.in.controller.mapper.UsuarioMapper;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.usuario.DadosAtualizacaoUsuario;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.usuario.DadosCadastroUsuario;
import br.com.fiap3ess.autoescola3ess.application.core.domain.Usuario;
import br.com.fiap3ess.autoescola3ess.application.port.out.UsuarioRepository;
import br.com.fiap3ess.autoescola3ess.exception.type.ValidacaoException;
import br.com.fiap3ess.autoescola3ess.shared.vo.enumeration.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
    @Mock
    UsuarioRepository repository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    UsuarioService service;

    @BeforeEach
    void setUp() {
        service = new UsuarioService(repository, new UsuarioMapper(), passwordEncoder);
    }

    @Test
    @DisplayName("Expectativa: Salvar a senha criptografada com BCrypt, nunca em texto puro.")
    void cadastrarUsuarioCenario1() {
        //arrange
        when(repository.existsByLogin("secretaria")).thenReturn(false);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        //act
        service.cadastrarUsuario(new DadosCadastroUsuario("secretaria", "senha123", Role.USER));

        //assert
        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getSenha()).isNotEqualTo("senha123");
        assertThat(passwordEncoder.matches("senha123", captor.getValue().getSenha())).isTrue();
        assertThat(captor.getValue().getPerfil()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("Expectativa: Impedir cadastro com login já existente.")
    void cadastrarUsuarioCenario2() {
        when(repository.existsByLogin("admin")).thenReturn(true);

        assertThatThrownBy(() -> service.cadastrarUsuario(new DadosCadastroUsuario("admin", "senha123", Role.ADMIN)))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Login já cadastrado!");
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Expectativa: Manter a senha atual quando apenas o perfil for alterado.")
    void atualizarUsuarioCenario1() {
        //arrange
        Usuario usuario = new Usuario(2L, "secretaria", "hash-atual", Role.USER, true);
        when(repository.findById(2L)).thenReturn(Optional.of(usuario));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        //act
        var resultado = service.atualizarUsuario(new DadosAtualizacaoUsuario(2L, null, Role.ADMIN));

        //assert
        assertThat(resultado.perfil()).isEqualTo(Role.ADMIN);
        assertThat(usuario.getSenha()).isEqualTo("hash-atual");
    }

    @Test
    @DisplayName("Expectativa: Exclusão lógica deixa o usuário inativo.")
    void excluirUsuarioCenario1() {
        Usuario usuario = new Usuario(2L, "secretaria", "hash", Role.USER, true);
        when(repository.findById(2L)).thenReturn(Optional.of(usuario));

        service.excluirUsuario(2L);

        assertThat(usuario.isAtivo()).isFalse();
        verify(repository).save(usuario);
    }
}
