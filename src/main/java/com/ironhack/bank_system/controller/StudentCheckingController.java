package com.ironhack.bank_system.controller;

import com.ironhack.bank_system.dto.CheckingDTO;
import com.ironhack.bank_system.dto.StudentCheckingDTO;
import com.ironhack.bank_system.services.CheckingService;
import com.ironhack.bank_system.services.StudentCheckingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "studentchecking")
@Api(value = "Student Checking Account Request")
public class StudentCheckingController {

    private final StudentCheckingService studentCheckingService;

    @Autowired
    public StudentCheckingController(StudentCheckingService studentCheckingService) {
        this.studentCheckingService = studentCheckingService;
    }

    @GetMapping
    @ApiOperation(value = "Get all student checking accounts", notes = "return all student checking accounts")
    @PreAuthorize("hasRole('ADMIN')")
    public List<StudentCheckingDTO> findAll() {
        return studentCheckingService.findAll();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Search one student checking account", notes = "return one student checking account")
    @PreAuthorize("hasRole('ADMIN')")
    public StudentCheckingDTO findAccountById(@PathVariable Long id) {
        return studentCheckingService.findAccountById(id);
    }

    @PostMapping
    @ApiOperation(value = "Create one student checking account", notes = "return one student checking account")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public StudentCheckingDTO createAccount(@Valid @RequestBody StudentCheckingDTO studentCheckingDTO) {
        return studentCheckingService.create(studentCheckingDTO);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Updated one student checking account", notes = "return one student checking account")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTHOLDERS', 'THIRDPARTY')")
    @ResponseStatus(HttpStatus.OK)
    public StudentCheckingDTO updateAccount(@PathVariable Long id, @RequestBody @Valid StudentCheckingDTO studentCheckingDTO) {
        return studentCheckingService.update(id, studentCheckingDTO);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete one student checking account", notes = "Student checking account deleted")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAccount(@PathVariable Long id) {
        studentCheckingService.delete(id);
    }
}


//    @ApiResponses({
//        @ApiResponse(code = 200, message = "Exists one student checking account at least"),
//        @ApiResponse(code = 204, message = "Doesn't exist any account"),
//        @ApiResponse(code = 400, message = "The petition failed"),
//        @ApiResponse(code = 401, message = "Unauthorized user")})
