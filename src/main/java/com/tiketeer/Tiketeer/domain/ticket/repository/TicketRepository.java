package com.tiketeer.Tiketeer.domain.ticket.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.purchase.Purchase;
import com.tiketeer.Tiketeer.domain.ticket.Ticket;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;

import jakarta.persistence.LockModeType;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {
	List<Ticket> findAllByTicketing(Ticketing ticketing);

	List<Ticket> findAllByPurchase(Purchase purchase);

	List<Ticket> findByTicketingIdAndPurchaseIsNull(UUID ticketingId);

	List<Ticket> findAllByPurchaseIsNotNull();

	List<Ticket> findByTicketingIdAndPurchaseIsNullOrderById(UUID ticketingId, Limit limit);

	@Lock(LockModeType.OPTIMISTIC)
	@Query("SELECT t FROM Ticket t WHERE t.ticketing.id = :ticketingId AND t.purchase IS NULL ORDER BY t.id")
	List<Ticket> findByTicketingIdAndPurchaseIsNullOrderByIdWithOptimisticLock(UUID ticketingId, Limit limit);

	@Transactional(readOnly = true)
	@Lock(LockModeType.OPTIMISTIC)
	@Query("SELECT t FROM Ticket t WHERE t.ticketing.id = :ticketingId AND t.purchase IS NULL ORDER BY FUNCTION('RAND')")
	List<Ticket> findByTicketingIdAndPurchaseIsNullOrderByRandWithOptimisticLock(UUID ticketingId, Limit limit);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT t FROM Ticket t WHERE t.ticketing.id = :ticketingId AND t.purchase IS NULL ORDER BY t.id")
	List<Ticket> findByTicketingIdAndPurchaseIsNullOrderByIdWithPessimisticLock(UUID ticketingId, Limit limit);
}
