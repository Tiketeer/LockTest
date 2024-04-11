package com.tiketeer.Tiketeer.testhelper;

import java.util.function.Supplier;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class Transaction {
	@Transactional
	public <T> T invoke(Supplier<T> functionInTransaction) {
		return functionInTransaction.get();
	}
}
