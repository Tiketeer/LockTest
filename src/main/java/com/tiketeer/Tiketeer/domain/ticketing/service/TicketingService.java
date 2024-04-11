package com.tiketeer.Tiketeer.domain.ticketing.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.domain.ticketing.exception.TicketingNotFoundException;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;

@Service
@Transactional(readOnly = true)
public class TicketingService {
	private final TicketingRepository ticketingRepository;

	@Autowired
	public TicketingService(TicketingRepository ticketingRepository) {
		this.ticketingRepository = ticketingRepository;
	}

	public Ticketing findById(UUID ticketingId) {
		return ticketingRepository.findById(ticketingId).orElseThrow(TicketingNotFoundException::new);
	}
}
