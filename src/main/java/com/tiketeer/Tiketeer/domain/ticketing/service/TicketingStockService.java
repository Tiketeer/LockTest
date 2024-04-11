package com.tiketeer.Tiketeer.domain.ticketing.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.ticket.Ticket;
import com.tiketeer.Tiketeer.domain.ticket.service.TicketCrudService;
import com.tiketeer.Tiketeer.domain.ticketing.exception.TicketingNotFoundException;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;

@Service
@Transactional(readOnly = true)
public class TicketingStockService {
	private final TicketCrudService ticketCrudService;
	private final TicketingRepository ticketingRepository;

	@Autowired
	public TicketingStockService(TicketCrudService ticketCrudService, TicketingRepository ticketingRepository) {
		this.ticketCrudService = ticketCrudService;
		this.ticketingRepository = ticketingRepository;
	}

	@Transactional
	public void createStock(UUID ticketingId, int stock) {
		var ticketing = ticketingRepository.findById(ticketingId).orElseThrow(TicketingNotFoundException::new);
		ticketCrudService.createTickets(ticketing.getId(), stock);
	}

	@Transactional
	public void updateStock(UUID ticketingId, int newStock) {
		var tickets = ticketCrudService.listTicketByTicketingId(ticketingId);

		var numOfTickets = tickets.size();
		if (numOfTickets > newStock) {
			dropNumOfTicketsByTicketing(ticketingId, numOfTickets - newStock);

		} else if (numOfTickets < newStock) {
			createStock(ticketingId, newStock - numOfTickets);
		}
	}

	@Transactional
	public void dropAllStock(UUID ticketingId) {
		var tickets = ticketCrudService.listTicketByTicketingId(ticketingId);

		var ticketIdsForDelete = tickets.stream()
			.map(Ticket::getId).toList();
		ticketCrudService.deleteAllByTicketIds(ticketIdsForDelete);
	}

	private void dropNumOfTicketsByTicketing(UUID ticketingId, int numOfTickets) {
		var ticketing = ticketingRepository.findById(ticketingId).orElseThrow(TicketingNotFoundException::new);

		var tickets = ticketCrudService.listTicketByTicketingId(ticketing.getId());

		var ticketIdsForDelete = tickets.stream()
			.limit(numOfTickets)
			.map(Ticket::getId).toList();

		ticketCrudService.deleteAllByTicketIds(ticketIdsForDelete);
	}
}
