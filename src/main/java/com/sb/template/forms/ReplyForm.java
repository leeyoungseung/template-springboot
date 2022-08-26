package com.sb.template.forms;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.sb.template.entity.Reply;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "Data when create or update Reply.")
@Data
public class ReplyForm {

	@ApiModelProperty(value = "'Reply Number' which is unique value of Reply.", required = false, example = "1")
	private Integer replyNo;

	@ApiModelProperty(value = "Reply Contents", required = true, example = "example-Reply-Contents")
	@NotBlank(message = "Please input contents")
	private String contents;

	@ApiModelProperty(value = "'Board Number' associated with List of Reply.", required = true, example = "1")
	@NotNull(message = "ID is mandatory")
	private Integer boardNo;

	@ApiModelProperty(value = "'Member ID' associated with List of Reply.", required = true, example = "test@gmail.com")
	@NotBlank(message = "ID is mandatory")
	@Pattern(regexp = "^[a-zA-Z0-9+-_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "Unsuitable inputed ID")
	private String memberId;

	public Reply toEntity() {
		Reply reply = new Reply();
		if (replyNo != null) reply.setReplyNo(replyNo);
		reply.setContents(contents);
		reply.setBoardNo(boardNo);
		reply.setMemberId(memberId);
		return reply;
	}

}
