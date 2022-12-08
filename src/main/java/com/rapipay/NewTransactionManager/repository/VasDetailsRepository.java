package com.rapipay.NewTransactionManager.repository;

import com.rapipay.NewTransactionManager.entities.VasDetails;
import com.rapipay.NewTransactionManager.utils.QueryName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;

public interface VasDetailsRepository extends JpaRepository<VasDetails,String> {

    @Query(value = QueryName.getPosId, nativeQuery = true)
    String getIdFromDatabase(@Param("mid") String mid, @Param("tid") String tid);

}
