package com.tiketeer.Tiketeer.domain.purchase.usecase.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class CreatePurchaseCommandDto {
	private final String memberEmail;
	private final UUID ticketingId;
	private final Integer count;
	private final LocalDateTime commandCreatedAt;
}
