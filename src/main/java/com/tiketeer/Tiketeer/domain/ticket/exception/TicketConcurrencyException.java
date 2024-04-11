package com.tiketeer.Tiketeer.domain.ticket.exception;

import com.tiketeer.Tiketeer.exception.DefinedException;
import com.tiketeer.Tiketeer.exception.code.TicketExceptionCode;

public class TicketConcurrencyException extends DefinedException {
	public TicketConcurrencyException() {
		super(TicketExceptionCode.TICKET_CONCURRENCY);
	}
}
