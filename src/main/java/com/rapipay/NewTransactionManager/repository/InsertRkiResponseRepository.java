package com.rapipay.NewTransactionManager.repository;

import com.rapipay.NewTransactionManager.entities.RkiRequestModel;
import com.rapipay.NewTransactionManager.utils.QueryName;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public interface InsertRkiResponseRepository extends JpaRepository<RkiRequestModel, String> {

    @Query(value = QueryName.insertRkiResponseDetails, nativeQuery = true)
    String insertRkiResponse(@Param("ksn_value") String ksn, @Param("ipek_value") String ipekValue);


}
