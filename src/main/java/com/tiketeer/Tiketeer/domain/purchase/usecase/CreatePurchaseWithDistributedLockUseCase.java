package com.tiketeer.Tiketeer.domain.purchase.usecase;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.tiketeer.Tiketeer.domain.member.service.MemberCrudService;
import com.tiketeer.Tiketeer.domain.member.service.MemberPointService;
import com.tiketeer.Tiketeer.domain.purchase.Purchase;
import com.tiketeer.Tiketeer.domain.purchase.repository.PurchaseRepository;
import com.tiketeer.Tiketeer.domain.purchase.usecase.dto.CreatePurchaseCommandDto;
import com.tiketeer.Tiketeer.domain.purchase.usecase.dto.CreatePurchaseResultDto;
import com.tiketeer.Tiketeer.domain.ticket.exception.TicketConcurrencyException;
import com.tiketeer.Tiketeer.domain.ticket.service.concurrency.TicketConcurrencyService;
import com.tiketeer.Tiketeer.domain.ticketing.service.TicketingService;

public class CreatePurchaseWithDistributedLockUseCase extends CreatePurchaseUseCase {
	private final RedissonClient redissonClient;
	private final long waitTime = 10L;
	private final long leaseTime = 3L;

	@Autowired
	public CreatePurchaseWithDistributedLockUseCase(
		PurchaseRepository purchaseRepository,
		TicketingService ticketingService,
		MemberPointService memberPointService,
		MemberCrudService memberCrudService,
		TicketConcurrencyService ticketConcurrencyService,
		RedissonClient redissonClient
	) {
		super(purchaseRepository, ticketingService, memberPointService, memberCrudService,
			ticketConcurrencyService);
		this.redissonClient = redissonClient;
	}

	@Override
	@Transactional
	public CreatePurchaseResultDto createPurchase(CreatePurchaseCommandDto command) {
		String lockName = command.getTicketingId().toString();
		RLock rLock = redissonClient.getLock(lockName);

		try {
			boolean available = rLock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);

			if (!available) {
				throw new TicketConcurrencyException();
			}
			var ticketingId = command.getTicketingId();
			var count = command.getCount();

			var member = memberCrudService.findByEmail(command.getMemberEmail());

			var ticketing = ticketingService.findById(ticketingId);

			memberPointService.subtractPoint(member.getId(), ticketing.getPrice() * count);

			var newPurchase = purchaseRepository.save(Purchase.builder().member(member).build());

			ticketConcurrencyService.assignPurchaseToTicket(ticketingId, newPurchase.getId(), count);

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
}
