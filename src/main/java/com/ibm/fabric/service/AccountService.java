package com.ibm.fabric.service;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ibm.fabric.base.BaseService;

@Service
public class AccountService extends BaseService {
	private static final Logger logger = LoggerFactory.getLogger(FabcarService.class);
	
	public String getAccountAmount(String accountName) {
        // create a gateway connection
		String value = null;
        try {
            Contract contract = getGatewayClient().getContract("mychannel", "mycc");

            byte[] result = null;
   
            result = contract.evaluateTransaction("query", accountName);
            
            value = new String(result);
            logger.info("queryCar evaluateTransaction result: {}", value);

        } catch (ContractException e) {
        	e.printStackTrace();
        	value = e.getMessage();
        } catch (Exception e) {
        	e.printStackTrace();
        	value = e.getMessage();
        } 
        return value;
	}
}
