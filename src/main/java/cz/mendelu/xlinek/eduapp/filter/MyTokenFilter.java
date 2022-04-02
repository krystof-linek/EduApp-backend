package cz.mendelu.xlinek.eduapp.filter;

import com.google.api.client.json.webtoken.JsonWebToken;
import com.google.auth.oauth2.TokenVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MyTokenFilter extends OncePerRequestFilter {

    @Value("${google.clientId}")
    private String client_id;
    @Value("${google.iss}")
    private String iss;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = getToken(request);

        if (!token.equals("invalid"))
            token = verifyJwt(token);

        if (SecurityContextHolder.getContext().getAuthentication() == null) {

            UsernamePasswordAuthenticationToken userPassAuthToken = new UsernamePasswordAuthenticationToken(token, null); //userDetails.getAuthorities());

            userPassAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(userPassAuthToken);
        }

        filterChain.doFilter(request, response);
    }

    public String getToken(HttpServletRequest request) {
        if(request.getHeader("Authorization") != null) {
            String token = request.getHeader("Authorization");
            String[] parts = token.split(" ");
            if (parts.length != 2 || !parts[0].contains("Bearer")) {
                token = "invalid";
            } else
                token = parts[1];
            return token;
        } else
            return "invalid";
    }

    private String verifyJwt(String token) {
        TokenVerifier tokenVerifier = TokenVerifier.newBuilder()
                .setAudience(client_id)
                .setIssuer(iss)
                .build();
        try {
            JsonWebToken jsonWebToken = tokenVerifier.verify(token);

            JsonWebToken.Payload payload = jsonWebToken.getPayload();

            if (payload.getSubject() != null && payload.get("email") != null)
                return token;
            else
                return "invalid";
        } catch (TokenVerifier.VerificationException e) {
            return "invalid";
        }
    }
}
