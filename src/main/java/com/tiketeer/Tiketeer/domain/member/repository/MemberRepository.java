package com.tiketeer.Tiketeer.domain.member.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tiketeer.Tiketeer.domain.member.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {
	Optional<Member> findByEmail(String email);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE Member m SET m.deletedAt = current_timestamp WHERE m.deletedAt is null")
	int softDeleteAllInBatch();
}
