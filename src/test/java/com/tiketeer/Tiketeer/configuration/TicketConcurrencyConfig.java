package com.tiketeer.Tiketeer.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.tiketeer.Tiketeer.domain.member.service.MemberCrudService;
import com.tiketeer.Tiketeer.domain.member.service.MemberPointService;
import com.tiketeer.Tiketeer.domain.purchase.repository.PurchaseRepository;
import com.tiketeer.Tiketeer.domain.purchase.service.PurchaseCrudService;
import com.tiketeer.Tiketeer.domain.purchase.usecase.CreatePurchaseUseCaseImpl;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticket.service.concurrency.TicketConcurrencyService;
import com.tiketeer.Tiketeer.domain.ticket.service.concurrency.TicketNonConcurrencyService;
import com.tiketeer.Tiketeer.domain.ticketing.service.TicketingService;

@Configuration
@Profile("test")
public class TicketConcurrencyConfig {
	@Bean
	public TicketConcurrencyService ticketConcurrencyService(
		TicketRepository ticketRepository,
		PurchaseCrudService purchaseCrudService) {
		return new TicketNonConcurrencyService(ticketRepository, purchaseCrudService);
	}

	@Bean
	public CreatePurchaseUseCaseImpl createPurchaseUseCase(PurchaseRepository purchaseRepository,
		TicketingService ticketingService,
		MemberPointService memberPointService,
		MemberCrudService memberCrudService,
		TicketConcurrencyService ticketConcurrencyService) {
		return new CreatePurchaseUseCaseImpl(purchaseRepository, ticketingService,
			memberPointService, memberCrudService, ticketConcurrencyService);
	}
}
