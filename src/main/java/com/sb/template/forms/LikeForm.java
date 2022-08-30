package com.sb.template.forms;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.sb.template.entity.Like;

import lombok.Data;

@Data
public class LikeForm {

	private Integer likeId;

	@NotNull
	private boolean likeStatus;

	@NotNull
	private String likeType;

	@NotNull
	private Integer targetNo;

	@NotBlank(message = "ID is mandatory")
	@Pattern(regexp = "^[a-zA-Z0-9+-_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "Unsuitable inputed ID")
	private String memberId;

	public Like toEntity() throws Exception {
		Like like = new Like();
		like.setTargetNo(targetNo);
		like.setLikeStatus(likeStatus);
		like.setLikeType(likeType == null ? "board" : likeType);
		like.setMemberId(memberId);
		return like;
	}

}
