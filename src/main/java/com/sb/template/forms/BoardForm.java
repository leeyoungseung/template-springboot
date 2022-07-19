package com.sb.template.forms;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.sb.template.entity.Board;

import lombok.Data;

@Data
public class BoardForm {

	private Integer boardNo;

	@NotNull(message = "Please input type")
	private Integer type;

	@NotBlank(message = "Please input title")
	private String title;

	@NotBlank(message = "Please input contents")
	private String contents;

	@Pattern(regexp = "^[a-zA-Z0-9+-_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "Unsuitable inputed ID")
	private String memberId;

	public Board toEntity() {
		Board board = new Board();

		board.setType(this.type);
		board.setTitle(this.title);
		board.setContents(this.contents);
		board.setMemberId(((this.memberId == null || (this.memberId.equals(""))) ? "" : this.memberId));

		return board;
	}
}
