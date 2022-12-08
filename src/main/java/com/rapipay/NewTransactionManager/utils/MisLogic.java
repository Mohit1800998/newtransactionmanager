package com.rapipay.NewTransactionManager.utils;

import com.rapipay.NewTransactionManager.entities.RkiProcessModal;
import com.rapipay.NewTransactionManager.responseModel.ResponseDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class MisLogic {

	@Autowired
	TripleDesSecurity objTripleDes;

	@Autowired
	Util util;

	public static Logger log = LogManager.getLogger(MisLogic.class);
	public void createIpekValues(String urn, RkiProcessModal objRkiProccess, ResponseDto responseDto) {
		
		try {
			
			log.info("[URN_{}] Inside createIpekValues ",urn);
			String baseDerivationKey = objTripleDes.decrypt(objRkiProccess.getBdkKey());
			String keySerialNumber = "FFFFFFFFFF"+objRkiProccess.getKsn1();
			
			log.info("[URN_{}]  BDK : {}",urn, objTripleDes.decrypt(objRkiProccess.getBdkKey()));
			log.info("[URN_{}]  KSN : {}",urn, "FFFFFFFFFF"+objRkiProccess.getKsn1());
			


			String result= IpekCreation.GetIPEK(keySerialNumber, baseDerivationKey);			
			
			objRkiProccess.setIpekValue(result.toUpperCase());
			log.info("[URN_{}] IPEK : {}",urn ,objRkiProccess.getIpekValue());
		} catch (NullPointerException e) {
			responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));


		} catch (Exception e) {
			responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));

		}

	}

}
