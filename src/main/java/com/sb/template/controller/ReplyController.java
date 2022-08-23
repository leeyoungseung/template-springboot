package com.sb.template.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sb.template.dto.ResponseDto;
import com.sb.template.entity.Reply;
import com.sb.template.enums.ResponseInfo;
import com.sb.template.service.ReplyService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(path = "/reply")
public class ReplyController {


	@Autowired
	private ReplyService replyService;


	@GetMapping(path = "list/{boardNo}")
	public ResponseEntity<?> viewReplyListByBoardNo(
			@PathVariable(required = true) int boardNo) {

		List<Reply> replyList = null;

		try {
			replyList = replyService.getReplyAllByBoardNo(boardNo);

			if (replyList == null || replyList.size() == 0) {
				System.err.println(ResponseInfo.NO_CONTENT.getMessage());


				return ResponseEntity.ok(ResponseDto.builder()
						.resultCode(ResponseInfo.NO_CONTENT.getResultCode())
						.message(ResponseInfo.NO_CONTENT.getMessage())
						.data(replyList)
						.build()
						);
			}


			return ResponseEntity.ok(ResponseDto.builder()
					.resultCode(ResponseInfo.SUCCESS.getResultCode())
					.message(ResponseInfo.SUCCESS.getMessage())
					.data(replyList)
					.build()
					);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	}


}
