package br.com.fiap3ess.autoescola3ess.config.security.filter;

import br.com.fiap3ess.autoescola3ess.adapter.out.security.UsuarioAutenticado;
import br.com.fiap3ess.autoescola3ess.config.security.service.TokenService;
import br.com.fiap3ess.autoescola3ess.application.port.out.UsuarioRepository;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository repository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String tokenJWT = recuperarToken(request);

        if (tokenJWT != null) {
            try {
                String subject = tokenService.getSubject(tokenJWT);
                repository.findByLogin(subject)
                        .filter(usuario -> usuario.isAtivo())
                        .map(UsuarioAutenticado::new)
                        .ifPresent(usuario -> {
                            var authenticated = new UsernamePasswordAuthenticationToken(
                                    usuario,
                                    null,
                                    usuario.getAuthorities()
                            );
                            SecurityContextHolder.getContext().setAuthentication(authenticated);
                        });
            } catch (JWTVerificationException ex) {
                //Token inválido ou expirado, segue sem autenticar
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }
}