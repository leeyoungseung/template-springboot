package com.sb.template.service;

import java.net.URI;
import java.util.Date;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.sb.template.conf.GoogleConf;
import com.sb.template.dto.google.GoogleLoginDto;
import com.sb.template.entity.Member;
import com.sb.template.enums.MemberRole;
import com.sb.template.forms.google.GoogleLoginRequest;
import com.sb.template.forms.google.GoogleLoginResponse;
import com.sb.template.repo.MemberRepository;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class GoogleService {

	@Autowired
	private GoogleConf conf;

	@Autowired
	private MemberRepository memberRepository;


	public ResponseEntity<?> googleLoginInit() throws Exception {
		String authUri = conf.googleInitUrl();
		URI redirectUri = new URI(authUri);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(redirectUri);

		log.info("AuthURI : {}, RedirectURI : {}", authUri, redirectUri);

		return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
	}


	public void googleLoginProcess(
			String code,
			HttpServletRequest req, HttpServletResponse res, Model model
			) throws Exception {

		RestTemplate restTemplate = new RestTemplate();
		GoogleLoginRequest googleRequestParams = GoogleLoginRequest.builder()
				.clientId(conf.getGoogleClientId())
				.clientSecret(conf.getGoogleSecret())
				.code(code)
				.redirectUri(conf.getGoogleRedirectUri())
				.grantType("authorization_code")
				.build();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<GoogleLoginRequest> httpRequestEntity = new HttpEntity<>(googleRequestParams, headers);

		String googleAuthUrl = conf.getGoogleAuthUrl() + "/token";

		log.info("Google Auth Request Info. GoogleAuthUrl : {}, HttpHeaders : {}, ReqeustParams: {} ",
				googleAuthUrl, headers, googleRequestParams);

		ResponseEntity<String> googleResponseJsonStr =
				restTemplate.postForEntity(googleAuthUrl, httpRequestEntity, String.class);

		if (googleResponseJsonStr == null) {
			throw new Exception(String.format(
							"Failure Google Login Process. Caused by [Auth] googleResponseJsonStr : %s", googleResponseJsonStr));
		}

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		GoogleLoginResponse googleLoginResponse =
				objectMapper.readValue(
						googleResponseJsonStr.getBody(),
						new TypeReference<GoogleLoginResponse>() {}
						);

		log.info("Google Auth Response Info : {} ", googleLoginResponse);

		String jwtToken = googleLoginResponse.getIdToken();
		String googleGetUserInfoUrl = UriComponentsBuilder.fromHttpUrl(
				conf.getGoogleAuthUrl() + "/tokeninfo")
				.queryParam("id_token", jwtToken)
				.toUriString();

		log.info("Google GetUserInfo Request Info. GoogleGetUserInfoUrl : {}, jwtToken : {}",
				googleGetUserInfoUrl, jwtToken);

		String resultJsonStr = restTemplate.getForObject(googleGetUserInfoUrl, String.class);
		log.info("Google GetUserInfo Response Info : {} ", resultJsonStr);

		if (resultJsonStr == null || resultJsonStr.length() == 0) {
			throw new Exception(String.format(
					"Failure Google Login Process. Caused by [UserInfo] resultJsonStr : %s", resultJsonStr));
		}

		GoogleLoginDto dto = objectMapper.readValue(resultJsonStr, new TypeReference<GoogleLoginDto>() {});
		log.info("Google GetUserInfo Response Info -> DTO : {} ", dto);


		Optional<Member> memberOp = memberRepository.findByMemberId(dto.getEmail());
		Member entity = null;

		if (memberOp.isEmpty()) {
			entity = new Member();
			entity.setMemberId(dto.getEmail());
			entity.setPassword("dummy");
			entity.setRole(MemberRole.SOCIAL.value);

		} else {
			entity = memberOp.get();

		}

		entity.setUpdatedTime(new Date());
		entity = memberRepository.save(entity);

		log.info("Save login user info {} : ", entity.toString());


		HttpSession session = req.getSession();
		session.setAttribute("member", entity);

		model.addAttribute("message", "Login Success!!");
		model.addAttribute("redirectUrl", "/");
	}
}
