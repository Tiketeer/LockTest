package com.tiketeer.Tiketeer.domain.member.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.exception.NotEnoughPointException;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@Import({TestHelper.class})
@SpringBootTest
public class MemberPointServiceTest {
	@Autowired
	private TestHelper testHelper;
	@Autowired
	private MemberPointService memberPointService;
	@Autowired
	private MemberRepository memberRepository;

	@BeforeEach
	void initTable() {
		testHelper.initDB();
	}

	@AfterEach
	void cleanTable() {
		testHelper.cleanDB();
	}

	@Test
	@DisplayName("기존 포인트 3000 > 포인트 5000 소비 요청 > 실패")
	@Transactional
	void subtractPointFailBecauseSubtractionMoreThanPoint() {
		// given
		var email = "test@test.com";
		var member = testHelper.createMember(email);
		member.setPoint(3000);

		Assertions.assertThatThrownBy(() -> {
			// when
			memberPointService.subtractPoint(member.getId(), 5000);
			// then
		}).isInstanceOf(NotEnoughPointException.class);
	}

	@Test
	@DisplayName("기존 포인트 5000 > 포인트 5000 소비 요청 > 성공")
	@Transactional
	void subtractPointSuccess() {
		// given
		var email = "test@test.com";
		var initPoint = 5000;
		var member = testHelper.createMember(email);
		member.setPoint(initPoint);

		// when
		memberPointService.subtractPoint(member.getId(), initPoint);

		// then
		var memberInDB = memberRepository.findByEmail(email).orElseThrow();
		Assertions.assertThat(memberInDB.getPoint()).isEqualTo(0);
	}

	@Test
	@DisplayName("기존 포인트 0 > 포인트 5000 추가 요청 > 성공")
	@Transactional
	void addPointSuccess() {
		// given
		var email = "test@test.com";
		var addPoint = 5000;
		var member = testHelper.createMember(email);
		member.setPoint(0);

		// when
		memberPointService.addPoint(member.getId(), addPoint);

		// then
		var memberInDB = memberRepository.findByEmail(email).orElseThrow();
		Assertions.assertThat(memberInDB.getPoint()).isEqualTo(addPoint);
	}
}
