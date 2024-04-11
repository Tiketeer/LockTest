package com.tiketeer.Tiketeer.domain.ticketing.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.exception.MemberNotFoundException;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;
import com.tiketeer.Tiketeer.domain.ticketing.service.TicketingStockService;
import com.tiketeer.Tiketeer.domain.ticketing.usecase.dto.CreateTicketingCommandDto;
import com.tiketeer.Tiketeer.domain.ticketing.usecase.dto.CreateTicketingResultDto;

@Service
public class CreateTicketingUseCase {
	private final TicketingRepository ticketingRepository;
	private final TicketingStockService ticketingStockService;
	private final MemberRepository memberRepository;

	@Autowired
	public CreateTicketingUseCase(TicketingRepository ticketingRepository, TicketingStockService ticketingStockService,
		MemberRepository memberRepository) {
		this.ticketingRepository = ticketingRepository;
		this.ticketingStockService = ticketingStockService;
		this.memberRepository = memberRepository;
	}

	@Transactional
	public CreateTicketingResultDto createTicketing(CreateTicketingCommandDto command) {
		var member = memberRepository.findByEmail(command.getMemberEmail())
			.orElseThrow(MemberNotFoundException::new);

		var ticketing = ticketingRepository.save((
			Ticketing.builder()
				.member(member)
				.title(command.getTitle())
				.description(command.getDescription())
				.location(
					command.getLocation())
				.category(command.getCategory())
				.runningMinutes(command.getRunningMinutes())
				.price(command.getPrice())
				.eventTime(command.getEventTime())
				.saleStart(command.getSaleStart())
				.saleEnd(command.getSaleEnd())
				.build()));

		ticketingStockService.createStock(ticketing.getId(), command.getStock());

		return CreateTicketingResultDto.builder()
			.ticketingId(ticketing.getId())
			.createdAt(ticketing.getCreatedAt())
			.build();
	}

}
