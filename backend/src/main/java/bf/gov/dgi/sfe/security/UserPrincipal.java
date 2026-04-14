package bf.gov.dgi.sfe.security;

import bf.gov.dgi.sfe.entity.Utilisateur;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class UserPrincipal implements UserDetails {

    private final Utilisateur utilisateur;

    public UserPrincipal(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return utilisateur.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getCode()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return utilisateur.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return utilisateur.getUsername();
    }

    @Override
    public boolean isEnabled() {
        return utilisateur.isActif();
    }
}
