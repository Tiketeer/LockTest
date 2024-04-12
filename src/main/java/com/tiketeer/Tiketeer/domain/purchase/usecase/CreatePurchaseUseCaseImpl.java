package com.tiketeer.Tiketeer.domain.purchase.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.service.MemberCrudService;
import com.tiketeer.Tiketeer.domain.member.service.MemberPointService;
import com.tiketeer.Tiketeer.domain.purchase.Purchase;
import com.tiketeer.Tiketeer.domain.purchase.repository.PurchaseRepository;
import com.tiketeer.Tiketeer.domain.purchase.usecase.dto.CreatePurchaseCommandDto;
import com.tiketeer.Tiketeer.domain.purchase.usecase.dto.CreatePurchaseResultDto;
import com.tiketeer.Tiketeer.domain.ticket.service.concurrency.TicketConcurrencyService;
import com.tiketeer.Tiketeer.domain.ticketing.service.TicketingService;

public class CreatePurchaseUseCaseImpl {

	protected final PurchaseRepository purchaseRepository;
	protected final TicketingService ticketingService;
	protected final MemberPointService memberPointService;
	protected final MemberCrudService memberCrudService;
	protected final TicketConcurrencyService ticketConcurrencyService;

	@Autowired
	public CreatePurchaseUseCaseImpl(
		PurchaseRepository purchaseRepository,
		TicketingService ticketingService,
		MemberPointService memberPointService,
		MemberCrudService memberCrudService,
		TicketConcurrencyService ticketConcurrencyService
	) {
		this.purchaseRepository = purchaseRepository;
		this.ticketingService = ticketingService;
		this.memberPointService = memberPointService;
		this.memberCrudService = memberCrudService;
		this.ticketConcurrencyService = ticketConcurrencyService;
	}

	@Transactional
	public CreatePurchaseResultDto createPurchase(CreatePurchaseCommandDto command) {
		var ticketingId = command.getTicketingId();
		var count = command.getCount();

		var member = memberCrudService.findByEmail(command.getMemberEmail());

		var ticketing = ticketingService.findById(ticketingId);

		memberPointService.subtractPoint(member.getId(), ticketing.getPrice() * count);

		var newPurchase = purchaseRepository.save(Purchase.builder().member(member).build());

		ticketConcurrencyService.assignPurchaseToTicket(ticketingId, newPurchase.getId(), count);

		return CreatePurchaseResultDto.builder()
			.purchaseId(newPurchase.getId())
			.createdAt(newPurchase.getCreatedAt())
			.build();
	}
}
