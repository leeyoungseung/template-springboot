package com.sb.template.service;

import java.util.Date;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

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


	public Optional<Member> getMemberInfoByMemberId(String memberId) {
		return memberRepository.findByMemberId(memberId);
	}


	public void loginProcess(AuthForm form,
			HttpServletRequest req, HttpServletResponse res, Model model) {

		// exist and data
		Optional<Member> memberOp = memberRepository.findByMemberId(form.getMemberId());
		if (memberOp.isEmpty()) {
			model.addAttribute("message", form.getMemberId()+" is not exist!!");
			model.addAttribute("redirectUrl", "/auth/join");
			return;
		}

		// Password OK?
		if (!memberOp.get().getPassword().equals(enc.generateSHA512(form.getPassword()))) {
			model.addAttribute("message", "Unmatch Password!!");
			model.addAttribute("redirectUrl", "/auth/login");
			return;
		}

		// Set User Data in Session
		HttpSession session = req.getSession();
		session.setAttribute("member", memberOp.get());

		// Update latest login time.
		memberOp.get().setUpdatedTime(new Date());
		memberRepository.save(memberOp.get());
		log.info("Save login user info {} : ", memberOp.get().toString());

		model.addAttribute("message", "Login Success!!");
		model.addAttribute("redirectUrl", "/");
	}

}
