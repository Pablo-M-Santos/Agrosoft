package com.agrosoft.auth.security;

import com.agrosoft.User.domain.AccessLevel;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Getter
public class AuthUserDetails implements UserDetails {

    private final UUID id;
    private final String email;
    private final String passwordHash;
    private final AccessLevel accessLevel;
    private final Boolean active;

    public AuthUserDetails(UUID id, String email, String passwordHash, AccessLevel accessLevel, Boolean active) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.accessLevel = accessLevel;
        this.active = active;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + accessLevel.name()));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(active);
    }
}
