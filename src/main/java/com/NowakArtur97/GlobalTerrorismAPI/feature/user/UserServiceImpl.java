package com.NowakArtur97.GlobalTerrorismAPI.feature.user;

import com.NowakArtur97.GlobalTerrorismAPI.mapper.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {

    private final static RoleNode DEFAULT_USER_ROLE = new RoleNode("user");

    private final UserRepository userRepository;

    private final ObjectMapper objectMapper;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserNode register(UserDTO userDTO) {

        UserNode userNode = objectMapper.map(userDTO, UserNode.class);

        userNode.setPassword(bCryptPasswordEncoder.encode(userNode.getPassword()));

        userNode.setRoles(Set.of(DEFAULT_USER_ROLE));

        return userRepository.save(userNode);
    }

    @Override
    public Optional<UserNode> findByUserName(String userName) {

        return userRepository.findByUserName(userName);
    }

    @Override
    public Optional<UserNode> findByEmail(String email) {

        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<UserNode> findByUserNameOrEmail(String userName, String email) {

        return userRepository.findByUserNameOrEmail(userName, email);
    }
}
