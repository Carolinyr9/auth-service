package br.ifsp.auth.service;

import br.ifsp.auth.dto.UserPatchDTO;
import br.ifsp.auth.dto.UserRequestDTO;
import br.ifsp.auth.dto.UserRequestWithRolesDTO; 
import br.ifsp.auth.dto.UserResponseDTO;
import br.ifsp.auth.dto.RoleRequestDTO; 
import br.ifsp.auth.dto.page.PagedResponse;
import br.ifsp.auth.exception.ResourceNotFoundException;
import br.ifsp.auth.model.Role;
import br.ifsp.auth.model.User;
import br.ifsp.auth.model.enums.RoleName;
import br.ifsp.auth.repository.RoleRepository;
import br.ifsp.auth.repository.UserRepository;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public UserService(UserRepository userRepository, RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        User user = modelMapper.map(userRequestDTO, User.class);
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));

        Role defaultRole = roleRepository.findByRoleName(RoleName.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Default role ROLE_USER not found."));
        user.setRoles(new HashSet<>());
        user.addRole(defaultRole);

        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserResponseDTO.class);
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

    @Transactional(readOnly = true)
    public PagedResponse<UserResponseDTO> getAllUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        List<UserResponseDTO> content = userPage.getContent().stream()
                                            .map(user -> modelMapper.map(user, UserResponseDTO.class))
                                            .collect(Collectors.toList());
        return new PagedResponse<>(content, userPage.getNumber(), userPage.getSize(),
                                   userPage.getTotalElements(), userPage.getTotalPages(), userPage.isLast());
    }


    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return modelMapper.map(user, UserResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return modelMapper.map(user, UserResponseDTO.class);
    }

    @Transactional
    public UserResponseDTO registerUser(UserRequestDTO userRequestDTO) {
        if (userRepository.existsByUsername(userRequestDTO.getUsername())) {
            throw new IllegalArgumentException("Username '" + userRequestDTO.getUsername() + "' already exists.");
        }
        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new IllegalArgumentException("Email '" + userRequestDTO.getEmail() + "' already exists.");
        }

        User user = modelMapper.map(userRequestDTO, User.class);
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));

        Role defaultRole = roleRepository.findByRoleName(RoleName.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Default role ROLE_USER not found."));
        user.addRole(defaultRole);

        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserResponseDTO.class);
    }

    @Transactional
    public UserResponseDTO createUserWithRoles(UserRequestWithRolesDTO userRequestWithRolesDTO) {
        if (userRepository.existsByUsername(userRequestWithRolesDTO.getUsername())) {
            throw new IllegalArgumentException("Username '" + userRequestWithRolesDTO.getUsername() + "' already exists.");
        }
        if (userRepository.existsByEmail(userRequestWithRolesDTO.getEmail())) {
            throw new IllegalArgumentException("Email '" + userRequestWithRolesDTO.getEmail() + "' already exists.");
        }

        User user = modelMapper.map(userRequestWithRolesDTO, User.class);
        user.setPassword(passwordEncoder.encode(userRequestWithRolesDTO.getPassword()));

        Set<Role> roles = userRequestWithRolesDTO.getRoles().stream()
                .map(roleDTO -> roleRepository.findByRoleName(RoleName.fromString(roleDTO.getRoleName()))
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + roleDTO.getRoleName())))
                .collect(Collectors.toSet());
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserResponseDTO.class);
    }


    @Transactional
    public UserResponseDTO updateUser(Long id, UserRequestWithRolesDTO userRequestDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        userRepository.findByUsername(userRequestDTO.getUsername()).ifPresent(existingUser -> {
            if (!existingUser.getId().equals(id)) {
                throw new IllegalArgumentException("Username '" + userRequestDTO.getUsername() + "' already exists.");
            }
        });

        userRepository.findByEmailIgnoreCase(userRequestDTO.getEmail()).ifPresent(existingUser -> {
            if (!existingUser.getId().equals(id)) {
                throw new IllegalArgumentException("Email '" + userRequestDTO.getEmail() + "' already exists.");
            }
        });

        user.setName(userRequestDTO.getName());
        user.setEmail(userRequestDTO.getEmail());
        user.setUsername(userRequestDTO.getUsername());

        if (userRequestDTO.getPassword() != null && !userRequestDTO.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        }

        Set<Role> newRoles = userRequestDTO.getRoles().stream()
                .map(roleDTO -> roleRepository.findByRoleName(RoleName.fromString(roleDTO.getRoleName()))
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + roleDTO.getRoleName())))
                .collect(Collectors.toSet());
        user.setRoles(newRoles); 

        User updatedUser = userRepository.save(user);
        return modelMapper.map(updatedUser, UserResponseDTO.class);
    }


    @Transactional
    public UserResponseDTO patchUser(Long id, UserPatchDTO userPatchDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (userPatchDTO.getName() != null) {
            String name = userPatchDTO.getName();
            if (name.isBlank()) throw new IllegalArgumentException("Name cannot be blank if provided.");
            user.setName(name);
        }

        if (userPatchDTO.getEmail() != null) {
            String email = userPatchDTO.getEmail();
            if (email.isBlank()) throw new IllegalArgumentException("Email cannot be blank if provided.");

            userRepository.findByEmailIgnoreCase(email).ifPresent(existingUserByEmail -> {
                if (!existingUserByEmail.getId().equals(id)) {
                    throw new IllegalArgumentException("Email '" + email + "' already exists.");
                }
            });
            user.setEmail(email);
        }

        if (userPatchDTO.getUsername() != null) {
            String username = userPatchDTO.getUsername();
            if (username.isBlank()) throw new IllegalArgumentException("Username cannot be blank if provided.");

            userRepository.findByUsername(username).ifPresent(existingUserByUsername -> {
                if (!existingUserByUsername.getId().equals(id)) {
                    throw new IllegalArgumentException("Username '" + username + "' already exists.");
                }
            });
            user.setUsername(username);
        }

        if (userPatchDTO.getPassword() != null) {
            String password = userPatchDTO.getPassword();
            if (password.isBlank()) throw new IllegalArgumentException("Password cannot be blank if provided.");
            user.setPassword(passwordEncoder.encode(password));
        }

        if (userPatchDTO.getRoleIds() != null) {
            Set<Role> newRoles = userPatchDTO.getRoleIds().stream()
                    .map(roleId -> roleRepository.findById(roleId)
                            .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId)))
                    .collect(Collectors.toSet());
            user.setRoles(newRoles); 
        }

        User patchedUser = userRepository.save(user);
        return modelMapper.map(patchedUser, UserResponseDTO.class);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}