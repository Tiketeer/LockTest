package com.tiketeer.Tiketeer.domain.member.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.Member;

@DataJpaTest
class MemberRepositoryTest {
	@Autowired
	private MemberRepository memberRepository;

	@Test
	@DisplayName("멤버 저장 > 멤버 조회 > 저장한 값과 조회된 값 비교")
	void findByEmailSuccess() {
		Member saved = memberRepository.save(
			new Member("test@gmail.com", 0L));

		Optional<Member> optionalMember = memberRepository.findByEmail(saved.getEmail());

		assertThat(optionalMember.get().getId()).isEqualTo(saved.getId());
	}

	@Test
	@DisplayName("5개의 멤버 존재 > 전체 소프트 삭제 요청 > 성공")
	@Transactional
	void softDeleteAllInBatchSuccess() {
		// given
		var memberCnt = 10;
		for (int idx = 0; idx < memberCnt; idx++) {
			memberRepository.save(Member.builder()
				.email("test" + idx + "@test.com")
				.point(0L)
				.build());
		}
		Assertions.assertThat(memberRepository.findAll().size()).isEqualTo(memberCnt);

		// when
		var deletedCnt = memberRepository.softDeleteAllInBatch();

		// then
		Assertions.assertThat(deletedCnt).isEqualTo(memberCnt);
		Assertions.assertThat(memberRepository.findAll().size()).isEqualTo(0);
	}
}