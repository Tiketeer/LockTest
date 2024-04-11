package com.tiketeer.Tiketeer.exception.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TicketExceptionCode implements ExceptionCode {
	TICKET_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 티켓입니다."),
	TICKET_CONCURRENCY(HttpStatus.CONFLICT, "티켓 조회에 실패했습니다.");
	
	private final HttpStatus httpStatus;
	private final String message;
}
