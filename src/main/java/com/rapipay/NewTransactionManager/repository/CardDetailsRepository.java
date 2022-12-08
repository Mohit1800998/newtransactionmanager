package com.rapipay.NewTransactionManager.repository;

import com.rapipay.NewTransactionManager.entities.CardDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardDetailsRepository extends JpaRepository<CardDetails,String> {

}
