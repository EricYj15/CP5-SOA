package br.com.fiap3ess.autoescola3ess.application.service;

import br.com.fiap3ess.autoescola3ess.adapter.in.controller.mapper.UsuarioMapper;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.usuario.DadosAtualizacaoUsuario;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.usuario.DadosCadastroUsuario;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.response.usuario.DadosDetalhamentoUsuario;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.response.usuario.DadosListagemUsuario;
import br.com.fiap3ess.autoescola3ess.application.core.domain.Usuario;
import br.com.fiap3ess.autoescola3ess.application.port.out.UsuarioRepository;
import br.com.fiap3ess.autoescola3ess.exception.type.UsuarioNotFoundException;
import br.com.fiap3ess.autoescola3ess.exception.type.ValidacaoException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository repository;
    private final UsuarioMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public DadosDetalhamentoUsuario cadastrarUsuario(DadosCadastroUsuario dados) {
        if (repository.existsByLogin(dados.login())) {
            throw new ValidacaoException("Login já cadastrado!");
        }
        Usuario usuario = mapper.toDomain(dados, passwordEncoder.encode(dados.senha()));
        Usuario salvo = repository.save(usuario);
        return mapper.toDetails(salvo);
    }

    @Transactional(readOnly = true)
    public Page<DadosListagemUsuario> listarUsuarios(Pageable paginacao) {
        return repository
                .findAllByAtivoTrue(paginacao)
                .map(mapper::toList);
    }

    @Transactional(readOnly = true)
    public DadosDetalhamentoUsuario detalharUsuario(Long id) {
        Usuario usuario = repository
                .findById(id)
                .orElseThrow(() ->
                        new UsuarioNotFoundException("ID do usuário informado não existe!"));
        return mapper.toDetails(usuario);
    }

    @Transactional
    public DadosDetalhamentoUsuario atualizarUsuario(DadosAtualizacaoUsuario dados) {
        Usuario usuario = repository
                .findById(dados.id())
                .orElseThrow(() ->
                        new UsuarioNotFoundException("ID do usuário informado não existe!"));
        String senhaCriptografada = dados.senha() != null ? passwordEncoder.encode(dados.senha()) : null;
        usuario.atualizarInformacoes(senhaCriptografada, dados.perfil());
        Usuario salvo = repository.save(usuario);
        return mapper.toDetails(salvo);
    }

    @Transactional
    public void excluirUsuario(Long id) {
        Usuario usuario = repository
                .findById(id)
                .orElseThrow(() ->
                        new UsuarioNotFoundException("ID do usuário informado não existe!"));
        usuario.excluir();
        repository.save(usuario);
    }
}
