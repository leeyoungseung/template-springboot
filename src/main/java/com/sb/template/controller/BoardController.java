package com.sb.template.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sb.template.entity.Board;
import com.sb.template.enums.BoardType;
import com.sb.template.forms.BoardForm;
import com.sb.template.service.BoardService;

@Controller
@RequestMapping(path = "/board")
public class BoardController {

	@Autowired
	private BoardService boardService;

	@RequestMapping(method = RequestMethod.GET, path = "list")
	public String viewBoardList(Model model) {

		List<Board> boardList = boardService.getAllBoard();
		model.addAttribute("boardList", boardList);

		return "board/list";
	}


	@RequestMapping(method = RequestMethod.GET, path = "write")
	public String writeBoard(Model model) {

		model.addAttribute("boardTypes", BoardType.getBoardTypes());

		return "board/write";
	}


	@RequestMapping(method = RequestMethod.POST, path = "write")
	public String writeCompleteBoard(BoardForm form, Model model) {

		Integer boardNo = null;
		boardNo = boardService.createBoard(form.toEntity()).getBoardNo();

		return "redirect:/board/list";
	}

}
