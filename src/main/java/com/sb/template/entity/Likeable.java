package com.sb.template.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class Likeable {

	@Column(name = "likes")
	private Integer likes;

	@Column(name = "dis_likes")
	private Integer dislikes;

	@PrePersist
	public void prePersist() {
		this.likes = this.likes == null ? 0 : this.likes;
		this.dislikes = this.dislikes == null ? 0 : this.dislikes;
	}

}
