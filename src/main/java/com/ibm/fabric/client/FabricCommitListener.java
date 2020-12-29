package com.ibm.fabric.client;

import org.hyperledger.fabric.gateway.spi.CommitListener;
import org.hyperledger.fabric.gateway.spi.PeerDisconnectEvent;
import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.BlockEvent.TransactionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FabricCommitListener implements CommitListener {
	private static Logger logger = LoggerFactory.getLogger(FabricCommitListener.class);
	@Override
	public void acceptCommit(TransactionEvent transactionEvent) {
		BlockEvent blockEvent = transactionEvent.getBlockEvent();
		
		logger.info("TX BlockNumber:{}, PreviousHash:{}, TransactionCount:{} ", blockEvent.getBlockNumber(), 
				blockEvent.getPreviousHash(),
				blockEvent.getTransactionCount());
		
		

	}

	@Override
	public void acceptDisconnect(PeerDisconnectEvent disconnectEvent) {
		logger.info("peerDisconnected:{}", disconnectEvent);

	}

}
