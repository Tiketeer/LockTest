package com.tiketeer.Tiketeer.domain.purchase.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiketeer.Tiketeer.domain.purchase.PurchaseTestHelper;
import com.tiketeer.Tiketeer.domain.purchase.controller.dto.PostPurchasePLockRequestDto;
import com.tiketeer.Tiketeer.domain.purchase.controller.dto.PostPurchaseResponseDto;
import com.tiketeer.Tiketeer.domain.purchase.repository.PurchaseRepository;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.TicketingTestHelper;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@Import({TestHelper.class, PurchaseTestHelper.class, TicketingTestHelper.class})
@SpringBootTest
@AutoConfigureMockMvc
public class PurchaseControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private TicketingTestHelper ticketingTestHelper;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private TestHelper testHelper;
	@Autowired
	private PurchaseRepository purchaseRepository;
	@Autowired
	private TicketRepository ticketRepository;

	@BeforeEach
	void initDB() {
		testHelper.initDB();
	}

	@AfterEach
	void cleanDB() {
		testHelper.cleanDB();
	}

	@Test
	@DisplayName("정상 컨디션 > 비관적 락 구매 생성 요청 > 성공")
	void postPurchaseWithPLockSuccess() throws Exception {
		// given
		var email = "test@test.com";
		var seller = testHelper.createMember(email);
		var ticketing = ticketingTestHelper.createTicketing(seller.getId(), 0, 1000, 5);

		var buyerEmail = "test2@test.com";
		var buyer = testHelper.createMember(buyerEmail, 100000);
		var buyCnt = 2;
		var req = PostPurchasePLockRequestDto.builder()
			.ticketingId(ticketing.getId())
			.email(buyerEmail)
			.count(buyCnt)
			.build();

		// when
		mockMvc.perform(
				post("/api/purchases/p-lock")
					.contextPath("/api")
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8)
					.content(objectMapper.writeValueAsString(req))
			).andExpect(status().isCreated())
			.andDo(response -> {
				var result = testHelper.getDeserializedApiResponse(response.getResponse().getContentAsString(),
					PostPurchaseResponseDto.class).getData();

				// then
				Assertions.assertThat(result.getPurchaseId()).isNotNull();
				var purchase = purchaseRepository.findById(result.getPurchaseId());
				Assertions.assertThat(purchase.isPresent()).isTrue();
				Assertions.assertThat(ticketRepository.findAllByPurchase(purchase.get()).size()).isEqualTo(buyCnt);
			});
	}

}
