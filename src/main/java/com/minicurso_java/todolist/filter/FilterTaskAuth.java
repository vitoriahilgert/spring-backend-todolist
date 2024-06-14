package com.minicurso_java.todolist.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.minicurso_java.todolist.user.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Validando rota
        var servletPath = request.getServletPath();

        if (servletPath.startsWith("/tasks/")) {
            // Pegar a autenticação
            var authorization = request.getHeader("Authorization");
            var authEncoded = authorization.substring("Basic".length()).trim();
            byte[] authDecoded = Base64.getDecoder().decode(authEncoded);
            var authString = new String(authDecoded);

            String[] credentials = authString.split(":");
            String username = credentials[0];
            String password = credentials[1];

            // Validar usuário
            var user = this.userRepository.findByUsername(username);
            if (user == null) {
                response.sendError(401); // Unauthorized user
            } else {
                // Validar senha
                var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                if (passwordVerify.verified) {
                    // Setando o id do usuário para recuperar no controller
                    request.setAttribute("idUser", user.getId());

                    // Passa o request (tudo que vem do servidor) e a response (tudo que é enviado) para a próxima camada
                    // nesse caso, o controller
                    filterChain.doFilter(request, response);
                } else {
                    response.sendError(401);
                }
            }
        }
        else {
            filterChain.doFilter(request, response);
        }
    }
}
