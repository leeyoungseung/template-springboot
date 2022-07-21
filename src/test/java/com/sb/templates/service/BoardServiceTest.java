package com.sb.templates.service;

import static org.mockito.ArgumentMatchers.any;

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
		Board board1 = new Board(1, 1, "test_board_title",
				"test_board_contents001","test001@gmail.com",new Date(), new Date(), 0, 0);
		Board board2 = new Board(2, 2, "test_board_title",
				"test_board_contents002","test002@gmail.com",new Date(), new Date(), 0, 0);
		List<Board> boardList = new ArrayList<Board>();
		boardList.add(board1);
		boardList.add(board2);

		BDDMockito.given(boardRepo.findAll()).willReturn(boardList);

		// when
		List<Board> result = boardServ.getAllBoard();

		// then
		Assertions.assertThat(result).isNotNull();
		Assertions.assertThat(result.size()).isEqualTo(2);
		Assertions.assertThat(result.get(0).getTitle()).isEqualTo(board1.getTitle());

	}


	@Test
	@DisplayName("Testing BoardService createBoard")
	public void createBoardTest() {
		// given
		Board board = new Board(1, 1, "test_board_title",
				"test_board_contents001","test001@gmail.com",new Date(), new Date(), 0, 0);
		BDDMockito.given(boardRepo.save(any())).willReturn(board);

		// when
		Board result = boardServ.createBoard(board);

		// then
		Assertions.assertThat(result).isNotNull();
		Assertions.assertThat(result.getTitle()).isEqualTo(board.getTitle());
		Assertions.assertThat(result.getContents()).isEqualTo(board.getContents());
	}
}

