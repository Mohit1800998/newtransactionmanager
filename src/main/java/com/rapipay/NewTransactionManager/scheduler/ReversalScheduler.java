package com.rapipay.NewTransactionManager.scheduler;


import com.rapipay.NewTransactionManager.entities.TransactionRequestResponse;
import com.rapipay.NewTransactionManager.service.reversalService.PaynextHost;
import com.rapipay.NewTransactionManager.utils.CollectionName;
import com.rapipay.NewTransactionManager.utils.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReversalScheduler {


    public static final Logger log = LogManager.getLogger(ReversalScheduler.class);

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    PaynextHost paynextHost;

    @Scheduled(cron = "${REVERSAL_CRON}")
    public void invoke() {
        loadData();
    }

    public void loadData() {
        try {

            Query query = new Query();
            query.addCriteria(Criteria.where("status").is(Constants.FAILURE));
            query.addCriteria(Criteria.where("isReversed").is(Constants.ISREVERSEDFALSE));
            query.addCriteria(Criteria.where("requestFor").regex("_"));

            List<TransactionRequestResponse> transactionRequestResponses = mongoTemplate.find(query, TransactionRequestResponse.class, CollectionName.TRANSACTIONREQUESTRESPONSE.collectionName);

            if(transactionRequestResponses.size()==0){
                Query queryForTimeout = new Query();
                queryForTimeout.addCriteria(Criteria.where("status").is(Constants.FAILURE));
                queryForTimeout.addCriteria(Criteria.where("isReversed").is(Constants.ISREVERSEDFALSE));
                queryForTimeout.addCriteria(Criteria.where("requestFor").regex("TIMEOUT"));

                transactionRequestResponses = mongoTemplate.find(queryForTimeout, TransactionRequestResponse.class, CollectionName.TRANSACTIONREQUESTRESPONSE.collectionName);

            }
            for (int i = 0; i < transactionRequestResponses.size(); i++) {
                TransactionRequestResponse payloadData  = transactionRequestResponses.get(i);
                String urn = payloadData.getMid();
                JSONObject request = new JSONObject(payloadData.getRequest());
                String treqId = payloadData.getTreqId();

                log.info("[URN_{}] Inside the scheduler",urn);
                paynextHost.reversalRequest(urn, treqId, payloadData.getRequestFor(), request);
            }
        } catch (NullPointerException e) {
            log.error("Error occured while fetching response {} : {} ",
                    e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error occured while fetching response  : {}",
                    e.getMessage());
        }


    }

}
