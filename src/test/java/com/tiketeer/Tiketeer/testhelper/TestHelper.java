package com.tiketeer.Tiketeer.testhelper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.purchase.repository.PurchaseRepository;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;
import com.tiketeer.Tiketeer.response.ApiResponse;

@TestComponent
public class TestHelper {
	private final MemberRepository memberRepository;
	private final PurchaseRepository purchaseRepository;
	private final TicketRepository ticketRepository;
	private final TicketingRepository ticketingRepository;
	private final ObjectMapper objectMapper;

	@Autowired
	public TestHelper(
		MemberRepository memberRepository,
		PurchaseRepository purchaseRepository,
		TicketRepository ticketRepository,
		TicketingRepository ticketingRepository,
		ObjectMapper objectMapper
	) {
		this.memberRepository = memberRepository;
		this.purchaseRepository = purchaseRepository;
		this.ticketRepository = ticketRepository;
		this.ticketingRepository = ticketingRepository;
		this.objectMapper = objectMapper;
	}

	@Transactional
	public void initDB() {
	}

	@Transactional
	public void cleanDB() {
		List.of(
			ticketRepository,
			purchaseRepository,
			ticketingRepository,
			memberRepository
		).forEach(JpaRepository::deleteAllInBatch);
	}

	@Transactional
	public Member createMember(String email) {
		return createMember(email, "1q2w3e4r!!");
	}

	@Transactional
	public Member createMember(String email, String password) {
		return createMember(email, password, 0);
	}

	@Transactional
	public Member createMember(String email, long point) {
		return createMember(email, "1q2w3e4r!!", point);
	}

	@Transactional
	public Member createMember(String email, String password, long point) {
		return memberRepository.save(Member.builder()
			.email(email)
			.password(password)
			.point(point)
			.build());
	}

	public <T> ApiResponse<List<T>> getDeserializedListApiResponse(String json, Class<T> responseType) throws
		JsonProcessingException {
		return objectMapper.readValue(json, getListApiResponseType(responseType));
	}

	public <T> ApiResponse<T> getDeserializedApiResponse(String json, Class<T> responseType) throws
		JsonProcessingException {
		return objectMapper.readValue(json, getApiResponseType(responseType));
	}

	private JavaType getListApiResponseType(Class<?> clazz) {
		JavaType listType = getListType(clazz);
		return getApiResponseType(listType);
	}

	private JavaType getApiResponseType(Class<?> clazz) {
		return objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, clazz);
	}

	private JavaType getApiResponseType(JavaType javaType) {
		return objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, javaType);
	}

	private JavaType getListType(Class<?> clazz) {
		return objectMapper.getTypeFactory().constructParametricType(List.class, clazz);
	}
}
