package com.ibm.fabric.utils;

public class FabricUtils {
	
    public static String[] transactionValidCode=new String[]
            {"VALID","NIL_ENVELOPE_VALUE","BAD_PAYLOAD_VALUE","BAD_COMMON_HEADER_VALUE",
              "BAD_CREATOR_SIGNATURE_VALUE","INVALID_ENDORSER_TRANSACTION_VALUE","INVALID_CONFIG_TRANSACTION_VALUE","UNSUPPORTED_TX_PAYLOAD_VALUE",
              "BAD_PROPOSAL_TXID_VALUE","DUPLICATE_TXID_VALUE","ENDORSEMENT_POLICY_FAILURE_VALUE","MVCC_READ_CONFLICT_VALUE",
              "PHANTOM_READ_CONFLICT_VALUE","UNKNOWN_TX_TYPE_VALUE","TARGET_CHAIN_NOT_FOUND_VALUE","MARSHAL_TX_ERROR_VALUE",
              "NIL_TXACTION_VALUE","EXPIRED_CHAINCODE_VALUE","CHAINCODE_VERSION_CONFLICT_VALUE","BAD_HEADER_EXTENSION_VALUE",
              "BAD_CHANNEL_HEADER_VALUE","BAD_RESPONSE_PAYLOAD_VALUE","BAD_RWSET_VALUE","ILLEGAL_WRITESET_VALUE",
              "INVALID_WRITESET_VALUE","NOT_VALIDATED_VALUE","INVALID_OTHER_REASON_VALUE"
            };
}
