package com.sb.template.dto;

import lombok.Data;

@Data
public class LikeDto {

	private Integer likeCount;

	private Integer dislikeCount;

	private Boolean likeStatus;

	private Boolean dislikeStatus;
}
