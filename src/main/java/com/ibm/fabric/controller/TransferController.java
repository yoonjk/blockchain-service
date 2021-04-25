package com.ibm.fabric.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.fabric.model.TransferDto;
import com.ibm.fabric.service.AccountService;

@RestController
@RequestMapping("/pay")
public class TransferController {

	@Autowired
	AccountService accountService;
	
	@GetMapping("/accounts/{accountName}")
	public String getAccountAmount(@PathVariable String accountName) {
		return accountService.getAccountAmount(accountName);
	}
	
	@GetMapping("/blocks/{blockNumber}")
	public String queryBlockByNumber(@PathVariable String blockNumber) {
		return accountService.queryBlockByNumber(Long.parseLong(blockNumber));
	}
	
	@GetMapping("/blocks/{transactionID}")
	public String queryByTransactionID(@PathVariable String transactionID) {
		return accountService.queryByTransactionID(transactionID);
	}
	
	@GetMapping("/blocks/")
	public String queryBlockHeight() {
		return accountService.queryBlockHeight();
	}
	
	@PostMapping("/accounts/transfer")
	public String transfer(@RequestBody TransferDto transfer) {
		return accountService.transfer(transfer);
	}
}
