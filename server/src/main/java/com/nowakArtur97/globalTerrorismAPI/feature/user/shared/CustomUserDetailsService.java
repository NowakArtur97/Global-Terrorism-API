package com.nowakArtur97.globalTerrorismAPI.feature.user.shared;

import com.nowakArtur97.globalTerrorismAPI.feature.user.shared.RoleNode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Set;

public interface CustomUserDetailsService extends UserDetailsService {

    List<GrantedAuthority> getAuthorities(Set<RoleNode> userRoles);
}
