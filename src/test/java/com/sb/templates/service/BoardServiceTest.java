package com.sb.templates.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.sb.template.entity.Board;
import com.sb.template.repo.BoardRepository;
import com.sb.template.service.BoardService;

@ExtendWith(MockitoExtension.class)
public class BoardServiceTest {

	@Mock
	private BoardRepository boardRepo;

	@InjectMocks
	private BoardService boardServ;


	@Test
	@DisplayName("Testing BoardService getAllBoard")
	public void getAllBoardTest() {

		// given
		Pageable pageable = PageRequest.of(0, 10);

		Board board1 = new Board(1, 1, "test_board_title",
				"test_board_contents001","test001@gmail.com",new Date(), new Date(), 0, 0);
		Board board2 = new Board(2, 2, "test_board_title",
				"test_board_contents002","test002@gmail.com",new Date(), new Date(), 0, 0);
		List<Board> boardList = new ArrayList<Board>();
		boardList.add(board1);
		boardList.add(board2);

		Page<Board> expectedResult = new PageImpl<Board>(boardList);

		BDDMockito.given(boardRepo.findAll(pageable)).willReturn(expectedResult);

		// when
		Page<Board> result = boardServ.getAllBoard(pageable);

		// then
		Assertions.assertThat(result).isNotNull();
		Assertions.assertThat(result.getContent().size()).isEqualTo(2);
		Assertions.assertThat(result.getContent().get(0).getTitle()).isEqualTo(board1.getTitle());

	}

}

