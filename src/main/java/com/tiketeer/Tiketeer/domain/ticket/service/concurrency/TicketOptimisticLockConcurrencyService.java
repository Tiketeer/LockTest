package com.tiketeer.Tiketeer.domain.ticket.service.concurrency;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Limit;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import com.tiketeer.Tiketeer.domain.purchase.exception.NotEnoughTicketException;
import com.tiketeer.Tiketeer.domain.purchase.service.PurchaseCrudService;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;

public class TicketOptimisticLockConcurrencyService implements TicketConcurrencyService {

	private final TicketRepository ticketRepository;
	private final PurchaseCrudService purchaseCrudService;

	@Autowired
	public TicketOptimisticLockConcurrencyService(TicketRepository ticketRepository,
		PurchaseCrudService purchaseCrudService) {
		this.ticketRepository = ticketRepository;
		this.purchaseCrudService = purchaseCrudService;
	}

	@Override
	@Retryable(
		retryFor = OptimisticLockingFailureException.class,
		backoff = @Backoff(delay = 100),
		maxAttempts = 100
	)
	public void assignPurchaseToTicket(UUID ticketingId, UUID purchaseId, int ticketCount) {
		var purchase = purchaseCrudService.findById(purchaseId);
		var tickets = ticketRepository.findByTicketingIdAndPurchaseIsNullOrderByIdWithOptimisticLock(
			ticketingId, Limit.of(ticketCount));

		if (tickets.size() < ticketCount) {
			throw new NotEnoughTicketException();
		}

		tickets.forEach(ticket -> {
			ticket.setPurchase(purchase);
		});
		ticketRepository.flush();
	}
}
