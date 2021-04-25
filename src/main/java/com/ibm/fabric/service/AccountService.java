package com.ibm.fabric.service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Transaction;
import org.hyperledger.fabric.protos.ledger.rwset.kvrwset.KvRwset;
import org.hyperledger.fabric.sdk.BlockInfo;
import org.hyperledger.fabric.sdk.BlockInfo.EnvelopeType;
import org.hyperledger.fabric.sdk.BlockchainInfo;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.TxReadWriteSetInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.protobuf.InvalidProtocolBufferException;
import com.ibm.fabric.base.BaseService;
import com.ibm.fabric.model.TransferDto;
import com.ibm.fabric.utils.FabricUtils;

@Service
public class AccountService extends BaseService {
	private static final Logger logger = LoggerFactory.getLogger(FabcarService.class);

	public String getAccountAmount(String accountName) {
        // create a gateway connection
		String value = null;
        try {
            Contract contract = getGatewayClient().getContract("paychannel", "mycc");

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
	
	/**
	 * createTransaction Transaction을 사용하는 경우 
	 * @param transferDto
	 * @return
	 */
	public String transfer2(TransferDto transferDto) {
        // create a gateway connection
		String value = null;
        try {        	
            ArrayList<String> argsList = new ArrayList<>();
 
            logger.info("=======================================================");
            Contract contract = getGatewayClient().getContract("paychannel", "mycc");
            logger.info("=======================================================");
            
            argsList.add(transferDto.getSourceAccount());
            argsList.add(transferDto.getTargetAccount());
            argsList.add(transferDto.getAmount());
            
            byte[] result = null;
            Transaction transaction = contract.createTransaction("invoke");
            
            result = transaction.submit(transferDto.getSourceAccount(), transferDto.getTargetAccount(), transferDto.getAmount());
    
            value = new String(result);
            
            logger.info("invoke createTransaction result: {}", value, transaction);

        } catch (Exception e) {
        	e.printStackTrace();
        	value = e.getMessage();
        } 
        return value;
	}

	public String transfer(TransferDto transferDto) {
        // create a gateway connection
		String value = null;
        try {        	
            ArrayList<String> argsList = new ArrayList<>();
 
            logger.info("=======================================================");
            Contract contract = getGatewayClient().getContract("paychannel", "mycc");
            logger.info("=======================================================");
            
            argsList.add(transferDto.getSourceAccount());
            argsList.add(transferDto.getTargetAccount());
            argsList.add(transferDto.getAmount());
            
            byte[] result = null;
            result = contract.submitTransaction("invoke", transferDto.getSourceAccount(), transferDto.getTargetAccount(), transferDto.getAmount());
            value = new String(result);
            
            logger.info("queryCar evaluateTransaction result: {}", value);

        } catch (Exception e) {
        	e.printStackTrace();
        	value = e.getMessage();
        } 
        return value;
	}
	
	public String queryBlockByNumber(Long blockNumber) {
		String value = null;
		Gson gson = new Gson();
        try {        	
            ArrayList<String> argsList = new ArrayList<>();
 
            logger.info("=======================================================");
            
            Network network = getGatewayClient().getNetwork("paychannel");
            
            Channel channel = network.getChannel();

            BlockInfo blockInfo = channel.queryBlockByNumber(blockNumber);
            
            logger.info("1.queryBlockByNumber result: {}", blockInfo.getBlock().getHeader());
            logger.info("2.getBlock result: {}", blockInfo.getBlock());
            logger.info("2.1.getEnvelopeCount result: {}", blockInfo.getEnvelopeCount());

            value = gson.toJson(getRWSetFromBlock(blockInfo));
            


        } catch (Exception e) {
        	e.printStackTrace();
        	value = e.getMessage();
        } 
        return value;
	}
	
	public String queryByTransactionID(String transactionID) {
		String value = null;
		Gson gson = new Gson();
        try {        	
            ArrayList<String> argsList = new ArrayList<>();
 
            logger.info("=======================================================");
            
            Network network = getGatewayClient().getNetwork("paychannel");
            
            Channel channel = network.getChannel();

            BlockInfo blockInfo = channel.queryBlockByTransactionID(transactionID);
            
            logger.info("1.queryBlockByNumber result: {}", blockInfo.getBlock().getHeader());
            logger.info("2.getBlock result: {}", blockInfo.getBlock());
            logger.info("2.1.getEnvelopeCount result: {}", blockInfo.getEnvelopeCount());

            value = gson.toJson(getRWSetFromBlock(blockInfo, transactionID));
        } catch (Exception e) {
        	e.printStackTrace();
        	value = e.getMessage();
        } 
        return value;
	}	
	
	public static List<Map> getRWSetFromBlock(BlockInfo blockInfo, String txID) throws InvalidProtocolBufferException {
        List<Map> transactionList = new ArrayList<>();
        boolean isHeader = false;
        
        for (BlockInfo.EnvelopeInfo envelopeInfo : blockInfo.getEnvelopeInfos()) {
            String id = envelopeInfo.getCreator().getId();
            String mspid = envelopeInfo.getCreator().getMspid();
 
            if (envelopeInfo.getType() == EnvelopeType.TRANSACTION_ENVELOPE) {
            	
            	if (!isHeader) {
            		Map<String, Object> txHeaderMap = new HashMap<>();
            		txHeaderMap.put("blockNumber", blockInfo.getBlockNumber());
            		txHeaderMap.put("transactionCount", blockInfo.getTransactionCount());
            		txHeaderMap.put("ChannelID", blockInfo.getChannelId());
            		transactionList.add(txHeaderMap);
            		
            		isHeader = true;
            	}
            	
                Date timestamp = envelopeInfo.getTimestamp();
                BlockInfo.TransactionEnvelopeInfo transactionEnvelopeInfo = (BlockInfo.TransactionEnvelopeInfo) envelopeInfo;
                String transactionID = transactionEnvelopeInfo.getTransactionID();
                
                if (!txID.equalsIgnoreCase(transactionID)) {
                	continue;
                }
                
                boolean valid = transactionEnvelopeInfo.isValid();
                byte validationCode = transactionEnvelopeInfo.getValidationCode();
                for (BlockInfo.TransactionEnvelopeInfo.TransactionActionInfo transactionActionInfo : transactionEnvelopeInfo.getTransactionActionInfos()) {
                    Map<String, Object> transactionMap = new HashMap<>();
                    transactionMap.put("transactionID", transactionID);
                    transactionMap.put("timestamp", timestamp.getTime());
                    transactionMap.put("validationCode", validationCode);
                    
                    int index = validationCode;
					if (index <= 24) {
						transactionMap.put("validationCodeName", FabricUtils.transactionValidCode[index]);
					} else {
						transactionMap.put("validationCodeName", FabricUtils.transactionValidCode[index-229]);
					}
                    int chaincodeInputArgsCount = transactionActionInfo.getChaincodeInputArgsCount();
                    String[] argus = new String[chaincodeInputArgsCount];
                    for (int i = 0; i < chaincodeInputArgsCount; i++) {
                        argus[i] = new String(transactionActionInfo.getChaincodeInputArgs(i));
                    }
                    transactionMap.put("INPUT", argus);
                    transactionMap.put("status", transactionActionInfo.getResponseStatus());
                    String chaincodeIDName = transactionActionInfo.getChaincodeIDName();
                    transactionMap.put("chaincodeName", chaincodeIDName);
                    String chaincodeIDVersion = transactionActionInfo.getChaincodeIDVersion();
                    transactionMap.put("chaincodeVersion", chaincodeIDVersion);
                    TxReadWriteSetInfo rwsetInfo = transactionActionInfo.getTxReadWriteSet();
                    if (null != rwsetInfo) {
                        List<Map> rwList = new ArrayList<Map>();
 
                        for (TxReadWriteSetInfo.NsRwsetInfo nsRwsetInfo : rwsetInfo.getNsRwsetInfos()) {
 
                            Map<String, Object> rwMap = new HashMap<>();
                            Map<String, String> writeMap = new HashMap<>();
                            KvRwset.KVRWSet rws = nsRwsetInfo.getRwset();

                            for (KvRwset.KVWrite writeList : rws.getWritesList()) {
                                String valAsString = printableString(new String(writeList.getValue().toByteArray(), StandardCharsets.UTF_8));
                                writeList.getKey();
                                writeMap.put(writeList.getKey(), valAsString);
                            }
                            rwMap.put("WRITE", writeMap);
                            rwList.add(rwMap);
 
                        }
                        transactionMap.put("OUTPUT", rwList);
                    }
                    transactionList.add(transactionMap);
                    
                    return transactionList;
                }
                
            }

        }
        
        return transactionList;
	}
	
	public static List<Map> getRWSetFromBlock(BlockInfo blockInfo) throws InvalidProtocolBufferException {
        List<Map> transactionList = new ArrayList<>();
        boolean isHeader = false;
        
        for (BlockInfo.EnvelopeInfo envelopeInfo : blockInfo.getEnvelopeInfos()) {
            String id = envelopeInfo.getCreator().getId();
            String mspid = envelopeInfo.getCreator().getMspid();
 
            if (envelopeInfo.getType() == EnvelopeType.TRANSACTION_ENVELOPE) {
                Date timestamp = envelopeInfo.getTimestamp();
                BlockInfo.TransactionEnvelopeInfo transactionEnvelopeInfo = (BlockInfo.TransactionEnvelopeInfo) envelopeInfo;
                String transactionID = transactionEnvelopeInfo.getTransactionID();
                boolean valid = transactionEnvelopeInfo.isValid();
                byte validationCode = transactionEnvelopeInfo.getValidationCode();
            	if (!isHeader) {
            		Map<String, Object> txHeaderMap = new HashMap<>();
            		txHeaderMap.put("blockNumber", blockInfo.getBlockNumber());
            		txHeaderMap.put("transactionCount", blockInfo.getTransactionCount());
            		txHeaderMap.put("ChannelID", blockInfo.getChannelId());
            		transactionList.add(txHeaderMap);
            		
            		isHeader = true;
            	}
            	
                for (BlockInfo.TransactionEnvelopeInfo.TransactionActionInfo transactionActionInfo : transactionEnvelopeInfo.getTransactionActionInfos()) {
                    Map<String, Object> transactionMap = new HashMap<>();
                    transactionMap.put("transactionID", transactionID);
                    transactionMap.put("timestamp", timestamp.getTime());
//                    transactionMap.put("isValid", valid);
//                    transactionMap.put("MSPID", mspid);
//                    transactionMap.put("usercert", id);
                    transactionMap.put("validationCode", validationCode);
                    int index = validationCode;
					if (index <= 24) {
						transactionMap.put("validationCodeName", FabricUtils.transactionValidCode[index]);
					} else {
						transactionMap.put("validationCodeName", FabricUtils.transactionValidCode[index-229]);
					}
                    int chaincodeInputArgsCount = transactionActionInfo.getChaincodeInputArgsCount();
                    String[] argus = new String[chaincodeInputArgsCount];
                    for (int i = 0; i < chaincodeInputArgsCount; i++) {
                        argus[i] = new String(transactionActionInfo.getChaincodeInputArgs(i));
                    }
                    transactionMap.put("INPUT", argus);
                    transactionMap.put("status", transactionActionInfo.getResponseStatus());
//                    transactionMap.put("endorsementsCount", transactionActionInfo.getEndorsementsCount());
                    String chaincodeIDName = transactionActionInfo.getChaincodeIDName();
                    transactionMap.put("chaincodeName", chaincodeIDName);
                    String chaincodeIDVersion = transactionActionInfo.getChaincodeIDVersion();
                    transactionMap.put("chaincodeVersion", chaincodeIDVersion);
                    TxReadWriteSetInfo rwsetInfo = transactionActionInfo.getTxReadWriteSet();
                    if (null != rwsetInfo) {
                        List<Map> rwList = new ArrayList<Map>();
 
                        for (TxReadWriteSetInfo.NsRwsetInfo nsRwsetInfo : rwsetInfo.getNsRwsetInfos()) {
 
                            Map<String, Object> rwMap = new HashMap<>();
                            Map<String, String> writeMap = new HashMap<>();
                            KvRwset.KVRWSet rws = nsRwsetInfo.getRwset();
                            String[] readSet = new String[rws.getReadsCount()];
//                            int i = 0;
//                            for (KvRwset.KVRead readList : rws.getReadsList()) {
//                                String key = readList.getKey();
//                                logger.info("readSet key: {}", key);
//                                readSet[i++] = key;
//                            }
//                            rwMap.put("read", readSet);
                            for (KvRwset.KVWrite writeList : rws.getWritesList()) {
                                String valAsString = printableString(new String(writeList.getValue().toByteArray(), StandardCharsets.UTF_8));
                                writeList.getKey();
                                writeMap.put(writeList.getKey(), valAsString);
                            }
                            rwMap.put("WRITE", writeMap);
                            rwList.add(rwMap);
 
                        }
                        transactionMap.put("OUTPUT", rwList);
                    }
                    transactionList.add(transactionMap);
                }
            }
 
        }
        return transactionList;
    }
 
 
 
    static String printableString(String string) {
        int maxLogStringLength = 64;
        if (string == null || string.length() == 0) {
            return string;
        }
        String ret = string.replaceAll("[^\\p{Print}]", "?");
        ret = ret.substring(0, Math.min(ret.length(), maxLogStringLength)) + (ret.length() > maxLogStringLength ? "..." : "");
        return ret;
 
    }

	
	public String queryBlockHeight() {
		String value = null;
        try {        	
            ArrayList<String> argsList = new ArrayList<>();
 
            logger.info("=======================================================");
            
            Network network = getGatewayClient().getNetwork("paychannel");
            
            Channel channel = network.getChannel();

            BlockchainInfo blockInfo = channel.queryBlockchainInfo();
            
            value = Long.toString(blockInfo.getHeight());
            
            logger.info("queryBlockByNumber result: {}", value);

        } catch (Exception e) {
        	e.printStackTrace();
        	value = e.getMessage();
        } 
        return value;
	}
}
