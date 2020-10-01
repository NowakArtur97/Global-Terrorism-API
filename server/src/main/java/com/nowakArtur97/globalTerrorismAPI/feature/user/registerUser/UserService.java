package com.nowakArtur97.globalTerrorismAPI.feature.user.registerUser;

import com.nowakArtur97.globalTerrorismAPI.feature.user.shared.RoleNode;
import com.nowakArtur97.globalTerrorismAPI.feature.user.shared.UserNode;
import com.nowakArtur97.globalTerrorismAPI.feature.user.shared.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final static RoleNode DEFAULT_USER_ROLE = new RoleNode("user");

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserNode register(UserDTO userDTO) {

        UserNode userNode = modelMapper.map(userDTO, UserNode.class);

        userNode.setPassword(bCryptPasswordEncoder.encode(userNode.getPassword()));

        userNode.setRoles(Set.of(DEFAULT_USER_ROLE));

        return userRepository.save(userNode);
    }

    public Optional<UserNode> findByUserName(String userName) {

        return userRepository.findByUserName(userName);
    }

    public Optional<UserNode> findByEmail(String email) {

        return userRepository.findByEmail(email);
    }

    public Optional<UserNode> findByUserNameOrEmail(String userName, String email) {

        return userRepository.findByUserNameOrEmail(userName, email);
    }

    public UserDataStatusCheckResponse checkUserData(UserDataStatusCheckRequest userData) {

        boolean isUserNameAvailable = findByUserName(userData.getUserName()).isEmpty();
        boolean isEmailAvailable = findByEmail(userData.getEmail()).isEmpty();

        return new UserDataStatusCheckResponse(isUserNameAvailable, isEmailAvailable);
    }
}
