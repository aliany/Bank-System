package com.ironhack.bank_system.repository;

import com.ironhack.bank_system.model.StudentChecking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StudentCheckingRepository extends JpaRepository <StudentChecking, Long> {
}
