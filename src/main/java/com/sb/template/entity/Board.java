package com.sb.template.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "board")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Board {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "board_no")
	private Integer boardNo;

	@Column(name = "type")
	private Integer type;

	@Column(name = "title")
	private String title;

	@Column(name = "contents")
	private String contents;

	@Column(name = "member_id")
	private String memberId;

	@Column(name = "created_time")
	private Date createdTime;

	@Column(name = "updated_time")
	private Date updatedTime;

	@Column(name = "likes")
	private Integer likes;

	@Column(name = "dis_likes")
	private Integer dislikes;

}

