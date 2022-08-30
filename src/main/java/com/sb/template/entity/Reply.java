package com.sb.template.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;

@Entity
@Table(name = "reply")
@Data
public class Reply extends Likeable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "reply_no")
	private Integer replyNo;

	@Column(name = "contents")
	private String contents;

	@Column(name = "board_no")
	private Integer boardNo;

	@Column(name = "member_id")
	private String memberId;

	@Column(name = "created_time")
	@CreationTimestamp
	private Date createdTime;

	@Column(name = "updated_time")
	@UpdateTimestamp
	private Date updatedTime;

}
