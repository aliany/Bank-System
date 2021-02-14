package com.ironhack.bank_system.repository;

import com.ironhack.bank_system.dto.AccountDTO;
import com.ironhack.bank_system.model.Account;
import org.javamoney.moneta.Money;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByIban(@Param("iban") String iban);

    @Query(
            value = "SELECT a FROM Account a "
                    + "WHERE a.iban = :iban "
                    + "AND a.primaryOwner.name LIKE %:name%"
    )
    Optional<Account> findByIbanAndPrimaryOwnerName(
            @Param("iban") String iban,
            @Param("name") String name
    );

    @Query(
            value = "SELECT a.balance FROM Account a "
                    + "WHERE a.iban = :iban "
    )
    Optional<Money> getBalance(@Param("iban") String iban);

    Optional<Account> findBySecretKey(String secretKey);

}
