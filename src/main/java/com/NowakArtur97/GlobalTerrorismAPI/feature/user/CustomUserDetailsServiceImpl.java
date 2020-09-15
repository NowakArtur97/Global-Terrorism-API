package com.NowakArtur97.GlobalTerrorismAPI.feature.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
class CustomUserDetailsServiceImpl implements CustomUserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String value) throws UsernameNotFoundException {

        UserNode userNode = userRepository.findByUserNameOrEmail(value, value)
                .orElseThrow(() -> new UsernameNotFoundException("User with name/email: '" + value + "' not found."));

        return new User(userNode.getUserName(), userNode.getPassword(), getAuthorities(userNode.getRoles()));
    }

    private static List<GrantedAuthority> getAuthorities(Set<RoleNode> userRoles) {

        List<GrantedAuthority> userAuthorities = new ArrayList<>();

        for (RoleNode role : userRoles) {
            userAuthorities.add(new SimpleGrantedAuthority(role.getName()));
        }

        return userAuthorities;
    }
}
