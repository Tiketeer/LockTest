package com.tiketeer.Tiketeer.domain.purchase.usecase;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.exception.MemberNotFoundException;
import com.tiketeer.Tiketeer.domain.member.exception.NotEnoughPointException;
import com.tiketeer.Tiketeer.domain.purchase.exception.NotEnoughTicketException;
import com.tiketeer.Tiketeer.domain.purchase.usecase.dto.CreatePurchaseCommandDto;
import com.tiketeer.Tiketeer.domain.ticket.Ticket;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@Import({TestHelper.class})
@SpringBootTest
public class CreatePurchaseUseCaseTest {
	@Autowired
	private TestHelper testHelper;
	@Autowired
	private CreatePurchaseUseCase createPurchaseUseCase;
	@Autowired
	private TicketRepository ticketRepository;
	@Autowired
	private TicketingRepository ticketingRepository;

	@BeforeEach
	void initTable() {
		testHelper.initDB();
	}

	@AfterEach
	void cleanTable() {
		testHelper.cleanDB();
	}

	@Test
	@DisplayName("정상 조건 > 구매 생성 요청 > 성공")
	@Transactional
	void createPurchaseSuccess() {
		// given
		var mockEmail = "test1@test.com";
		var initPoint = 10000;
		var member = testHelper.createMember(mockEmail, "1234");
		member.setPoint(initPoint);

		var ticketing = createTicketing(member, 0, 3000, 5);

		var purchaseCount = 3;
		var createPurchaseCommand = CreatePurchaseCommandDto.builder()
			.memberEmail(mockEmail)
			.ticketingId(ticketing.getId())
			.count(purchaseCount)
			.build();

		// when
		var result = createPurchaseUseCase.createPurchase(createPurchaseCommand);

		// then
		var purchaseUnderMember = member.getPurchases().getFirst();
		Assertions.assertThat(purchaseUnderMember.getId()).isEqualTo(result.getPurchaseId());

		var tickets = ticketRepository.findAllByPurchase(purchaseUnderMember);
		Assertions.assertThat(tickets.size()).isEqualTo(purchaseCount);
	}

	@Test
	@DisplayName("존재하지 않는 멤버 > 구매 생성 요청 > 실패")
	@Transactional
	void createPurchaseFailMemberNotFound() {
		// given
		var mockEmail = "test1@test.com";

		var createPurchaseCommand = CreatePurchaseCommandDto.builder()
			.memberEmail(mockEmail)
			.ticketingId(UUID.randomUUID())
			.count(1)
			.build();

		Assertions.assertThatThrownBy(() -> {
			// when
			createPurchaseUseCase.createPurchase(createPurchaseCommand);
			// then
		}).isInstanceOf(MemberNotFoundException.class);
	}

	@Test
	@DisplayName("구매 가능한 티켓이 부족 > 구매 생성 요청 > 실패")
	@Transactional
	void createPurchaseFailNotEnoughTicket() {
		// given
		var sellerEmail = "test1@test.com";
		var seller = testHelper.createMember(sellerEmail);
		var ticketing = createTicketing(seller, 0, 1000, 3);

		var buyerEmail = "test2@test.com";
		var buyer = testHelper.createMember(buyerEmail);
		buyer.setPoint(10000);
		var createPurchaseCommand = CreatePurchaseCommandDto.builder()
			.memberEmail(buyerEmail)
			.ticketingId(ticketing.getId())
			.count(5)
			.build();

		Assertions.assertThatThrownBy(() -> {
			// when
			createPurchaseUseCase.createPurchase(createPurchaseCommand);
			// then
		}).isInstanceOf(NotEnoughTicketException.class);
	}

	@Test
	@DisplayName("구매하기 위한 금액이 부족 > 구매 요청 > 실패")
	void createPurchaseFailBecauseNotEnoughPoint() {
		// given
		var sellerEmail = "test@test.com";
		var seller = testHelper.createMember(sellerEmail);
		var ticketing = createTicketing(seller, 0, 3000, 5);

		var buyerEmail = "test2@test.com";
		var buyer = testHelper.createMember(buyerEmail);
		buyer.setPoint(5000);

		var createPurchaseCommand = CreatePurchaseCommandDto.builder()
			.memberEmail(buyerEmail)
			.ticketingId(ticketing.getId())
			.count(3)
			.build();

		Assertions.assertThatThrownBy(() -> {
			// when
			createPurchaseUseCase.createPurchase(createPurchaseCommand);
			// then
		}).isInstanceOf(NotEnoughPointException.class);
	}

	private Ticketing createTicketing(Member member, int saleStartAfterYears, long price, int stock) {
		var now = LocalDateTime.now();
		var eventTime = now.plusYears(saleStartAfterYears + 2);
		var saleStart = now.plusYears(saleStartAfterYears);
		var saleEnd = now.plusYears(saleStartAfterYears + 1);
		var ticketing = ticketingRepository.save(Ticketing.builder()
			.price(price)
			.title("test")
			.member(member)
			.description("")
			.location("Seoul")
			.eventTime(eventTime)
			.saleStart(saleStart)
			.saleEnd(saleEnd)
			.category("concert")
			.runningMinutes(300).build());
		ticketRepository.saveAll(Arrays.stream(new int[stock])
			.mapToObj(i -> Ticket.builder().ticketing(ticketing).build())
			.toList());
		return ticketing;
	}
}
