package com.sb.template.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sb.template.dto.LikeDto;
import com.sb.template.dto.ResponseDto;
import com.sb.template.enums.ResponseInfo;
import com.sb.template.exception.InvalidParamException;
import com.sb.template.forms.LikeForm;
import com.sb.template.service.LikeService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(path = "/like")
public class LikeController {

	@Autowired
	private LikeService likeService;


	@GetMapping
	public ResponseEntity<?> getLikeInfo(
			@RequestParam(name = "targetNo", required = true) Integer targetNo,
			@RequestParam(name = "memberId", required = true) String memberId,
			@RequestParam(name = "likeType", required = true) String likeType
			) throws Exception {

		log.info("targetNo : {}, memberId : {}, likeType : {}", targetNo, memberId, likeType);

		if (targetNo == null || memberId == null || likeType == null) {
			throw new InvalidParamException(
					String.format("Target_No : {%s}, MemberId : {%s}, likeType : {%s}", targetNo, memberId, likeType));
		}

		LikeDto dto = likeService.getLikeInfo(targetNo, memberId, likeType);

		return ResponseEntity.ok(ResponseDto.builder()
				.resultCode(ResponseInfo.SUCCESS.getResultCode())
				.message(ResponseInfo.SUCCESS.getMessage())
				.data(dto)
				.build()
				);
	}


	@PostMapping
	public ResponseEntity<?> createOrUpdateLike(
			@Validated @RequestBody LikeForm form,
			BindingResult bindingResult,
			Model model
			) throws Exception {

		log.info("Like create or update form : {} ", form.toString());

		if (bindingResult.hasErrors()) {
			List<String> errors = bindingResult.getAllErrors().stream().map(e -> e.getDefaultMessage()).collect(Collectors.toList());
			throw new InvalidParamException(String.join(",", errors));
		}

		Boolean result = likeService.createOrUpdateLike(form);
		if (!result) {
			throw new Exception("createOrUpdateLike Process failure.");
		}

		return ResponseEntity.ok(ResponseDto.builder()
				.resultCode(ResponseInfo.SUCCESS.getResultCode())
				.message(ResponseInfo.SUCCESS.getMessage())
				.data(result)
				.build()
				);
	}

}
