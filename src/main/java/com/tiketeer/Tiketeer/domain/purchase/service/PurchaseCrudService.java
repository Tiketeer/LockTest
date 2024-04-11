package com.tiketeer.Tiketeer.domain.purchase.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.purchase.Purchase;
import com.tiketeer.Tiketeer.domain.purchase.exception.PurchaseNotFoundException;
import com.tiketeer.Tiketeer.domain.purchase.repository.PurchaseRepository;

@Service
@Transactional(readOnly = true)
public class PurchaseCrudService {
	private PurchaseRepository purchaseRepository;

	@Autowired
	public PurchaseCrudService(PurchaseRepository purchaseRepository) {
		this.purchaseRepository = purchaseRepository;
	}

	public Purchase findById(UUID purchaseId) {
		return purchaseRepository.findById(purchaseId).orElseThrow(PurchaseNotFoundException::new);
	}
}
