package com.tiketeer.Tiketeer.domain.purchase.usecase;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.service.MemberCrudService;
import com.tiketeer.Tiketeer.domain.member.service.MemberPointService;
import com.tiketeer.Tiketeer.domain.purchase.Purchase;
import com.tiketeer.Tiketeer.domain.purchase.exception.NotEnoughTicketException;
import com.tiketeer.Tiketeer.domain.purchase.repository.PurchaseRepository;
import com.tiketeer.Tiketeer.domain.purchase.usecase.dto.CreatePurchaseCommandDto;
import com.tiketeer.Tiketeer.domain.purchase.usecase.dto.CreatePurchaseResultDto;
import com.tiketeer.Tiketeer.domain.ticket.Ticket;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.service.TicketingService;

@Service
public class CreatePurchaseUseCaseCore {
	private final PurchaseRepository purchaseRepository;
	private final TicketingService ticketingService;
	private final MemberPointService memberPointService;
	private final MemberCrudService memberCrudService;
	private final TicketRepository ticketRepository;

	@FunctionalInterface
	interface ListTicketStrategy {
		List<Ticket> findByTicketingIdAndPurchaseIsNullOrderById(UUID ticketingId, Limit limit);
	}

	@Autowired
	public CreatePurchaseUseCaseCore(
		PurchaseRepository purchaseRepository,
		TicketingService ticketingService,
		MemberPointService memberPointService,
		MemberCrudService memberCrudService,
		TicketRepository ticketRepository
	) {
		this.purchaseRepository = purchaseRepository;
		this.ticketingService = ticketingService;
		this.memberPointService = memberPointService;
		this.memberCrudService = memberCrudService;
		this.ticketRepository = ticketRepository;
	}

	@Transactional
	public CreatePurchaseResultDto createPurchase(CreatePurchaseCommandDto command) {
		return execPurchaseLogic(command, ticketRepository::findByTicketingIdAndPurchaseIsNullOrderById);
	}

	@Transactional
	public CreatePurchaseResultDto createPurchase(CreatePurchaseCommandDto command,
		ListTicketStrategy listTicketStrategy) {
		return execPurchaseLogic(command, listTicketStrategy);
	}

	private CreatePurchaseResultDto execPurchaseLogic(CreatePurchaseCommandDto command,
		ListTicketStrategy listTicketStrategy) {
		var ticketingId = command.getTicketingId();
		var count = command.getCount();

		var member = memberCrudService.findByEmail(command.getMemberEmail());

		var ticketing = ticketingService.findById(ticketingId);

		memberPointService.subtractPoint(member.getId(), ticketing.getPrice() * count);

		var purchase = purchaseRepository.save(Purchase.builder().member(member).build());

		var tickets = listTicketStrategy.findByTicketingIdAndPurchaseIsNullOrderById(ticketingId, Limit.of(count));

		if (tickets.size() < count) {
			throw new NotEnoughTicketException();
		}

		tickets.forEach(ticket -> {
			ticket.setPurchase(purchase);
		});

		return CreatePurchaseResultDto.builder()
			.purchaseId(purchase.getId())
			.createdAt(purchase.getCreatedAt())
			.build();
	}
}
