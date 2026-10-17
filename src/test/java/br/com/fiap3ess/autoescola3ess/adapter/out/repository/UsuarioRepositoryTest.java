package br.com.fiap3ess.autoescola3ess.adapter.out.repository;

import br.com.fiap3ess.autoescola3ess.adapter.out.repository.mapper.UsuarioEntityMapper;
import br.com.fiap3ess.autoescola3ess.application.core.domain.Usuario;
import br.com.fiap3ess.autoescola3ess.application.port.out.UsuarioRepository;
import br.com.fiap3ess.autoescola3ess.shared.vo.enumeration.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import({UsuarioRepositoryImpl.class, UsuarioEntityMapper.class})
class UsuarioRepositoryTest {
    @Autowired
    UsuarioRepository repository;

    @Test
    @DisplayName("Expectativa: Encontrar o usuário admin criado pela migration, com perfil ADMIN e senha em BCrypt.")
    void findByLoginCenario1() {
        Optional<Usuario> admin = repository.findByLogin("admin");

        assertThat(admin).isPresent();
        assertThat(admin.get().getPerfil()).isEqualTo(Role.ADMIN);
        assertThat(admin.get().getSenha()).startsWith("$2a$");
        assertThat(admin.get().isAtivo()).isTrue();
    }

    @Test
    @DisplayName("Expectativa: Retornar vazio para login inexistente.")
    void findByLoginCenario2() {
        assertThat(repository.findByLogin("naoexiste")).isEmpty();
        assertThat(repository.existsByLogin("naoexiste")).isFalse();
    }

    @Test
    @DisplayName("Expectativa: Persistir novo usuário e não listá-lo após a exclusão lógica.")
    void saveEFindAllByAtivoTrueCenario1() {
        //arrange
        Usuario salvo = repository.save(new Usuario(null, "secretaria", "hash", Role.USER, true));
        assertThat(repository.existsByLogin("secretaria")).isTrue();

        //act
        salvo.excluir();
        repository.save(salvo);

        //assert
        assertThat(repository.findAllByAtivoTrue(PageRequest.of(0, 10)).getContent())
                .extracting(Usuario::getLogin)
                .containsExactly("admin");
    }
}
