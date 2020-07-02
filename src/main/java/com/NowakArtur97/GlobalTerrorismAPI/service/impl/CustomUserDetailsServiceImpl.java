package com.NowakArtur97.GlobalTerrorismAPI.service.impl;

import com.NowakArtur97.GlobalTerrorismAPI.node.RoleNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.UserNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.UserRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserNode userNode = userRepository.findByUserName(username);

        return new User(userNode.getUserName(), Arrays.toString(userNode.getPassword()), true, true,
                true, true, getAuthorities(userNode.getRoles()));
    }

    private static List<GrantedAuthority> getAuthorities(Set<RoleNode> userRoles) {

        List<GrantedAuthority> userAuthorities = new ArrayList<>();

        for (RoleNode role : userRoles) {
            userAuthorities.add(new SimpleGrantedAuthority(role.getName()));
        }

        return userAuthorities;
    }
}
