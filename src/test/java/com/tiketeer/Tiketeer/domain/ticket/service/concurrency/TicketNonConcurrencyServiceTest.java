package com.tiketeer.Tiketeer.domain.ticket.service.concurrency;

import java.time.LocalDateTime;

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
import com.tiketeer.Tiketeer.domain.purchase.Purchase;
import com.tiketeer.Tiketeer.domain.purchase.exception.NotEnoughTicketException;
import com.tiketeer.Tiketeer.domain.purchase.repository.PurchaseRepository;
import com.tiketeer.Tiketeer.domain.purchase.service.PurchaseCrudService;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;
import com.tiketeer.Tiketeer.domain.ticketing.service.TicketingStockService;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@Import({TestHelper.class})
@SpringBootTest
public class TicketNonConcurrencyServiceTest {
	@Autowired
	private TestHelper testHelper;
	@Autowired
	private TicketNonConcurrencyService ticketNonConcurrencyService;
	@Autowired
	private TicketingRepository ticketingRepository;
	@Autowired
	private TicketingStockService ticketingStockService;
	@Autowired
	private PurchaseRepository purchaseRepository;
	@Autowired
	private TicketRepository ticketRepository;

	@BeforeEach
	void initTable() {
		testHelper.initDB();
	}

	@AfterEach
	void cleanTable() {
		testHelper.cleanDB();
	}

	@Test
	@DisplayName("구매 수량보다 적은 티켓의 수 > 티켓 배정 요청 > 실패")
	@Transactional
	void assignPurchaseToTicketFailBecauseNotEnoughTicket() {
		// given
		var sellerEmail = "test@test.com";
		var seller = testHelper.createMember(sellerEmail);
		var stock = 5;
		var ticketing = createTicketing(seller, stock);
		var purchase = purchaseRepository.save(Purchase.builder().member(seller).build());
		var purchaseTicketCount = 10;

		Assertions.assertThatThrownBy(() -> {
			// when
			ticketNonConcurrencyService.assignPurchaseToTicket(ticketing.getId(), purchase.getId(),
				purchaseTicketCount);
			// then
		}).isInstanceOf(NotEnoughTicketException.class);
	}

	@Test
	@DisplayName("충분한 티켓의 수 > 티켓 배정 요청 > 성공")
	@Transactional
	void assignPurchaseToTicketSuccess() {
		// given
		var sellerEmail = "seller@test.com";
		var seller = testHelper.createMember(sellerEmail);
		var stock = 10;
		var ticketing = createTicketing(seller, stock);

		var buyerEmail = "buyer@test.com";
		var buyer = testHelper.createMember(buyerEmail);
		buyer.setPoint(1000000);
		var purchase = purchaseRepository.save(Purchase.builder().member(buyer).build());
		var purchaseTicketCount = 5;

		// when
		ticketNonConcurrencyService.assignPurchaseToTicket(ticketing.getId(), purchase.getId(), purchaseTicketCount);

		// then
		Assertions.assertThat(ticketRepository.findAllByPurchase(purchase).size())
			.isEqualTo(purchaseTicketCount);
	}

	private Ticketing createTicketing(Member member, int stock) {
		var now = LocalDateTime.now();
		var ticketing = ticketingRepository.save(Ticketing.builder()
			.member(member)
			.title("제목")
			.price(10000)
			.location("서울")
			.category("바자회")
			.runningMinutes(100)
			.saleStart(now.minusMonths(6))
			.saleEnd(now.plusMonths(6))
			.eventTime(now.plusYears(1))
			.build());
		ticketingStockService.createStock(ticketing.getId(), stock);
		return ticketing;
	}

	@TestConfiguration
	static class Config {
		@Bean
		public TicketNonConcurrencyService ticketNonConcurrencyService(TicketRepository ticketRepository,
			PurchaseCrudService purchaseCrudService) {
			return new TicketNonConcurrencyService(ticketRepository, purchaseCrudService);
		}
	}
}
