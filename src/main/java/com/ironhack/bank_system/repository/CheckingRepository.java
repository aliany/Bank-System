package com.ironhack.bank_system.repository;

import com.ironhack.bank_system.model.Checking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckingRepository extends JpaRepository <Checking, Long> {
}
