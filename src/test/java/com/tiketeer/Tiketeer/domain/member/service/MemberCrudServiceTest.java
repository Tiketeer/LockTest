package com.tiketeer.Tiketeer.domain.member.service;

import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.tiketeer.Tiketeer.domain.member.exception.MemberNotFoundException;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@Import({TestHelper.class})
@SpringBootTest
public class MemberCrudServiceTest {
	@Autowired
	private TestHelper testHelper;
	@Autowired
	private MemberCrudService memberCrudService;

	@BeforeEach
	void initTable() {
		testHelper.initDB();
	}

	@AfterEach
	void cleanTable() {
		testHelper.cleanDB();
	}

	@Test
	@DisplayName("존재하지 않는 멤버 ID > 조회 요청 > 실패")
	void findByIdFailBecauseNotExistMember() {
		// given
		var invalidId = UUID.randomUUID();

		Assertions.assertThatThrownBy(() -> {
			// when
			memberCrudService.findById(invalidId);
			// then
		}).isInstanceOf(MemberNotFoundException.class);
	}

	@Test
	@DisplayName("존재하는 멤버 > 조회 요청 > 성공")
	void findByIdSuccess() {
		// given
		var email = "test@test.com";
		var member = testHelper.createMember(email);

		// when
		var memberInDB = memberCrudService.findById(member.getId());

		// then
		Assertions.assertThat(memberInDB).isNotNull();
		Assertions.assertThat(memberInDB.getEmail()).isEqualTo(email);
	}

	@Test
	@DisplayName("존재하지 않는 멤버 이메일 > 조회 요청 > 실패")
	void findByIdFailBecauseNotExistMemberEmail() {
		// given
		var invalidEmail = "testtest@test.com";

		Assertions.assertThatThrownBy(() -> {
			// when
			memberCrudService.findByEmail(invalidEmail);
			// then
		}).isInstanceOf(MemberNotFoundException.class);
	}

	@Test
	@DisplayName("존재하는 멤버 이메일 > 조회 요청 > 성공")
	void findByEmailSuccess() {
		// given
		var email = "test@test.com";
		var member = testHelper.createMember(email);

		// when
		var memberInDB = memberCrudService.findByEmail(member.getEmail());

		// then
		Assertions.assertThat(memberInDB).isNotNull();
		Assertions.assertThat(memberInDB.getId()).isEqualTo(member.getId());
	}
}
