package com.tiketeer.Tiketeer.domain.purchase.usecase;

import static org.assertj.core.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.purchase.service.PurchaseCrudService;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticket.service.concurrency.TicketConcurrencyService;
import com.tiketeer.Tiketeer.domain.ticket.service.concurrency.TicketConcurrencyWithPessimisticLockService;
import com.tiketeer.Tiketeer.testhelper.TestHelper;
import com.tiketeer.Tiketeer.testhelper.Transaction;

@Import({TestHelper.class, CreatePurchaseConcurrencyTest.class, Transaction.class})
@SpringBootTest
class CreatePurchaseUseCasePessimisticLockConcurrencyTest {

	@Autowired
	private TestHelper testHelper;
	@Autowired
	private TicketRepository ticketRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private CreatePurchaseConcurrencyTest createPurchaseConcurrencyTest;
	@Autowired
	private Transaction transaction;

	@BeforeEach
	void initTable() {
		testHelper.initDB();
	}

	@AfterEach
	void cleanTable() {
		testHelper.cleanDB();
	}

	@Test
	@DisplayName("20개의 티켓 생성 > 40명의 구매자가 경쟁 > 20명 구매 성공, 20명 구매 실패")
	void createPurchaseWithConcurrency() throws InterruptedException {
		//given
		var ticketStock = 20;
		var seller = testHelper.createMember("seller@etest.com");
		var ticketing = createPurchaseConcurrencyTest.createTicketing(seller, ticketStock);

		int threadNums = 40;
		var buyers = createPurchaseConcurrencyTest.createBuyers(threadNums);

		createPurchaseConcurrencyTest.makeConcurrency(threadNums, buyers, ticketing);

		//then
		transaction.invoke(() -> {
			var tickets = ticketRepository.findAllByPurchase(null);
			assertThat(tickets.size()).isEqualTo(0);

			var allMembers = memberRepository.findAll();

			//assert all ticket owners are unique
			var purchasedTickets = ticketRepository.findAllByPurchaseIsNotNull();
			assertThat(purchasedTickets.size()).isEqualTo(ticketStock);

			var ticketOwnerIdList = purchasedTickets
				.stream()
				.map(ticket -> ticket.getPurchase().getMember().getId()).toList();

			Set<UUID> ticketOwnerIdSet = new HashSet<>(ticketOwnerIdList);
			assertThat(ticketOwnerIdSet.size()).isEqualTo(ticketOwnerIdList.size());

			//assert one purchase per member
			var ticketingSuccessMembers = allMembers.stream()
				.filter(member -> member.getPurchases().size() == 1)
				.toList();

			assertThat(ticketingSuccessMembers.size()).isEqualTo(ticketStock);
			return null;
		});
	}

	@TestConfiguration
	static class TestConfig {
		@Bean
		public TicketConcurrencyService ticketConcurrencyService(
			TicketRepository ticketRepository,
			PurchaseCrudService purchaseCrudService) {
			return new TicketConcurrencyWithPessimisticLockService(ticketRepository, purchaseCrudService);
		}
	}
}