package com.rapipay.NewTransactionManager.repository;

import com.rapipay.NewTransactionManager.entities.TransactionResponse;
import com.rapipay.NewTransactionManager.utils.QueryName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;

@Repository
public interface TransactionResponseRepository extends JpaRepository<TransactionResponse, String> {

    @Transactional
    @Modifying
    @Query(value = QueryName.UpdateAidAndTc,nativeQuery = true)
    int updateAidTcInDB(@Param("tc") String tc,@Param("modifiedOn") Date modifiedOn,@Param("modifiedBy") String modifiedBy,@Param("rrn") String rrn,@Param("serviceType") String serviceType,@Param("amount") double amount);
}
