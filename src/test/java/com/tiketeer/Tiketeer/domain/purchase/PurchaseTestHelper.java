package com.tiketeer.Tiketeer.domain.purchase;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.data.domain.Limit;
import org.springframework.data.util.Pair;

import com.tiketeer.Tiketeer.domain.member.exception.MemberNotFoundException;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.purchase.exception.NotEnoughTicketException;
import com.tiketeer.Tiketeer.domain.purchase.repository.PurchaseRepository;
import com.tiketeer.Tiketeer.domain.ticket.Ticket;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.domain.ticketing.exception.TicketingNotFoundException;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;

@TestComponent
public class PurchaseTestHelper {

	@Autowired
	private PurchaseRepository purchaseRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private TicketRepository ticketRepository;
	@Autowired
	private TicketingRepository ticketingRepository;

	public Pair<Purchase, List<Ticket>> createPurchase(UUID memberId, UUID ticketingId, int count) {
		var member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
		var ticketing = ticketingRepository.findById(ticketingId).orElseThrow(TicketingNotFoundException::new);
		var purchase = purchaseRepository.save(Purchase.builder().member(member).build());

		if (count > 0) {
			var tickets = updateTicketPurchase(purchase, ticketing, count);
			return Pair.of(purchase, tickets);
		}
		return Pair.of(purchase, Collections.emptyList());
	}

	private List<Ticket> updateTicketPurchase(Purchase purchase, Ticketing ticketing, int count) {
		var tickets = this.ticketRepository.findByTicketingIdAndPurchaseIsNullOrderById(ticketing.getId(),
			Limit.of(count));
		if (tickets.size() < count) {
			throw new NotEnoughTicketException();
		}
		tickets.forEach(ticket -> {
			ticket.setPurchase(purchase);
			this.ticketRepository.save(ticket);
		});
		return tickets;
	}
}
