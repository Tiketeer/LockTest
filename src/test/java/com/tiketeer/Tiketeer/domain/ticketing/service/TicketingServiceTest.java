package com.tiketeer.Tiketeer.domain.ticketing.service;

import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.tiketeer.Tiketeer.domain.purchase.repository.PurchaseRepository;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.exception.TicketingNotFoundException;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@Import({TestHelper.class})
@SpringBootTest
@DisplayName("TicketingService Test")
public class TicketingServiceTest {
	@Autowired
	private TestHelper testHelper;
	@Autowired
	private TicketingService ticketingService;
	@Autowired
	private TicketingRepository ticketingRepository;
	@Autowired
	private TicketRepository ticketRepository;
	@Autowired
	private PurchaseRepository purchaseRepository;

	@BeforeEach
	void initTable() {
		testHelper.initDB();
	}

	@AfterEach
	void cleanTable() {
		testHelper.cleanDB();
	}

	@Test
	@DisplayName("존재하지 않는 티케팅 > 조회 요청 > 실패")
	void findByIdFailBecauseNotExistTicketing() {
		// given
		var invalidTicketingId = UUID.randomUUID();

		Assertions.assertThatThrownBy(() -> {
			// when
			ticketingService.findById(invalidTicketingId);
			// then
		}).isInstanceOf(TicketingNotFoundException.class);
	}

}
