package com.sb.template.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
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


	@RequestMapping(method = RequestMethod.GET, path = "read/{boardNo}")
	public String viewBoardOne(@PathVariable int boardNo, Model model) {

        model.addAttribute("board", boardService.getBoardOne(boardNo));

		return "board/read";
	}


	@RequestMapping(method = RequestMethod.GET, path = "update/{boardNo}")
	public String updateBoard(@PathVariable int boardNo, Model model) {

		boardService.updateBoardForm(boardNo, model);

		return "board/update";
	}


	@RequestMapping(method = RequestMethod.POST, path = "update/{boardNo}")
	public String updateCompleteBoard(@PathVariable int boardNo, BoardForm form, Model model) {

		boardService.updateBoard(boardNo, form.toEntity());

		model.addAttribute("message", "Update Success");

		return "redirect:/board/read/"+boardNo;
	}


}
