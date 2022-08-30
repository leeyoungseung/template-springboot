package com.sb.template.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sb.template.dto.LikeDto;
import com.sb.template.entity.Board;
import com.sb.template.entity.Like;
import com.sb.template.entity.Likeable;
import com.sb.template.entity.Reply;
import com.sb.template.forms.LikeForm;
import com.sb.template.repo.BoardRepository;
import com.sb.template.repo.LikeRepository;
import com.sb.template.repo.MemberRepository;
import com.sb.template.repo.ReplyRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LikeService {

	@Autowired
	private LikeRepository likeRepository;

	@Autowired
	private BoardRepository boardRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private ReplyRepository replyRepository;


	public Likeable getContent(LikeForm form ) throws Exception {
		Integer targetNo = form.getTargetNo();
		String likeType = form.getLikeType();

		log.info("param check targetNo : {}, likeType : {}", targetNo, likeType);

		if (targetNo == null || likeType == null) {
			throw new Exception("Not Exist param!! targetNo : ["+targetNo+"], likeType : ["+likeType+"]");
		}

		if (likeType.equals("board")) {
			Optional<Board> dataOne = boardRepository.findByBoardNo(targetNo);

			if (dataOne.isEmpty()) {
				throw new Exception("Not Exist board Data!! TargetNo : ["+form.getTargetNo()+"], memberId : ["+form.getMemberId()+"]");
			}

			return dataOne.get();

		} else if (likeType.equals("reply")) {
			Optional<Reply> dataOne = replyRepository.findByReplyNo(targetNo);

			if (dataOne.isEmpty()) {
				throw new Exception("Not Exist reply Data!! TargetNo : ["+form.getTargetNo()+"], memberId : ["+form.getMemberId()+"]");
			}

			return dataOne.get();

		} else {
			throw new Exception("Unsuitable Like type!! targetNo : ["+targetNo+"], likeType : ["+likeType+"]");
		}

	}


	@Transactional
	public boolean createOrUpdateLike(LikeForm form) throws Exception {

		Like like = form.toEntity();

		// 1. Exist target data?
		Likeable dataOne = getContent(form);

		// 2. Exist Member by MemberId?
		if (like.getMemberId() == null || memberRepository.findByMemberId(like.getMemberId()).isEmpty()) {
			throw new Exception("Not Exist Member Data!! ["+like.toString()+"]");
		}

		// 3. Exist Like data?
		Optional<Like> likeDataOne = likeRepository.findByTargetNoAndMemberId(+form.getTargetNo(), form.getMemberId());

		if (likeDataOne.isEmpty()) {
			// 3-Case1-1. Create Like Data

			likeRepository.save(like);

			// 3-Case1-2. Update Board Data. increment count of Like or Dislike.
			setLikeCountPlus(like.isLikeStatus(), dataOne);

		} else {

			Like oldLike = likeDataOne.get();

			// 3-Case2-1 Equal like status
			// Just decrement count of like or dislike.
			if (oldLike.isLikeStatus() == like.isLikeStatus()) {

				setLikeCountMinus(like.isLikeStatus(), dataOne);

				log.info("Delete Like Data : {} ", oldLike.toString());

				likeRepository.delete(oldLike);

			// 3-Case2-2 Unequal like status
			// Both decrement count of like or dislike and increment opposite value.
			} else if (oldLike.isLikeStatus() != like.isLikeStatus()) {

				setLikeCountToggle(like.isLikeStatus(), dataOne);

				oldLike.setLikeStatus(like.isLikeStatus());

				log.info("Update Like Data : {} ", oldLike.toString());

				likeRepository.save(oldLike);

			} else {
				throw new Exception("Failure to process Update Like Data!! ["+like.toString()+"]");
			}

		}

		// 4. Save Board OR Reply Data. count of like and dislike
		if (dataOne instanceof Board) {
			Board board = (Board) dataOne;
			log.info("Update Board Data : {} ", board.toString());
			boardRepository.save(board);

		} else if (dataOne instanceof Reply) {
			Reply reply = (Reply) dataOne;
			log.info("Update Reply Data : {} ", reply.toString());
			replyRepository.save(reply);

		} else {
			throw new Exception("Unsuitable Like type!! targetNo : ["+form.getTargetNo()+"], likeType : ["+form.getLikeType()+"]");
			
		}

		return true;
	}


	@Transactional
	public LikeDto getLikeInfo(Integer targetNo, String memberId, String likeType) throws Exception {

		LikeDto dto = new LikeDto();
		List<Like> likeList = new ArrayList<Like>();
		List<Like> dislikeList = new ArrayList<Like>();
		Optional<Like> dataOne = null;
		Optional<List<Like>> dataList = null;

		// 1. Exist target data?
		if (likeType.equals("board")) {
			Optional<Board> boardDataOne = boardRepository.findByBoardNo(targetNo);
			if (boardDataOne.isEmpty()) {
				throw new Exception("Not Exist Data!! boardNo : ["+targetNo+"], memberId : ["+memberId+"]");
			}

		} else if (likeType.equals("reply")) {
			Optional<Reply> replyDateOne = replyRepository.findByReplyNo(targetNo);
			if (replyDateOne.isEmpty()) {
				throw new Exception("Not Exist Data!! replyNo : ["+targetNo+"], memberId : ["+memberId+"]");
			}

		} else {
			throw new Exception("Unsuitable Like type!! targetNo : ["+targetNo+"], likeType : ["+likeType+"]");

		}

		// 2. Set response parameter to control a function of like.
		dataOne = likeRepository.findByTargetNoAndMemberId(targetNo, memberId);
		if (dataOne.isEmpty()) {
			dto.setLikeStatus(false);
			dto.setDislikeStatus(false);

		} else {
			if (dataOne.get().isLikeStatus()) {
				dto.setLikeStatus(dataOne.get().isLikeStatus());
				dto.setDislikeStatus(!dataOne.get().isLikeStatus());

			} else {
				dto.setLikeStatus(!dataOne.get().isLikeStatus());
				dto.setDislikeStatus(dataOne.get().isLikeStatus());

			}
		}

		// 3. Set response parameter to count of like and dislike.
		dataList = likeRepository.findByTargetNo(targetNo);
		if (dataList.isEmpty()) {
			dto.setLikeCount(0);
			dto.setDislikeCount(0);

		} else {
			dataList.get().forEach(like -> {
				if (like.isLikeStatus()) {
					likeList.add(like);

				} else {
					dislikeList.add(like);
				}
			});
		}

		dto.setLikeCount(likeList.size());
		dto.setDislikeCount(dislikeList.size());

		return dto;
	}


	private void setLikeCountMinus(boolean likeStatus, Likeable data) {
		if (likeStatus) {
			data.setLikes(data.getLikes() - 1);
		} else {
			data.setDislikes(data.getDislikes() - 1);
		}
	}


	private void setLikeCountPlus(boolean likeStatus, Likeable data) {
		if (likeStatus) {
			data.setLikes(data.getLikes() + 1);
		} else {
			data.setDislikes(data.getDislikes() + 1);
		}
	}


	private void setLikeCountToggle(boolean likeStatus, Likeable data) {
		if (likeStatus) {
			data.setLikes(data.getLikes() + 1);
			data.setDislikes(data.getDislikes() - 1);
		} else {
			data.setLikes(data.getLikes() - 1);
			data.setDislikes(data.getDislikes() + 1);
		}
	}
}
