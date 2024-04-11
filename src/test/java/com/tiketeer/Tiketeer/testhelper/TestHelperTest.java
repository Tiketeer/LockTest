package com.tiketeer.Tiketeer.testhelper;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.purchase.Purchase;
import com.tiketeer.Tiketeer.domain.purchase.repository.PurchaseRepository;
import com.tiketeer.Tiketeer.domain.ticket.Ticket;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;
import com.tiketeer.Tiketeer.response.ApiResponse;

@Import({TestHelper.class})
@SpringBootTest
public class TestHelperTest {
	@Autowired
	private TestHelper testHelper;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private PurchaseRepository purchaseRepository;
	@Autowired
	private TicketRepository ticketRepository;
	@Autowired
	private TicketingRepository ticketingRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@AfterEach
	void clearTable() {
		testHelper.cleanDB();
	}

	@Test
	@DisplayName("DB 내 데이터 존재 > TestHelper.cleanDB 호출 > DB 내 모든 테이블이 빔")
	@Transactional
	void cleanDB() {
		// given
		testHelper.initDB();

		var mockEmail = "test@test.com";
		var mockPwd = "1234sdasdf";
		var mockMember = memberRepository.save(Member.builder()
			.email(mockEmail)
			.password(mockPwd)
			.point(0L)
			.build());

		var mockTicketing = ticketingRepository.save(Ticketing.builder()
			.price(10000)
			.member(mockMember)
			.title("Mock Ticketing")
			.location("서울 어딘가")
			.category("몰라")
			.eventTime(LocalDateTime.of(9999, 12, 31, 0, 0))
			.runningMinutes(999)
			.saleStart(LocalDateTime.of(9999, 11, 1, 0, 0))
			.saleEnd(LocalDateTime.of(9999, 11, 30, 0, 0))
			.build());

		var mockPurchase = purchaseRepository.save(Purchase.builder().member(mockMember).build());

		ticketRepository.save(Ticket.builder().ticketing(mockTicketing).purchase(mockPurchase).build());

		var repoForTestList = List.of(
			ticketingRepository,
			purchaseRepository,
			ticketRepository,
			memberRepository
		);

		repoForTestList.forEach(repo -> {
			assertThat(repo.findAll()).isNotEmpty();
		});

		// when
		testHelper.cleanDB();

		// then
		repoForTestList.forEach(repo -> {
			assertThat(repo.findAll()).isEmpty();
		});
	}

	@Test
	@DisplayName("이메일만 지정 > 멤버 생성 요청 > 이메일만 지정된 멤버 생성 (나머지는 메서드 내 기본 값)")
	@Transactional
	void createMemberEmailParamSuccess() {
		// given
		testHelper.initDB();

		var email = "test@test.com";

		// when
		var memberId = testHelper.createMember(email).getId();

		// then
		var memberOpt = memberRepository.findById(memberId);
		assertThat(memberOpt.isPresent()).isTrue();

		var member = memberOpt.get();
		assertThat(member.getEmail()).isEqualTo(email);
		assertThat(passwordEncoder.matches("1q2w3e4r!!", member.getPassword())).isTrue();
		defaultMemberPropertiesTest(member);
	}

	@Test
	@DisplayName("이메일, 패스워드 지정 > 멤버 생성 요청 > 이메일, 패스워드가 지정된 멤버 생성 (나머지는 메서드 내 기본 값)")
	@Transactional
	void createMemberEmailAndPasswordParamSuccess() {
		// given
		testHelper.initDB();

		var email = "test@test.com";
		var password = "qwerty12345!@#$";

		// when
		var memberId = testHelper.createMember(email, password).getId();

		// then
		var memberOpt = memberRepository.findById(memberId);
		assertThat(memberOpt.isPresent()).isTrue();

		var member = memberOpt.get();
		assertThat(member.getEmail()).isEqualTo(email);
		assertThat(passwordEncoder.matches(password, member.getPassword())).isTrue();
		defaultMemberPropertiesTest(member);
	}

	@Test
	@DisplayName("이메일, 패스워드, 포인트 지정 > 멤버 생성 요청 > 이메일, 패스워드, 포이느가 지정된 멤버 생성 (나머지는 메서드 내 기본 값)")
	@Transactional
	void createMemberEmailAndPasswordAndRoleParamSuccess() {
		// given
		testHelper.initDB();

		var email = "test@test.com";
		var password = "qwerty12345!@#$";

		// when
		var memberId = testHelper.createMember(email, password, 10).getId();

		// then
		var memberOpt = memberRepository.findById(memberId);
		assertThat(memberOpt.isPresent()).isTrue();

		var member = memberOpt.get();
		assertThat(member.getEmail()).isEqualTo(email);
		assertThat(passwordEncoder.matches(password, member.getPassword())).isTrue();
		assertThat(member.getPoint()).isEqualTo(10);
	}

	@Test
	@DisplayName("이메일, 포인트 지정 > 멤버 생성 요청 > 이메일, 포인트가 지정된 멤버 생성 (나머지는 메서드 내 기본 값)")
	@Transactional
	void createMemberEmailPointRoleParamSuccess() {
		// given
		testHelper.initDB();

		var email = "test@test.com";
		var password = "1q2w3e4r!!";

		// when
		var memberId = testHelper.createMember(email, 10).getId();

		// then
		var memberOpt = memberRepository.findById(memberId);
		assertThat(memberOpt.isPresent()).isTrue();

		var member = memberOpt.get();
		assertThat(member.getEmail()).isEqualTo(email);
		assertThat(passwordEncoder.matches(password, member.getPassword())).isTrue();
		assertThat(member.getPoint()).isEqualTo(10);
	}

	private <T> void isInTest(Iterable<T> iterable, T target) {
		assertThat(target).isIn(iterable);
	}

	private void defaultMemberPropertiesTest(Member member) {
		assertThat(member.getPoint()).isEqualTo(0);
	}

	private record DeserializeTestClass(String name) {
	}

	@Test
	@DisplayName("ApiResponse 내부 List 형태 JSON 문자열 > 역직렬화 > 지정된 클래스 객체로 반환, 값 동일")
	void getDeserializedListApiResponseSuccess() throws JsonProcessingException {
		String json = "{\"data\":[{\"name\":\"test1\"},{\"name\":\"test2\"}]}";

		ApiResponse<List<DeserializeTestClass>> deserializedListApiResponse = testHelper.getDeserializedListApiResponse(
			json, DeserializeTestClass.class);

		DeserializeTestClass test1 = deserializedListApiResponse.getData().get(0);
		DeserializeTestClass test2 = deserializedListApiResponse.getData().get(1);

		assertThat(test1.name()).isEqualTo("test1");
		assertThat(test2.name()).isEqualTo("test2");
	}

	@Test
	@DisplayName("ApiResponse 형태 JSON 문자열 > 역직렬화 > 지정된 클래스 객체로 반환, 값 동일")
	void getDeserializedApiResponseSuccess() throws JsonProcessingException {
		String json = "{\"data\":{\"name\":\"test1\"}}";

		ApiResponse<DeserializeTestClass> deserializedApiResponse = testHelper.getDeserializedApiResponse(
			json, DeserializeTestClass.class);

		DeserializeTestClass test1 = deserializedApiResponse.getData();

		assertThat(test1.name()).isEqualTo("test1");
	}
}
