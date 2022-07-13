package com.sb.template.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.sb.template.entity.Board;
import com.sb.template.enums.BoardType;
import com.sb.template.repo.BoardRepository;

@Service
public class BoardService {

	@Autowired
	private BoardRepository boardRepository;

	public List<Board> getAllBoard() {

		List<Board> res = boardRepository.findAll();
		if (res == null) return null;

		return res;
	}

	public Board createBoard(Board board) {

		return boardRepository.save(board);
	}

	public Board getBoardOne(int boardNo) {

		Optional<Board> board = boardRepository.findById(boardNo);

		if (board.isEmpty()) {
			return null;
		}

		return board.get();
	}

	public void updateBoardForm(int boardNo, Model model) {
		Optional<Board> board = boardRepository.findById(boardNo);

		if (board.isEmpty()) {
			return ;
		}

		model.addAttribute("boardTypes", BoardType.getBoardTypes());
		model.addAttribute("board", board.get());
	}

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
}
