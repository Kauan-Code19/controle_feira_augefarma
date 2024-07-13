package com.augefarma.controle_feira.entities.administrator;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "Administrators")
public class AdministratorEntity implements UserDetails {

    // Primary key for the AdministratorEntity, auto-generated and not updatable
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    // Full name of the administrator, cannot be null
    @Column(name = "full_name", nullable = false)
    private String fullName;

    // Email of the administrator, must be unique and cannot be updated or null
    @Column(name = "email", unique = true, updatable = false, nullable = false)
    private String email;

    // Password of the administrator, cannot be null
    @Column(name = "password", nullable = false)
    private String password;

    // Returns the authorities granted to the administrator
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    // Returns the email as the username
    @Override
    public String getUsername() {
        return this.email;
    }

    // Indicates whether the administrator's account has expired
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    // Indicates whether the administrator is locked or unlocked
    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    // Indicates whether the administrator's credentials have expired
    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    // Indicates whether the administrator is enabled or disabled
    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
