package com.ironhack.bank_system.services;

import com.ironhack.bank_system.configurations.mapper.CustomMapper;
import com.ironhack.bank_system.dto.UserDTO;
import com.ironhack.bank_system.enums.Rol;
import com.ironhack.bank_system.model.User;
import com.ironhack.bank_system.repository.UserRepository;
import com.ironhack.bank_system.utils.Utils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class UserService extends BaseService {

    private final UserRepository userRepository;
    private final CustomMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(
            UserRepository userRepository,
            CustomMapper modelMapper,
            PasswordEncoder passwordEncoder
    ) {
        super(modelMapper);
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDTO> findAll() {
        return this.userRepository
                .findAll()
                .stream()
                .map(userBD -> toDto(userBD))
                .collect(Collectors.toList());
    }

    public UserDTO findUserById(Long id) {
        UserDTO userDTO = userRepository.findById(id)
                .stream()
                .map(userBD -> this.toDto(userBD))
                .findFirst()
                .orElse(null);
        if (Objects.isNull(userDTO)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account admin was not found.");
        }
        return userDTO;
    }

    public Optional<org.springframework.security.core.userdetails.User> findByHashedKey(String hashedKey) {
        Optional<User> userFromBD = userRepository.findByHashedKey(hashedKey);
        if (userFromBD.isPresent()) {
            org.springframework.security.core.userdetails.User userFromUserDetails = new org.springframework.security.core.userdetails.User(
                    userFromBD.get().getUsername(),
                    userFromBD.get().getPassword(),
                    true,
                    true,
                    true,
                    true,
                    AuthorityUtils.createAuthorityList("USER"));
            return Optional.of(userFromUserDetails);
        }
        return Optional.empty();
    }

    public UserDTO create(UserDTO userDTO) {
        try {
            if (Objects.isNull(userDTO.getRol())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The User need at least one rol.");
            } else {
                UserDTO resultDto = Optional.of(userDTO)
                        .map(tDto -> {
                            User user = toEntity(tDto);
                            user.setPassword(passwordEncoder.encode(user.getPassword()));
                            user.setHashedKey(passwordEncoder.encode(Utils.generateRamdonString()));
                            return user;
                        })
                        .map(userRepository::save)
                        .map(entity -> toDto(entity))
                        .orElse(null);
                if (Objects.isNull(resultDto)) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account user was not created.");
                }
                return resultDto;
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user was not created.");
        }

    }

    public UserDTO update(Long id, UserDTO userDTO) {
        try {
            UserDTO resultDto = userRepository.findById(id)
                    .map(entity -> {
                        entity.setUsername(userDTO.getUsername());
                        entity.setPassword(passwordEncoder.encode(userDTO.getPassword()));
                        entity.setRol(Rol.valueOf(userDTO.getRol()));
                        return userRepository.save(entity);
                    })
                    .map(entity -> toDto(entity))
                    .orElse(null);
            if (Objects.isNull(resultDto)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account user was not updated.");
            }
            return resultDto;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account user was not updated.");
        }
    }

    public void delete(Long id) {
        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account user was not deleted.");
        }
    }
}
