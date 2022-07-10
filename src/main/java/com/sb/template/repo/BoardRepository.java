package com.sb.template.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sb.template.entity.Board;

public interface BoardRepository extends JpaRepository<Board, Integer> {

	public Optional<Board> findByBoardNo(Integer boardNo);

}
