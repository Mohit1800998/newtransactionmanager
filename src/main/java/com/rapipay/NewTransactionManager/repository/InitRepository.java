package com.rapipay.NewTransactionManager.repository;

import com.rapipay.NewTransactionManager.entities.HostCredentialDetails;
import com.rapipay.NewTransactionManager.utils.QueryName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface InitRepository extends JpaRepository<HostCredentialDetails,String>{

    @Query(value = QueryName.getHostCredential, nativeQuery = true)
    ArrayList<HostCredentialDetails> getHostCred(@Param("id") int id);
}
