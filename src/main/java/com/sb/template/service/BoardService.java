package com.sb.template.service;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.sb.template.annotation.Timer;
import com.sb.template.entity.Board;
import com.sb.template.enums.BoardType;
import com.sb.template.repo.BoardRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BoardService {

	@Autowired
	private BoardRepository boardRepository;

	@Timer
	public Page<Board> getAllBoard(Pageable pageable) {
		int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
		pageable = PageRequest.of(page, pageable.getPageSize());

		Page<Board> res = boardRepository.findAll(pageable);
		if (res == null) return null;

		log.debug("Data from DB : {}", res);

		return res;
	}

	@Timer
	public Board createBoard(Board board) {

		return boardRepository.save(board);
	}

	@Timer
	public Board getBoardOne(int boardNo) {

		Optional<Board> board = boardRepository.findById(boardNo);

		if (board.isEmpty()) {
			throw new NoSuchElementException("Not exist Board !! ["+boardNo+"]");
		}

		return board.get();
	}

	@Timer
	public void updateBoardForm(int boardNo, Model model) {
		Optional<Board> board = boardRepository.findById(boardNo);

		if (board.isEmpty()) {
			return ;
		}

		model.addAttribute("boardTypes", BoardType.getBoardTypes());
		model.addAttribute("board", board.get());
	}

	@Timer
	@Transactional
	public Board updateBoard(int boardNo, Board updatedBoard) {

		Optional<Board> res = boardRepository.findById(boardNo);

		if (res.isEmpty()) {
			return null;
		}

		Board board = res.get();
		board.setType(updatedBoard.getType());
		board.setTitle(updatedBoard.getTitle());
		board.setContents(updatedBoard.getContents());

		Board endUpdatedBoard = boardRepository.save(board);
		return endUpdatedBoard;
	}

	@Timer
	public void deleteBoard(int boardNo) {

		Optional<Board> res = boardRepository.findById(boardNo);

		if (res.isEmpty()) {
			return ;
		}

		boardRepository.delete(res.get());
	}
}
