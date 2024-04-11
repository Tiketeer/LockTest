package com.tiketeer.Tiketeer.domain.ticket.service.concurrency;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.purchase.Purchase;
import com.tiketeer.Tiketeer.domain.purchase.exception.NotEnoughTicketException;
import com.tiketeer.Tiketeer.domain.purchase.repository.PurchaseRepository;
import com.tiketeer.Tiketeer.domain.purchase.service.PurchaseCrudService;
import com.tiketeer.Tiketeer.domain.ticket.Ticket;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;
import com.tiketeer.Tiketeer.domain.ticketing.service.TicketingStockService;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@SpringBootTest
@Import({TestHelper.class})
class TicketConcurrencyWithPessimisticLockServiceTest {
	@Autowired
	private TicketPessimisticLockConcurrencyService service;

	@Autowired
	private TestHelper testHelper;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private TicketingRepository ticketingRepository;

	@Autowired
	private TicketingStockService ticketingStockService;

	@Autowired
	private PurchaseRepository purchaseRepository;

	@Autowired
	private TicketRepository ticketRepository;

	@BeforeEach
	void init() {
		testHelper.initDB();
	}

	@AfterEach
	void clear() {
		testHelper.cleanDB();
	}

	@Test
	@DisplayName("티켓 10개 생성 > 티켓 10개 구매 > 정상 구매 확인")
	@Transactional
	void assignPurchaseToTicketSuccess() {
		// given
		Member seller = testHelper.createMember("seller@test.com");
		Member buyer = memberRepository.findByEmail("buyer@test.com").orElseThrow();

		Ticketing tickeing = createTickeing(seller);

		int stock = 10;
		ticketingStockService.createStock(tickeing.getId(), stock);

		Purchase purchase = purchaseRepository.save(new Purchase(buyer));

		// when
		service.assignPurchaseToTicket(tickeing.getId(), purchase.getId(), 10);

		// then
		List<Ticket> tickets = ticketRepository.findByTicketingIdAndPurchaseIsNull(
			tickeing.getId());
		assertThat(tickets.size()).isEqualTo(0);
	}

	@Test
	@DisplayName("티켓 1개 생성 > 티켓 10개 구매 > 예외 발생 확인")
	@Transactional
	void assignPurchaseToTicketFailByNotEnoughTicket() {
		Member seller = testHelper.createMember("seller@test.com");
		Member buyer = memberRepository.findByEmail("buyer@test.com").orElseThrow();

		Ticketing tickeing = createTickeing(seller);

		int stock = 1;
		ticketingStockService.createStock(tickeing.getId(), stock);

		Purchase purchase = purchaseRepository.save(new Purchase(buyer));

		Assertions.assertThatThrownBy(() -> service.assignPurchaseToTicket(tickeing.getId(), purchase.getId(), 10))
			.isInstanceOf(NotEnoughTicketException.class);

	}

	private Ticketing createTickeing(Member seller) {
		Ticketing ticketing = new Ticketing(10000, seller, "", "asdf", "asdfasdf", LocalDateTime.now().plusMonths(20),
			"", 500,
			LocalDateTime.now(),
			LocalDateTime.now().plusMonths(10));

		return ticketingRepository.save(ticketing);
	}

	@TestConfiguration
	static class Config {
		@Bean
		public TicketPessimisticLockConcurrencyService ticketNonConcurrencyService(
			TicketRepository ticketRepository,
			PurchaseCrudService purchaseCrudService) {
			return new TicketPessimisticLockConcurrencyService(ticketRepository, purchaseCrudService);
		}
	}
}