package com.tiketeer.Tiketeer.domain.purchase.usecase;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tiketeer.Tiketeer.domain.purchase.usecase.dto.CreatePurchaseDLockCommandDto;
import com.tiketeer.Tiketeer.domain.purchase.usecase.dto.CreatePurchaseResultDto;
import com.tiketeer.Tiketeer.domain.ticket.exception.TicketConcurrencyException;

@Service
public class CreatePurchaseDLockUseCase {
	private final RedissonClient redissonClient;
	private final CreatePurchaseUseCaseCore innerUseCase;

	@Autowired
	public CreatePurchaseDLockUseCase(
		RedissonClient redissonClient,
		CreatePurchaseUseCaseCore createPurchaseUseCaseCore
	) {
		this.redissonClient = redissonClient;
		this.innerUseCase = createPurchaseUseCaseCore;
	}

	public CreatePurchaseResultDto createPurchase(CreatePurchaseDLockCommandDto command) {
		String lockName = command.getTicketingId().toString();
		RLock rLock = redissonClient.getLock(lockName);

		try {
			boolean available = rLock.tryLock(command.getWaitTime(), command.getLeaseTime(), TimeUnit.SECONDS);

			if (!available) {
				throw new TicketConcurrencyException();
			}

			return innerUseCase.createPurchase(command);
		} catch (InterruptedException e) {
			throw new TicketConcurrencyException();
		} finally {
			rLock.unlock();
		}
	}
}
