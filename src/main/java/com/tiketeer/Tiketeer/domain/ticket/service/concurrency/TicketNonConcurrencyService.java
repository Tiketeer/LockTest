package com.tiketeer.Tiketeer.domain.ticket.service.concurrency;

import java.util.UUID;

import org.springframework.data.domain.Limit;

import com.tiketeer.Tiketeer.domain.purchase.exception.NotEnoughTicketException;
import com.tiketeer.Tiketeer.domain.purchase.service.PurchaseCrudService;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;

public class TicketNonConcurrencyService implements TicketConcurrencyService {
	private final TicketRepository ticketRepository;
	private final PurchaseCrudService purchaseCrudService;

	public TicketNonConcurrencyService(TicketRepository ticketRepository, PurchaseCrudService purchaseCrudService) {
		this.ticketRepository = ticketRepository;
		this.purchaseCrudService = purchaseCrudService;
	}

	@Override
	public void assignPurchaseToTicket(UUID ticketingId, UUID purchaseId, int ticketCount) {
		var purchase = purchaseCrudService.findById(purchaseId);
		var tickets = ticketRepository.findByTicketingIdAndPurchaseIsNullOrderById(
			ticketingId, Limit.of(ticketCount));

		if (tickets.size() < ticketCount) {
			throw new NotEnoughTicketException();
		}

		tickets.forEach(ticket -> {
			ticket.setPurchase(purchase);
		});
	}
}
