package com.tiketeer.Tiketeer.domain.purchase.service;

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

import com.tiketeer.Tiketeer.domain.purchase.Purchase;
import com.tiketeer.Tiketeer.domain.purchase.exception.PurchaseNotFoundException;
import com.tiketeer.Tiketeer.domain.purchase.repository.PurchaseRepository;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@Import({TestHelper.class})
@SpringBootTest
public class PurchaseCrudServiceTest {
	@Autowired
	private TestHelper testHelper;
	@Autowired
	private PurchaseCrudService purchaseCrudService;
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
	@DisplayName("존재하지 않는 구매 내역 > 조회 요청 > 실패")
	void findByIdFailBecauseNotFound() {
		// given
		var invalidId = UUID.randomUUID();

		Assertions.assertThatThrownBy(() -> {
			// when
			purchaseCrudService.findById(invalidId);
			// then
		}).isInstanceOf(PurchaseNotFoundException.class);
	}

	@Test
	@DisplayName("존재하는 구매 내역 > 조회 요청 > 성공")
	@Transactional
	void findByIdSuccess() {
		// given
		var email = "test@test.com";
		var member = testHelper.createMember(email);
		var purchase = purchaseRepository.save(Purchase.builder().member(member).build());

		// when
		var purchaseInDB = purchaseCrudService.findById(purchase.getId());

		// then
		Assertions.assertThat(purchaseInDB).isNotNull();
	}
}
