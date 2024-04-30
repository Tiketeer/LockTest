package com.tiketeer.Tiketeer.domain.purchase.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.backoff.UniformRandomBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import com.tiketeer.Tiketeer.domain.purchase.usecase.dto.CreatePurchaseOLockCommandDto;
import com.tiketeer.Tiketeer.domain.purchase.usecase.dto.CreatePurchaseResultDto;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;

@Service
public class CreatePurchaseOLockUseCase {
	private final TicketRepository ticketRepository;
	private final CreatePurchaseUseCaseCore innerUseCase;

	@Autowired
	public CreatePurchaseOLockUseCase(
		TicketRepository ticketRepository,
		CreatePurchaseUseCaseCore createPurchaseUseCaseCore
	) {
		this.ticketRepository = ticketRepository;
		this.innerUseCase = createPurchaseUseCaseCore;
	}

	public CreatePurchaseResultDto createPurchase(CreatePurchaseOLockCommandDto command) {
		var minBackoff = command.getMinBackoff();
		var maxBackoff = command.getMaxBackoff();
		var maxAttempts = command.getMaxAttempts();

		UniformRandomBackOffPolicy backoffPolicy = new UniformRandomBackOffPolicy();
		backoffPolicy.setMinBackOffPeriod(minBackoff);
		backoffPolicy.setMaxBackOffPeriod(maxBackoff);

		return RetryTemplate.builder()
			.maxAttempts(maxAttempts)
			.customBackoff(backoffPolicy)
			.retryOn(OptimisticLockingFailureException.class)
			.build()
			.execute(context -> innerUseCase.createPurchase(command,
				ticketRepository::findByTicketingIdAndPurchaseIsNullOrderByIdWithOptimisticLock));
	}

}
