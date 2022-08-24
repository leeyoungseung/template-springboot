package com.sb.template.controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.sb.template.dto.ResponseDto;
import com.sb.template.entity.Reply;
import com.sb.template.enums.ResponseInfo;
import com.sb.template.exception.InvalidParamException;
import com.sb.template.forms.ReplyForm;
import com.sb.template.service.ReplyService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(path = "/reply")
public class ReplyController {


	@Autowired
	private ReplyService replyService;


	@GetMapping(path = "list-v2/{boardNo}")
	public ResponseEntity<?> viewReplyListByBoardNoNew(
			@PathVariable(required = true) int boardNo, Pageable pageable) throws Exception {

		Page<Reply> replyList = null;

		replyList = replyService.getReplyAllByBoardNoPaging(boardNo, pageable);

		if (replyList == null || replyList.getSize() == 0) {
			log.error(ResponseInfo.NO_CONTENT.getMessage());
			throw new NoSuchElementException(String.format("Failure Reply Create!! Board_No : {%s}", boardNo));
		}

		return ResponseEntity.ok(ResponseDto.builder()
				.resultCode(ResponseInfo.SUCCESS.getResultCode())
				.message(ResponseInfo.SUCCESS.getMessage())
				.data(replyList)
				.build()
				);
	}


	@GetMapping(path = "list/{boardNo}")
	public ResponseEntity<?> viewReplyListByBoardNo(
			@PathVariable(required = true) int boardNo) throws Exception {

		List<Reply> replyList = null;
		replyList = replyService.getReplyAllByBoardNo(boardNo);

		if (replyList == null || replyList.size() == 0) {
			log.error(ResponseInfo.NO_CONTENT.getMessage());
			throw new NoSuchElementException(String.format("Failure Reply Create!! Board_No : {%s}", boardNo));
		}


		return ResponseEntity.ok(ResponseDto.builder()
				.resultCode(ResponseInfo.SUCCESS.getResultCode())
				.message(ResponseInfo.SUCCESS.getMessage())
				.data(replyList)
				.build()
				);

	}


	@PostMapping
	public ResponseEntity<?> createReply(
			@Validated @RequestBody ReplyForm form,
			BindingResult bindingResult) throws Exception {

		if (bindingResult.hasErrors()) {
			List<String> errors = bindingResult.getAllErrors().stream().map(e -> e.getDefaultMessage()).collect(Collectors.toList());
			StringBuilder errorStr = new StringBuilder();
			errors.forEach(err -> {
				errorStr.append("["+err+"],\n");
			});

			throw new InvalidParamException(String.format("Failure Reply Create!! Board_No : {%s}, Error Message {%s}",
					form.getBoardNo(), errorStr.toString()));
		}

		if (replyService.createReply(form.toEntity())) {
			return ResponseEntity.ok(ResponseDto.builder()
					.resultCode(ResponseInfo.SUCCESS.getResultCode())
					.message(ResponseInfo.SUCCESS.getMessage())
					.data(true)
					.build()
					);
		} else {
			throw new Exception(String.format("Failure Reply Create!! Board_No : {%s}", form.getBoardNo()));

		}
	}


	@PutMapping(path = "/{replyNo}")
	public ResponseEntity<?> updateReply(
			@Validated @RequestBody ReplyForm form,
			BindingResult bindingResult,
			@PathVariable(required = true) Integer replyNo) throws Exception {


		if (bindingResult.hasErrors()) {
			List<String> errors = bindingResult.getAllErrors().stream().map(e -> e.getDefaultMessage()).collect(Collectors.toList());
			StringBuilder errorStr = new StringBuilder();
			errors.forEach(err -> {
				errorStr.append("["+err+"],\n");
			});

			throw new InvalidParamException(String.format("Failure Reply Update!! Board_No : {%s}, Reply_No {%s}, Error Message {%s}",
					form.getBoardNo(), replyNo, errorStr.toString()));
		}


		if (replyService.updateReply(replyNo, form.toEntity())) {
			return ResponseEntity.ok(ResponseDto.builder()
					.resultCode(ResponseInfo.SUCCESS.getResultCode())
					.message(ResponseInfo.SUCCESS.getMessage())
					.data(true)
					.build()
					);
		} else {
			throw new Exception(String.format("Failure Reply Update!! Board_No : {%s}, Reply_No {%s}", form.getBoardNo(), replyNo));

		}

	}


	@DeleteMapping(path = "/{replyNo}")
	public ResponseEntity<?> deleteReply(@PathVariable(required = true) Integer replyNo,
			@RequestParam(name = "boardNo", required = false) Integer boardNo,
			@RequestParam(name = "memberId", required = false, value = "") String memberId) throws Exception {

		if (replyService.deleteReply(replyNo, boardNo, memberId)) {
			return ResponseEntity.ok(ResponseDto.builder()
					.resultCode(ResponseInfo.SUCCESS.getResultCode())
					.message(ResponseInfo.SUCCESS.getMessage())
					.data(true)
					.build()
					);

		} else {
			throw new Exception(String.format("Failure Reply Delete!! Board_No : {%s}, Reply_No {%s}", boardNo, replyNo));

		}

	}

}
