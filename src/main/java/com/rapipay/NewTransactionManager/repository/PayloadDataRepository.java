package com.rapipay.NewTransactionManager.repository;

import com.rapipay.NewTransactionManager.entities.TransactionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayloadDataRepository extends JpaRepository<TransactionRequest, String>{

}
