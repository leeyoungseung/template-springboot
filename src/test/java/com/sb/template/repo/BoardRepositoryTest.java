package com.sb.template.repo;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.sb.template.entity.Board;


@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class BoardRepositoryTest {

	@Autowired
	private TestEntityManager em;

	@Autowired
	private BoardRepository boardRepo;


	@Test
	@DisplayName("Testing BoardRepository findAll")
	public void findAllTest() {

		Iterable<Board> result = boardRepo.findAll();
		Assertions.assertThat(result).isNotEmpty();

	}

}
