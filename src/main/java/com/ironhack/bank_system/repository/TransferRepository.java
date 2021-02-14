package com.ironhack.bank_system.repository;

import com.ironhack.bank_system.enums.TransferType;
import com.ironhack.bank_system.model.Account;
import com.ironhack.bank_system.model.IMaxAmount;
import com.ironhack.bank_system.model.Transfer;
import com.ironhack.bank_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    @Query(
            value = "SELECT t FROM Transfer t "
                    + "WHERE t.accountTo.id = :accountId "
                    + "AND t.type = :type order by t.id desc"
    )
    public List<Transfer> findByAccountToAndType(
            @Param("accountId") Long accountId,
            @Param("type") TransferType type
    );

    @Query(
            value = "SELECT SUM(t.amount) as maxAmount FROM Transfer t "
                    + "WHERE CAST(t.date AS DATE) = :date "
                    + "GROUP BY t.`account_from_id` "
                    + "Order BY SUM(t.`amount`) DESC "
                    + "LIMIT 1",
            nativeQuery = true
    )
    public Optional<IMaxAmount> findMaxAmountInDate(
            @Param("date") String date
    );

    @Query(
            value = "SELECT SUM(t.amount) as maxAmount FROM Transfer t "
                    + "WHERE CAST(t.date AS DATE) = :date "
                    + "AND t.account_from_id = :account_from_id",
            nativeQuery = true
    )
    public Optional<IMaxAmount> findMaxAmountInDateFromAccount(
            @Param("date") String date,
            @Param("account_from_id") Long account_from_id
    );

    public List<Transfer> findByAccountFromOrderByCreatedDateDesc(Account account);
    
}
