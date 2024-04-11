package com.tiketeer.Tiketeer.domain.member.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.exception.MemberNotFoundException;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;

@Service
@Transactional(readOnly = true)
public class MemberCrudService {
	private final MemberRepository memberRepository;

	@Autowired
	public MemberCrudService(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	public Member findById(UUID memberId) {
		return memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
	}

	public Member findByEmail(String email) {
		return memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
	}
}
