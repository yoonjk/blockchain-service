package com.ibm.fabric.service;

import java.util.function.Consumer;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.BlockEvent.TransactionEvent;
import org.hyperledger.fabric.sdk.BlockInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ibm.fabric.client.FabricCommitListener;
import com.ibm.fabric.client.GatewayClient;
import com.ibm.fabric.model.Car;

@Service
public class FabcarService {
	
	private static final Logger logger = LoggerFactory.getLogger(FabcarService.class);
	GatewayClient gatewayClient;
	
	@Autowired
	public void setGatewayClient(GatewayClient gatewayClient) {
		this.gatewayClient = gatewayClient;
		
		gatewayClient.setUserId("user1");
		gatewayClient.setConfigFle("connection.json");
		
	}
	
	public String getAllCars() {
        // create a gateway connection
		String value = null;
        try {
            Contract contract = gatewayClient.getContract("mychannel", "fabcar");

            byte[] result = null;
            result = contract.evaluateTransaction("queryAllCars");
            System.out.println(new String(result));
            
            value = new String(result);
            logger.info("queryAllCars evaluateTransaction result: {}", new String(result));
            result = contract.evaluateTransaction("queryCar", "CAR12");
            logger.info("queryCar evaluateTransaction result: {}", new String(result));

        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        return value;
	}
	
	public String getCar(String carName) {
        // create a gateway connection
		String value = null;
        try {
            Contract contract = gatewayClient.getContract("mychannel", "fabcar");

            byte[] result = null;
   
            result = contract.evaluateTransaction("queryCar", carName);
            
            value = new String(result);
            logger.info("queryCar evaluateTransaction result: {}", value);

        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        return value;
	}
	
	
	public String createCar(Car car) {
        FabricCommitListener commitListener = new FabricCommitListener();
        String value = null;
        // create a gateway connection
        try {
            Contract contract = gatewayClient.getContract("mychannel", "fabcar");
           
            Network network = gatewayClient.getNetwork("mychannel");
            Consumer<BlockEvent> listener = network.addBlockListener(event -> {
//            	logger.info("event getAllFields:{}", event.getBlock().getAllFields());
//            	logger.info("Block getData:{}", event.getBlock().getData());
            	logger.info("BlockNumber: {}", event.getBlockNumber());
            	
            	try {
                	for (TransactionEvent txEvent : event.getTransactionEvents()) {

                		if (txEvent.isValid())
                		
                		logger.info("txEvent isValid:{}", txEvent.getChannelId());	
                    	logger.info("ChannelI:{}", txEvent.getChannelId());
                    	logger.info("EnvelopType:{}", txEvent.getType());
                    	logger.info("TransactionID: {}", txEvent.getTransactionID());
                    	
                    	BlockInfo blockInfo = network.getChannel().queryBlockByNumber(event.getBlockNumber() - 1);
                    	
                    	logger.info("BlockNumber: {}", blockInfo.getBlockNumber());
                	};
            	} catch (Exception e) {
            		e.printStackTrace();
            	}

            	
            });
            byte[] result;


         // Add Listener
            network.addCommitListener(commitListener, network.getChannel().getPeers(), "createCar");
            
            result = contract.submitTransaction("createCar", car.getKey(), car.getColour(), car.getMake(), car.getModel(), car.getOwner());
            value = new String(result);
            logger.info("createCar submitTransaction result: {}", value);
            
        } catch (Exception e) {
        	e.printStackTrace();
        }	
        
        return value;
	}
}
