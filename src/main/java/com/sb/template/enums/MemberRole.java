package com.sb.template.enums;

public enum MemberRole {

	ADMIN("admin"), COMMON("common"), SOCIAL("social");

	public String value;

	MemberRole(String value) {
		this.value = value;
	}
}
