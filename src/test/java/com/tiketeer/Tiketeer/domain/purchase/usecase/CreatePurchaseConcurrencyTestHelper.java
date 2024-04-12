package com.tiketeer.Tiketeer.domain.purchase.usecase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;
import com.tiketeer.Tiketeer.domain.ticketing.service.TicketingStockService;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@TestComponent
public class CreatePurchaseConcurrencyTestHelper {
	@Autowired
	private TicketingRepository ticketingRepository;
	@Autowired
	private TicketingStockService ticketingStockService;
	@Autowired
	private TestHelper testHelper;

	interface C {
		public void createPurchase(String buyerEmail);
	}

	public void makeConcurrency(int threadNums, List<Member> buyers, Ticketing ticketing, C c
	) throws
		InterruptedException {
		ExecutorService executorService = Executors.newFixedThreadPool(threadNums);
		CountDownLatch startLatch = new CountDownLatch(1);
		CountDownLatch endLatch = new CountDownLatch(threadNums);
		//when
		for (int i = 0; i < threadNums; i++) {
			int buyerIdx = i;
			executorService.submit(() -> {
				try {
					startLatch.await();
					c.createPurchase(buyers.get(buyerIdx).getEmail());
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				} finally {
					endLatch.countDown();
				}
			});
		}
		startLatch.countDown();
		endLatch.await();
	}

	public Ticketing createTicketing(Member member, int stock) {
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

	public List<Member> createBuyers(int buyerNum) {
		List<Member> buyers = new ArrayList<>();
		for (int i = 0; i < buyerNum; i++) {
			var memberEmail = "buyer" + i + "@test.com";
			Member buyer = testHelper.createMember(memberEmail, 10000);
			buyers.add(buyer);
		}
		return buyers;
	}
}
