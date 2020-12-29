package com.ibm.fabric.client;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GatewayClient {
	private Path walletPath;
	
	private Wallet wallet;
	
	private Gateway gateway;
	
	private String userId;
	
	private String configFile;
	
	private Gateway.Builder builder;
	
	private HashMap<String, Network> channelNetwork = new HashMap();
	
	private static final Logger logger = LoggerFactory.getLogger(GatewayClient.class);
	
	private boolean isLoadFile;
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * 
	 */
	public GatewayClient() {
		
	}
	
	public Network getNetwork(String channelName) {
		logger.info("channelName:{}", channelName);
		Network network = channelNetwork.get(channelName);
		
		if (network == null) {
			network = findNetworkByChannelName(channelName);
			
			channelNetwork.put(channelName, network);
		}
		
		return network;
	}
	
	private void initConfig() {
        // Load a file system based wallet for managing identities.
        Path walletPath;
        Wallet wallet;

        // load a CCP
        Path networkConfigPath;   
        
        try {
        	
        	if (!isLoadFile) {
                // Load a file system based wallet for managing identities.
                walletPath = Paths.get("wallet");
                wallet = Wallet.createFileSystemWallet(walletPath);

                // load a CCP
                networkConfigPath = Paths.get("", "..", "basic-network", configFile);
         
                builder = Gateway.createBuilder();
                
            	builder.identity(wallet, userId).networkConfig(networkConfigPath).discovery(false);
            	
            	isLoadFile = true;
        	}
        } catch (Exception e) {
        	e.printStackTrace();
        	throw new RuntimeException(e);
        }
	}
	
	/**
	 * setConfigFle
	 * @param configFile
	 */
	public void setConfigFle(String configFile) {
		this.configFile = configFile;
	}
	
	/**
	 * getContract
	 * @param channelName
	 * @param chaincodeName
	 * @return
	 * @throws Exception
	 */
	public Contract getContract(String channelName, String chaincodeName) throws Exception {
		Network network;
		Contract contract = null;
		try {
			
			if (!isLoadFile) {
				initConfig();
			}
            // get the network and contract
			logger.info("channelName:{}, chaincodeName:{}",channelName, chaincodeName );
			
			gateway = builder.connect();
            network = getNetwork(channelName);
            
            contract = network.getContract(chaincodeName);
            
            
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException (e);
		}
		return contract;
	}

	/**
	 * 
	 * @param channelName
	 * @return
	 */
	private Network findNetworkByChannelName(String channelName) {
		Network network = gateway.getNetwork(channelName);
		
		return network;
	}
	
	public boolean isLoadFile() {
		return isLoadFile;
	}

	public void setLoadFile(boolean isLoadFile) {
		this.isLoadFile = isLoadFile;
	}

	
}
