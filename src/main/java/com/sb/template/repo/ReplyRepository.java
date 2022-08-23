package com.sb.template.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sb.template.entity.Reply;


public interface ReplyRepository extends JpaRepository<Reply, Integer> {
	Optional<List<Reply>> findByBoardNo(Integer boardNo);

	Optional<Page<Reply>> findByBoardNo(Integer boardNo, Pageable pageable);
}
