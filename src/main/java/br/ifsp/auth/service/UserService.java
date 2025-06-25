package br.ifsp.auth.service;

import java.util.HashSet;
import java.util.Set;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import br.ifsp.auth.dto.RoleRequestDTO;
import br.ifsp.auth.dto.UserRequestDTO;
import br.ifsp.auth.dto.UserRequestWithRolesDTO;
import br.ifsp.auth.dto.UserResponseDTO;
import br.ifsp.auth.exception.ResourceNotFoundException;
import br.ifsp.auth.model.Role;
import br.ifsp.auth.model.User;
import br.ifsp.auth.model.enums.RoleName;
import br.ifsp.auth.repository.RoleRepository;
import br.ifsp.auth.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, ModelMapper modelMapper, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        Set<RoleRequestDTO> roles = new HashSet<>();
        roles.add(new RoleRequestDTO("ROLE_USER"));
        
        UserRequestWithRolesDTO userRequestWithRolesDTO = new UserRequestWithRolesDTO();
        userRequestWithRolesDTO.setName(userRequestDTO.getName());
        userRequestWithRolesDTO.setUsername(userRequestDTO.getUsername());
        userRequestWithRolesDTO.setEmail(userRequestDTO.getEmail());
        userRequestWithRolesDTO.setPassword(userRequestDTO.getPassword());
        userRequestWithRolesDTO.setRoles(roles);

        return createUser(userRequestWithRolesDTO);
    }

    @Transactional
    public UserResponseDTO createUser(UserRequestWithRolesDTO userRequestDTO) {
        if (userRepository.existsByUsername(userRequestDTO.getUsername())) {
            throw new IllegalArgumentException("Username '" + userRequestDTO.getUsername() + "' already exists.");
        }
        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new IllegalArgumentException("Email '" + userRequestDTO.getEmail() + "' already exists.");
        }

        User user = modelMapper.map(userRequestDTO, User.class);
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));

        for (RoleRequestDTO roleDTO : userRequestDTO.getRoles()) {
            Role role = roleRepository.findByRoleName(RoleName.fromString(roleDTO.getRoleName()))
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + roleDTO.getRoleName()));
            user.addRole(role);
        }

        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserResponseDTO.class);
    }
}
