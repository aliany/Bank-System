package com.ironhack.bank_system.controller;

import com.ironhack.bank_system.dto.UserDTO;
import com.ironhack.bank_system.services.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @ApiOperation(value = "Get all users", notes = "return all users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDTO> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Search one user", notes = "return one user")
    @PreAuthorize("hasRole('ADMIN')")
    public UserDTO findUserById(@PathVariable Long id) {
        return userService.findUserById(id);
    }

    @PostMapping
    @ApiOperation(value = "Create one user", notes = "return one user")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public UserDTO createUser(@Valid @RequestBody UserDTO userDTO) {

        return userService.create(userDTO);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Updated one user", notes = "return one user")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTHOLDERS')")
    public UserDTO updateUser(@PathVariable Long id, @RequestBody @Valid UserDTO userDTO) {
        return userService.update(id, userDTO);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete one user", notes = "User deleted")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable Long id) {
        userService.delete(id);
    }

}
