package com.sb.template.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sb.template.entity.Reply;
import com.sb.template.repo.BoardRepository;
import com.sb.template.repo.MemberRepository;
import com.sb.template.repo.ReplyRepository;


@Service
public class ReplyService {

	@Autowired
	private ReplyRepository replyRepository;

	@Autowired
	private BoardRepository boardRepository;

	@Autowired
	private MemberRepository memberRepository;

	// get reply connected by boardNo
	public List<Reply>getReplyAllByBoardNo(int boardNo) throws Exception {

		// exist board data of parameter?
		if (boardRepository.findByBoardNo(boardNo).isEmpty()) {
			throw new Exception("Not exist Board data!!");
		}

		// get reply data by boardNo
		Optional<List<Reply>> replyData = replyRepository.findByBoardNo(boardNo);
		if (replyData.isEmpty()) {
			return null;
		}

		return replyData.get();
	}


	@Transactional
	public boolean createReply(Reply newReply) {

		// boardNo is not null & exist board data by boardNo
		if (newReply.getBoardNo() == null || !boardRepository.existsById(newReply.getBoardNo())) {
			return false;
		}

		// MemberId is not null & exist Member data of parameter?
		if (newReply.getMemberId() == null || memberRepository.findByMemberId(newReply.getMemberId()).isEmpty()) {
			return false;
		}

		newReply.setLikes(0);
		newReply.setDislikes(0);
		replyRepository.save(newReply);

		return true;
	}


}
