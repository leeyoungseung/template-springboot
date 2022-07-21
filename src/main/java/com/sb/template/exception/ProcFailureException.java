package com.sb.template.exception;

public class ProcFailureException extends RuntimeException {

	private static final long serialVersionUID = -8737830430351077730L;
	private String redirectUrl;

	public ProcFailureException(String msg) {
		super(msg);
	}

	public ProcFailureException(String msg, String redirectUrl) {
		super(msg);
		this.redirectUrl = redirectUrl;
	}

	public String getRedirectUrl() {
		return this.redirectUrl;
	}

}
