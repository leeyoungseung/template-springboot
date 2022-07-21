package com.sb.template.exception;

import java.util.List;

public class InvalidParamException extends RuntimeException {

	private static final long serialVersionUID = 5741815589408622334L;

	public InvalidParamException(String msg) {
		super(msg);
	}

	public InvalidParamException(List<String> msgList) {
		this(String.join(", ", msgList));
	}

}
