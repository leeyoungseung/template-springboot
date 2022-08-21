package com.sb.template.controller;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.WebUtils;

import com.sb.template.dto.ResponseDto;
import com.sb.template.entity.Member;
import com.sb.template.enums.ResponseInfo;
import com.sb.template.forms.AuthForm;
import com.sb.template.service.MemberService;
import com.sb.template.service.storage.FilesStorageService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("auth")
@Slf4j
public class MembershipController {

	@Autowired
	private MemberService memberService;

	@Autowired
	private FilesStorageService filesStorageService;

	@RequestMapping(method = RequestMethod.GET, path = "/join")
	public String joinMember(@ModelAttribute AuthForm form) {

		return "auth/join";
	}


	@RequestMapping(method = RequestMethod.POST, path = "/join")
	public String joinMember(@ModelAttribute @Validated AuthForm form, BindingResult bindingResult, Model model) {

		if (bindingResult.hasErrors()) {
			List<String> errors = bindingResult.getAllErrors().stream().map(e -> e.getDefaultMessage()).collect(Collectors.toList());
			model.addAttribute("message", String.join(", ", errors));
			model.addAttribute("redirectUrl", "/auth/join");
			return "/common/message";
		}

        Member member = memberService.createMember(form);

		if (member == null) {
			model.addAttribute("message", form.getMemberId()+" is already exist!!");
			model.addAttribute("redirectUrl", "/auth/join");
			return "/common/message";
		}

		model.addAttribute("message", member.getMemberId()+"has been joined!!");
		model.addAttribute("redirectUrl", "/auth/login");

		return "/common/message";
	}


	@PostMapping(path = "/checkId")
	@ResponseBody
	public ResponseEntity<?> checkId(
			@RequestParam(required = true,
			name = "memberId")
			@Email
			@NotBlank
			String memberId
			) {

		log.info("Input memberId : {}", memberId);

		return ResponseEntity.ok(ResponseDto.builder()
				.resultCode(ResponseInfo.SUCCESS.getResultCode())
				.message(ResponseInfo.SUCCESS.getMessage())
				.data(memberService.existMemberId(memberId))
				.build()
				);
	}


	@RequestMapping(method = RequestMethod.GET, path = "/login")
	public String loginForm() {
		return "auth/login";
	}


	@RequestMapping(method = RequestMethod.POST, path = "/login")
	public String doLogin(@ModelAttribute @Validated AuthForm form, BindingResult bindingResult, HttpServletRequest req, HttpServletResponse res, Model model) {

		if (bindingResult.hasErrors()) {
			List<String> errors = bindingResult.getAllErrors().stream().map(e -> e.getDefaultMessage()).collect(Collectors.toList());
			model.addAttribute("message", String.join(", ", errors));
			model.addAttribute("redirectUrl", "/auth/join");
			return "/common/message";
		}

		log.info("LoginForm Data : {} ", form.toString());
		memberService.loginProcess(form, req, res, model);

		return "/common/message";
	}


	@RequestMapping(method = RequestMethod.GET, path = "/logout")
	public String logout(HttpServletRequest req, HttpServletResponse res, Model model) {

		HttpSession session = req.getSession();
		Object obj = session.getAttribute("member");

		if (obj != null) {
			session.removeAttribute("member");
			session.invalidate();

			Cookie authCookie = WebUtils.getCookie(req, "authCookie");
			if (authCookie != null) {
				authCookie.setPath("/");
				authCookie.setMaxAge(0);
				res.addCookie(authCookie);

				Optional<Member> entity = memberService.getMemberInfoByMemberId(((Member)obj).getMemberId());
				Member member = entity.get();
				member.setSessionKey("unused");
				member.setSessionLimitTime(new Date(System.currentTimeMillis()));

				memberService.updateMember(member);
			}
		}

		model.addAttribute("message", "Logout Success!!");
		model.addAttribute("redirectUrl", "/");

		return "/common/message";
	}


	@GetMapping("/member/{memberNo}")
	public String readMember(@PathVariable int memberNo,
			HttpServletRequest req, HttpServletResponse res, Model model) {

		log.info("memberNo : {}", memberNo);
		HttpSession session = req.getSession();
		Member member = (Member)session.getAttribute("member");

		try {
			if (memberNo != member.getMemberNo()) {
				log.error("Param memberNo : {}, Session memberNo", memberNo, member.getMemberNo());
				model.addAttribute("message", "Unmatch MemberNo");
				model.addAttribute("redirectUrl", "/auth/join");
				return "/common/message";
			}

			Optional<Member> result = memberService.getMemberInfoByMemberId(member.getMemberId());
			if (result.isEmpty()) {
				log.error("Param memberNo : {}, Session memberNo", memberNo, member.getMemberNo());
				model.addAttribute("message", "Unmatch MemberNo");
				model.addAttribute("redirectUrl", "/auth/join");
				return "/common/message";
			}

			model.addAttribute("memberInfo", result);

		} catch (NullPointerException e) {
			log.error(e.getMessage());
			model.addAttribute("message", "Session value is null");
			model.addAttribute("redirectUrl", "/auth/login");
			return "/common/message";

		} catch (Exception e) {
			log.error(e.getMessage());
			model.addAttribute("message", "Server error");
			model.addAttribute("redirectUrl", "/auth/join");
			return "/common/message";
		}

		return "/auth/memberInfo";
	}



	@PostMapping("/thumb")
	@ResponseBody
	public ResponseEntity<?> uploadThumb(
			@RequestParam(value = "file") MultipartFile [] file,
			HttpServletRequest req, HttpServletResponse res
			) throws IOException {

		    HttpSession session = req.getSession();
		    Member member = (Member)session.getAttribute("member");

			String uploadedFile = filesStorageService.saveOne(file[0], member.getMemberNo().toString());

			log.info("Response Data : ["+uploadedFile+"]");

			return ResponseEntity.ok(uploadedFile);
	}

	@GetMapping("/thumb")
	@ResponseBody
	public ResponseEntity<Resource> getThumb(@RequestParam(value = "filename") String filename,
			HttpServletRequest req, HttpServletResponse res) throws IOException {

	    HttpSession session = req.getSession();
	    Member member = (Member)session.getAttribute("member");

		Resource resource = filesStorageService.load(filename, member.getMemberNo().toString());

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+resource.getFilename());
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");


        log.info("thumb response Data : "+resource);
        return ResponseEntity.ok()
                .headers(header)
                .contentLength(resource.getFile().length())
                .body(resource);
	}


	@PostMapping("/profile-pic")
	public String joinMemberProfilePic(@RequestParam(value = "filename") String filename,
			HttpServletRequest req, HttpServletResponse res, Model model) throws IOException {

	    HttpSession session = req.getSession();
	    Member member = (Member)session.getAttribute("member");

		// exist and data
		Optional<Member> memberOp = memberService.getMemberInfoByMemberId(member.getMemberId());
		if (memberOp.isEmpty()) {
			model.addAttribute("message", member.getMemberId()+" not exist!!");
			model.addAttribute("nextUrl", "/auth/join");
			return "/common/message";
		}

		String saveFileName = new File (filesStorageService.move(filename, filename)).getName();

		Member memberForSave = memberOp.get();
		memberForSave.setProfilePicture(saveFileName);

		log.info("setProfilePicture : {}", saveFileName);

		memberService.updateMember(memberForSave);

		member.setProfilePicture(saveFileName);
		session.setAttribute("member", member);

		return "redirect:/auth/member/" + member.getMemberNo();
	}

}
