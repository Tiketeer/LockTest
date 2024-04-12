package com.tiketeer.Tiketeer.domain.stresstest.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.purchase.repository.PurchaseRepository;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;

@Service
public class CleanUpUseCase {
	private MemberRepository memberRepository;
	private PurchaseRepository purchaseRepository;
	private TicketRepository ticketRepository;
	private TicketingRepository ticketingRepository;

	@Autowired
	public CleanUpUseCase(MemberRepository memberRepository, PurchaseRepository purchaseRepository,
		TicketRepository ticketRepository, TicketingRepository ticketingRepository) {
		this.memberRepository = memberRepository;
		this.purchaseRepository = purchaseRepository;
		this.ticketRepository = ticketRepository;
		this.ticketingRepository = ticketingRepository;
	}

	public void cleanUp() {
		ticketRepository.deleteAllInBatch();
		ticketingRepository.deleteAllInBatch();
		purchaseRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
	}
}
