package com.sb.template.repo;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.sb.template.entity.Board;
import com.sb.template.forms.BoardForm;


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

	@Test
	@DisplayName("Testing BoardRepository save")
	public void saveTest() {
		// given
		BoardForm form = new BoardForm();
		form.setType(1);
		form.setTitle("test_board_title");
		form.setContents("test_board_contents001");
		form.setMemberId("test001@gmail.com");

		Board board = form.toEntity();

		// when
		Board savedBoard = boardRepo.save(board);

		// then
		Assertions.assertThat(board).isSameAs(savedBoard);
        Assertions.assertThat(board.getTitle()).isEqualTo(savedBoard.getTitle());
        Assertions.assertThat(savedBoard.getBoardNo()).isNotNull();

	}

}
