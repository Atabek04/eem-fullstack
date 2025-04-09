package kz.muhammadzahid.eem.security;

import kz.muhammadzahid.eem.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class UserPrincipal implements UserDetails {

    private final User user;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(User user) {
        this.user = user;
        this.authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // You can implement custom logic if needed
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // You can implement custom logic if needed
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // You can implement custom logic if needed
    }

    @Override
    public boolean isEnabled() {
        return user.isActive();
    }

}
