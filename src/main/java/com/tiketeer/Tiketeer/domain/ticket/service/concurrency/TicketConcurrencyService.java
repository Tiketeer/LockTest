package com.tiketeer.Tiketeer.domain.ticket.service.concurrency;

import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface TicketConcurrencyService {
	void assignPurchaseToTicket(UUID ticketingId, UUID purchaseId, int ticketCount);
}
