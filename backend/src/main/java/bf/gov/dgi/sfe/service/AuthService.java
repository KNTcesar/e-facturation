package bf.gov.dgi.sfe.service;

import bf.gov.dgi.sfe.dto.AuthRequest;
import bf.gov.dgi.sfe.dto.AuthResponse;
import bf.gov.dgi.sfe.security.JwtService;
import bf.gov.dgi.sfe.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    // Authentifie un utilisateur et construit un JWT avec ses roles.
    public AuthResponse authenticate(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Set<String> roles = principal.getAuthorities().stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .collect(Collectors.toSet());

        String token = jwtService.generateToken(principal, Map.of("roles", roles));
        return new AuthResponse(token, principal.getUsername(), roles);
    }
}
