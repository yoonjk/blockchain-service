package com.ibm.fabric.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.fabric.service.AccountService;

@RestController
@RequestMapping("/mycc")
public class TransferController {

	@Autowired
	AccountService accountService;
	
	@GetMapping("/accounts/{accountName}")
	public String getAccountAmount(@PathVariable String accountName) {
		return accountService.getAccountAmount(accountName);
	}
}
