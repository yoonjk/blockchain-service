package com.ibm.fabric.base;

import org.springframework.beans.factory.annotation.Autowired;

import com.ibm.fabric.client.GatewayClient;

public abstract class BaseService {

	GatewayClient gatewayClient;
	
	@Autowired
	public void setGatewayClient(GatewayClient gatewayClient) {
		this.gatewayClient = gatewayClient;
		
		gatewayClient.setUserId("user1");
		gatewayClient.setConfigFle("connection.json");
		
	}
	
	protected GatewayClient getGatewayClient() {
		return gatewayClient;
	}
}
