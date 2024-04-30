package com.tiketeer.Tiketeer.domain.purchase.usecase.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CreatePurchasePLockCommandDto extends CreatePurchaseCommandDto {

	@Builder
	public CreatePurchasePLockCommandDto(String memberEmail, UUID ticketingId, Integer count,
		LocalDateTime commandCreatedAt) {
		super(memberEmail, ticketingId, count, commandCreatedAt);
	}
}
