package com.sb.template.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sb.template.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Integer>{

	Optional<Member> findByMemberId(String memberId);
}
