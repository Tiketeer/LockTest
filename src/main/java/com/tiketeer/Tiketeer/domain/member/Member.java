package com.tiketeer.Tiketeer.domain.member;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.tiketeer.Tiketeer.domain.purchase.Purchase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "members")
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE members SET deleted_at = now() WHERE member_id = ?")
@SQLRestriction("deleted_at is null")
@Getter
@ToString
public class Member {
	@Id
	@UuidGenerator
	@Column(name = "member_id", nullable = false, updatable = false)
	private UUID id;

	@Setter
	@Column(name = "email", nullable = false)
	private String email;

	@Setter
	@Column(name = "password")
	private String password;

	@Setter
	@Column(name = "point", nullable = false)
	private long point = 0L;

	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_login_at")
	private LocalDateTime lastLoginAt;

	@OneToMany(mappedBy = "member")
	private List<Purchase> purchases = new ArrayList<>();

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@Builder
	public Member(String email, String password, long point) {
		this.email = email;
		this.password = password;
		this.point = point;
	}
}
