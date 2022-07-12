package com.sb.template.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sb.template.entity.Board;
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
}
