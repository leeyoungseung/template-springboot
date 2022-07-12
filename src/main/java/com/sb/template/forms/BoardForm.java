package com.sb.template.forms;

import com.sb.template.entity.Board;

import lombok.Data;

@Data
public class BoardForm {

	private Integer no;
	private Integer type;
	private String title;
	private String contents;
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
