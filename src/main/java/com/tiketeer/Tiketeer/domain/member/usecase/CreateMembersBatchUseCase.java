package com.tiketeer.Tiketeer.domain.member.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.CreateMembersBatchCommandDto;

@Service
public class CreateMembersBatchUseCase {
	private final Long memberDefaultPoint = 1000000000L;
	private final MemberRepository memberRepository;

	public CreateMembersBatchUseCase(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@Transactional
	public void createMembersInBatch(CreateMembersBatchCommandDto command) {
		var emailList = command.getEmailList();

		var memberList = emailList.stream()
			.map((email)-> Member.builder()
				.email(email)
				.point(memberDefaultPoint)
				.build())
			.toList();

		memberRepository.saveAll(memberList);
	}
}
