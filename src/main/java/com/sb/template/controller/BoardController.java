package com.sb.template.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.sb.template.entity.Board;
import com.sb.template.enums.BoardType;
import com.sb.template.exception.InvalidParamException;
import com.sb.template.exception.ProcFailureException;
import com.sb.template.forms.BoardForm;
import com.sb.template.service.BoardService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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


	@RequestMapping(method = RequestMethod.GET, path = "write")
	public String writeBoard(@ModelAttribute("boardForm") BoardForm form, Model model) {

		model.addAttribute("boardTypes", BoardType.getBoardTypes());

		return "board/write";
	}


	@RequestMapping(method = RequestMethod.POST, path = "write")
	public String writeCompleteBoard(@ModelAttribute("boardForm") @Validated BoardForm form,
			BindingResult bindingResult, Model model) {

		if (validationForSaveInputValue(bindingResult)) {
			return writeBoard(form, model);
		}
		Integer boardNo = null;
		boardNo = boardService.createBoard(form.toEntity()).getBoardNo();

		if (boardNo == null) {
			throw new ProcFailureException("Failure Board write process");
		}

		return "redirect:/board/list";
	}


	@RequestMapping(method = RequestMethod.GET, path = "read/{boardNo}")
	public String viewBoardOne(@PathVariable(required = true) int boardNo, Model model) {

        model.addAttribute("board", boardService.getBoardOne(boardNo));

		return "board/read";
	}


	@RequestMapping(method = RequestMethod.GET, path = "update/{boardNo}")
	public String updateBoard(@PathVariable(required = true) int boardNo, Model model) {

		boardService.updateBoardForm(boardNo, model);

		return "board/update";
	}


	@RequestMapping(method = RequestMethod.POST, path = "update/{boardNo}")
	public String updateCompleteBoard(
			@PathVariable(required = true) int boardNo,
			@ModelAttribute @Validated BoardForm form,
			BindingResult bindingResult, Model model) {

		validation(bindingResult);

		if (boardService.updateBoard(boardNo, form.toEntity()) == null) {
			throw new ProcFailureException("Failure Board update process");
		}

		model.addAttribute("message", "Update Success");

		return "redirect:/board/read/"+boardNo;
	}


	@RequestMapping(method = RequestMethod.GET, path = "delete/{boardNo}")
	public String deleteBoard(@PathVariable int boardNo, Model model) {

		model.addAttribute("board", boardService.getBoardOne(boardNo));

		return "board/delete";
	}


	@RequestMapping(method = RequestMethod.POST, path = "delete")
	public String deleteCompleteBoard(
			@RequestParam(name = "boardNo", required = true) int boardNo,
			Model model) {

		boardService.deleteBoard(boardNo);

		model.addAttribute("message", "Delete Success");
		return "redirect:/board/list";
	}


	/**
	 * If validation check result is fail, occur InvalidParamException.
	 *
	 * @param bindingResult
	 */
	private void validation(BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			List<String> errorStrList = bindingResult.getAllErrors()
			.stream()
			.map(e -> e.getDefaultMessage())
			.collect(Collectors.toList());

			log.error("Validation-NG : {} ", errorStrList);

			throw new InvalidParamException(errorStrList);
		}
		log.info("Validation-OK");
	}

	/**
	 * If validation check result is OK, return false.
	 * In the opposite case return true.
	 *
	 * @param bindingResult
	 * @return boolean
	 */
	private boolean validationForSaveInputValue(BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			List<String> errorStrList = bindingResult.getAllErrors()
			.stream()
			.map(e -> e.getDefaultMessage())
			.collect(Collectors.toList());

			log.error("Validation-NG : {} ", errorStrList);

			return true;
		}
		log.info("Validation-OK");
		return false;
	}


	@ExceptionHandler
	public ModelAndView procFailureErrorHandler(HttpServletRequest req, ProcFailureException e) {
		ModelAndView mav = new ModelAndView();

		String message = e.getMessage();
		String redirectUrl = "http://localhost:8080/";
		String view = "/common/error";

		mav.addObject("message", message);
		mav.addObject("redirectUrl", redirectUrl);
		mav.setViewName(view);

		log.error("Message : {}, RedirectUrl : {}, View : {}", message, redirectUrl, view);

		return mav;
	}

}
