package com.tiketeer.Tiketeer.domain.member.exception;

import com.tiketeer.Tiketeer.exception.DefinedException;
import com.tiketeer.Tiketeer.exception.code.MemberExceptionCode;

public class NotEnoughPointException extends DefinedException {
	public NotEnoughPointException() {
		super(MemberExceptionCode.NOT_ENOUGH_POINT);
	}
}
