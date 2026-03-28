package pl.czyzlowie.core.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class NoCacheFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        
        if (isProtectedPage(requestURI)) {
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate, private");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
        }
        
        filterChain.doFilter(request, response);
    }

    private boolean isProtectedPage(String requestURI) {
        return requestURI.startsWith("/profil") ||
               requestURI.startsWith("/powiadomienia") ||
               requestURI.startsWith("/dziennik-wypraw") ||
               requestURI.startsWith("/moje-polowy") ||
               requestURI.startsWith("/statystyki") ||
               requestURI.startsWith("/cele") ||
               requestURI.startsWith("/kalendarz-wypraw") ||
               requestURI.startsWith("/ulubione-miejscowki") ||
               requestURI.startsWith("/lista-przynet") ||
               requestURI.startsWith("/moje-zestawy") ||
               requestURI.startsWith("/checklisty") ||
               requestURI.startsWith("/notatki") ||
               requestURI.startsWith("/ustawienia") ||
               requestURI.startsWith("/login") ||
               requestURI.startsWith("/logout") ||
               requestURI.startsWith("/rejestracja");
    }
}


