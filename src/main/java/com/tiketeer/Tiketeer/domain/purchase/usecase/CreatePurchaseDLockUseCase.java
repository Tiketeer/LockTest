package com.tiketeer.Tiketeer.domain.purchase.usecase;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.tiketeer.Tiketeer.domain.member.service.MemberCrudService;
import com.tiketeer.Tiketeer.domain.member.service.MemberPointService;
import com.tiketeer.Tiketeer.domain.purchase.Purchase;
import com.tiketeer.Tiketeer.domain.purchase.exception.NotEnoughTicketException;
import com.tiketeer.Tiketeer.domain.purchase.repository.PurchaseRepository;
import com.tiketeer.Tiketeer.domain.purchase.service.PurchaseCrudService;
import com.tiketeer.Tiketeer.domain.purchase.usecase.dto.CreatePurchaseDLockCommandDto;
import com.tiketeer.Tiketeer.domain.purchase.usecase.dto.CreatePurchaseResultDto;
import com.tiketeer.Tiketeer.domain.ticket.exception.TicketConcurrencyException;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.service.TicketingService;

@Service
public class CreatePurchaseDLockUseCase {
	private final PurchaseRepository purchaseRepository;
	private final TicketingService ticketingService;
	private final MemberPointService memberPointService;
	private final MemberCrudService memberCrudService;
	private final TicketRepository ticketRepository;
	private final PurchaseCrudService purchaseCrudService;
	private final RedissonClient redissonClient;

	@Autowired
	public CreatePurchaseDLockUseCase(
		PurchaseRepository purchaseRepository,
		TicketingService ticketingService,
		MemberPointService memberPointService,
		MemberCrudService memberCrudService,
		TicketRepository ticketRepository,
		PurchaseCrudService purchaseCrudService,
		RedissonClient redissonClient
	) {
		this.purchaseRepository = purchaseRepository;
		this.ticketingService = ticketingService;
		this.memberPointService = memberPointService;
		this.memberCrudService = memberCrudService;
		this.ticketRepository = ticketRepository;
		this.purchaseCrudService = purchaseCrudService;
		this.redissonClient = redissonClient;
	}

	@Transactional
	public CreatePurchaseResultDto createPurchase(CreatePurchaseDLockCommandDto command) {
		String lockName = command.getTicketingId().toString();
		RLock rLock = redissonClient.getLock(lockName);

		try {
			boolean available = rLock.tryLock(command.getWaitTime(), command.getLeaseTime(), TimeUnit.SECONDS);

			if (!available) {
				throw new TicketConcurrencyException();
			}
			var ticketingId = command.getTicketingId();
			var count = command.getCount();

			var member = memberCrudService.findByEmail(command.getMemberEmail());

			var ticketing = ticketingService.findById(ticketingId);

			memberPointService.subtractPoint(member.getId(), ticketing.getPrice() * count);

			var newPurchase = purchaseRepository.save(Purchase.builder().member(member).build());

			assignPurchaseToTicket(ticketingId, newPurchase.getId(), count);

			return CreatePurchaseResultDto.builder()
				.purchaseId(newPurchase.getId())
				.createdAt(newPurchase.getCreatedAt())
				.build();
		} catch (InterruptedException e) {
			throw new TicketConcurrencyException();
		} finally {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				public void afterCompletion(int status) {
					rLock.unlock();
				}
			});
		}
	}

	private void assignPurchaseToTicket(UUID ticketingId, UUID purchaseId, int ticketCount) {
		var purchase = purchaseCrudService.findById(purchaseId);
		var tickets = ticketRepository.findByTicketingIdAndPurchaseIsNullOrderById(
			ticketingId, Limit.of(ticketCount));

		if (tickets.size() < ticketCount) {
			throw new NotEnoughTicketException();
		}

		tickets.forEach(ticket -> {
			ticket.setPurchase(purchase);
		});
	}
}