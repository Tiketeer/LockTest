package com.tiketeer.Tiketeer.domain.purchase.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.purchase.usecase.dto.CreatePurchasePLockCommandDto;
import com.tiketeer.Tiketeer.domain.purchase.usecase.dto.CreatePurchaseResultDto;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;

@Service
public class CreatePurchasePLockUseCase {
	private final TicketRepository ticketRepository;
	private final CreatePurchaseUseCaseCore innerUseCase;

	@Autowired
	public CreatePurchasePLockUseCase(
		TicketRepository ticketRepository,
		CreatePurchaseUseCaseCore innerUseCase
	) {
		this.ticketRepository = ticketRepository;
		this.innerUseCase = innerUseCase;
	}

	@Transactional
	public CreatePurchaseResultDto createPurchase(CreatePurchasePLockCommandDto command) {
		return innerUseCase.createPurchase(command,
			ticketRepository::findByTicketingIdAndPurchaseIsNullOrderByIdWithPessimisticLock);
	}
}
