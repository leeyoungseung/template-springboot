package com.sb.template.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sb.template.entity.Like;

public interface LikeRepository extends JpaRepository<Like, Integer>{

	Optional<List<Like>> findByTargetNo(Integer targetNo);

	Optional<Like> findByTargetNoAndMemberId(Integer targetNo, String memberId);
}
