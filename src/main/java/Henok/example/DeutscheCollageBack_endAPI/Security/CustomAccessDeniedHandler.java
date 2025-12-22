package Henok.example.DeutscheCollageBack_endAPI.Security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Extract current user's role
        String userRole = "UNKNOWN";
        String username = "unknown";

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities() != null && !auth.getAuthorities().isEmpty()) {
            userRole = auth.getAuthorities().iterator().next().getAuthority()
                    .replace("ROLE_", ""); // Clean prefix → e.g., "STUDENT", "REGISTRAR"

            if (auth.getPrincipal() instanceof UserDetails) {
                username = ((UserDetails) auth.getPrincipal()).getUsername();
            } else if (auth.getPrincipal() instanceof String) {
                username = (String) auth.getPrincipal();
            }
        }

        String path = request.getRequestURI();

        // Friendly and explanatory message
        String message = String.format(
                "You are logged in as a %s, but this action requires higher privileges. " +
                        "The endpoint %s is restricted to authorized roles only (e.g., REGISTRAR, ADMIN, DEAN). " +
                        "Please contact the administrator if you believe this is an error.",
                userRole, path
        );

        // Structured error response (consistent with your project's style)
        Map<String, Object> error = Map.of(
                "timestamp", Instant.now().toString(),
                "status", 403,
                "error", "Forbidden - Insufficient Role Permissions",
                "message", message,
                "path", path,
                "username", username,
                "yourRole", userRole
        );

        response.getWriter().write(mapper.writeValueAsString(error));
        response.getWriter().flush();
    }
}