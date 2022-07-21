package com.sb.template.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sb.template.entity.Board;
import com.sb.template.service.BoardService;

@Controller
@RequestMapping(path = "/board")
public class BoardController {

	@Autowired
	private BoardService boardService;

	@RequestMapping(method = RequestMethod.GET, value = {"list", "/", ""})
	public String viewBoardList(Model model) {

		List<Board> boardList = boardService.getAllBoard();
		model.addAttribute("boardList", boardList);

		return "board/list";
	}
}
