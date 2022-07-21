package com.sb.template.exception;

import java.util.List;

public class UnvalidParamException extends RuntimeException {

	private static final long serialVersionUID = 5741815589408622334L;

	public UnvalidParamException(String msg) {
		super(msg);
	}

	public UnvalidParamException(List<String> msgList) {
		this(String.join(", ", msgList));
	}

}
