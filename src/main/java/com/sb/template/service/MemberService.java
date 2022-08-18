package com.sb.template.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sb.template.entity.Member;
import com.sb.template.enums.MemberRole;
import com.sb.template.forms.AuthForm;
import com.sb.template.repo.MemberRepository;
import com.sb.template.utils.EncUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MemberService {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private EncUtil enc;

	public Member createMember(AuthForm form) {

		Optional<Member> memberOp = memberRepository.findByMemberId(form.getMemberId());
		if (!memberOp.isEmpty()) {
			log.warn("{} is already exist!!", form.getMemberId());
			return null;
		}

		Member member = form.toEntity();
		member.setPassword(enc.generateSHA512(member.getPassword()));
		member.setRole(MemberRole.COMMON.value);

		member = memberRepository.save(member);
		return member;
	}


	public boolean existMemberId(String memberId) {
		Optional<Member> result = memberRepository.findByMemberId(memberId);
		return result.isEmpty() ? false : true;
	}

}
