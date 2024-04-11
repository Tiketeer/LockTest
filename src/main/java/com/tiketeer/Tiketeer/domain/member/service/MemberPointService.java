package com.tiketeer.Tiketeer.domain.member.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.exception.MemberNotFoundException;
import com.tiketeer.Tiketeer.domain.member.exception.NotEnoughPointException;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;

@Service
public class MemberPointService {
	private final MemberRepository memberRepository;

	@Autowired
	public MemberPointService(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@Transactional
	public void subtractPoint(UUID memberId, long amount) {
		var member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
		if (member.getPoint() < amount) {
			throw new NotEnoughPointException();
		}
		member.setPoint(member.getPoint() - amount);
	}

	@Transactional
	public void addPoint(UUID memberId, long amount) {
		var member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
		member.setPoint(member.getPoint() + amount);
	}
}
