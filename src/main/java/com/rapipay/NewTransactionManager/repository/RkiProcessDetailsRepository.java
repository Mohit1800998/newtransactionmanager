package com.rapipay.NewTransactionManager.repository;

import com.rapipay.NewTransactionManager.entities.RkiRequestModel;
import com.rapipay.NewTransactionManager.utils.QueryName;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;


@Repository
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public interface RkiProcessDetailsRepository extends JpaRepository<RkiRequestModel, String> {


    @Query(value = QueryName.getRkiProcessDetails, nativeQuery = true)
    ArrayList getRkiProcessDetails(@Param("ksn_value") String ksn);


}
