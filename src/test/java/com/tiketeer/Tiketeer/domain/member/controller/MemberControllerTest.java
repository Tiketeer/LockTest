package com.tiketeer.Tiketeer.domain.member.controller;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.aspectj.lang.annotation.Before;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiketeer.Tiketeer.domain.member.controller.dto.CreateMembersRequestDto;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@Import({TestHelper.class})
@SpringBootTest
@AutoConfigureMockMvc
public class MemberControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private TestHelper testHelper;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private ObjectMapper objectMapper;

	@AfterEach()
	void cleanTable() {
		testHelper.cleanDB();
	}

	@Test
	@DisplayName("50개의 이메일 리스트 > 배치 생성 요청 > 성공")
	void createMembersInBatchSuccess() throws Exception{
		// given
		List<String> emailList = new ArrayList<>();
		for (var i = 0; i < 50; i++) {
			var email = "test-" + UUID.randomUUID() + "@test.com";
			emailList.add(email);
		}

		var request = CreateMembersRequestDto.builder().emailList(emailList).build();

		Assertions.assertThat(memberRepository.findAll().size()).isEqualTo(0);

		// when
		mockMvc.perform(
			MockMvcRequestBuilders.post("/api/members")
				.contextPath("/api")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8)
				.content(objectMapper.writeValueAsString(request))
			// then
		).andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

		Assertions.assertThat(memberRepository.findAll().size()).isEqualTo(emailList.size());
	}
}
