package com.ironhack.bank_system;

import com.ironhack.bank_system.enums.Rol;
import com.ironhack.bank_system.model.User;
import com.ironhack.bank_system.repository.UserRepository;
import com.ironhack.bank_system.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import java.util.Properties;


@SpringBootApplication
public class BankSystemApplication {
    
    public static void main(String[] args) {
        
        SpringApplication.run(BankSystemApplication.class, args);
    
    }

    

}
