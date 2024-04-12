package com.tiketeer.Tiketeer.domain.purchase.usecase;

import com.tiketeer.Tiketeer.domain.purchase.usecase.dto.CreatePurchaseCommandDto;
import com.tiketeer.Tiketeer.domain.purchase.usecase.dto.CreatePurchaseResultDto;

public interface CreatePurchaseUseCase {
	CreatePurchaseResultDto createPurchase(CreatePurchaseCommandDto command);
}
