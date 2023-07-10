package org.zerock.j1.repository.search;

import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.j1.domain.Board;
import org.zerock.j1.domain.QBoard;
import org.zerock.j1.domain.QReply;
import org.zerock.j1.dto.BoardListRcntDTO;
import org.zerock.j1.dto.PageRequestDTO;
import org.zerock.j1.dto.PageResponseDTO;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class BoardSearchImpl extends QuerydslRepositorySupport implements BoardSearch {

    // 생성자를 만들어준다.
    public BoardSearchImpl() {
        super(Board.class);
    }

    // interface 메소드를 구현
    @Override
    public Page<Board> search1(String searchType, String keyword, Pageable pageable) {

        // QueryDomain 이 필요하다
        QBoard board = QBoard.board;
        // Query를 동적으로 만들어내는 작업
        // SQL 문을 객체화 시켜놓은것
        JPQLQuery<Board> query = from(board);

        // query.where(board.title.contains("1"));

        // 키워드 와 타입이 있는지 확인 후
        if (keyword != null && searchType != null) {
            // tc => [t,c]
            String[] searchArr = searchType.split("");
            // BooleanBuilder 생성
            BooleanBuilder searchBuilder = new BooleanBuilder();

            for (String type : searchArr) {

                switch (type) {
                    case "t" -> searchBuilder.or(board.title.contains(keyword));
                    case "c" -> searchBuilder.or(board.content.contains(keyword));
                    case "w" -> searchBuilder.or(board.writer.contains(keyword));
                }

            } // end for
              // for문 끝난후 where 로 searchBuilder 추가
            query.where(searchBuilder);
        }

        this.getQuerydsl().applyPagination(pageable, query);
        // list를 가져오는 방법
        List<Board> list = query.fetch();
        long count = query.fetchCount();

        log.info(list);
        log.info("count: " + count);
        // 동적쿼리까지 처리된 list
        return new PageImpl<>(list, pageable, count);
    }

    @Override
    public Page<Object[]> searchWithRcnt(String searchType, String keyword, Pageable pageable) {

        QBoard board = QBoard.board;
        QReply reply = QReply.reply;

        // JPQL로 보드 관련 테이블 만드는데 board에서 만든다
        JPQLQuery<Board> query = from(board);
        // left join 항상 left join거는 쪽을 기준으로 잡는다.
        query.leftJoin(reply).on(reply.board.eq(board));

        // 검색조건 추가
        if (keyword != null && searchType != null) {
            // tc => [t,c]
            String[] searchArr = searchType.split("");
            // BooleanBuilder 생성 ()
            BooleanBuilder searchBuilder = new BooleanBuilder();

            for (String type : searchArr) {

                switch (type) {
                    case "t" -> searchBuilder.or(board.title.contains(keyword));
                    case "c" -> searchBuilder.or(board.content.contains(keyword));
                    case "w" -> searchBuilder.or(board.writer.contains(keyword));
                    case "r" -> searchBuilder.or(reply.replyText.contains(keyword));
                }

            } // end for

              // for문 끝난후 where 로 searchBuilder 추가
            query.where(searchBuilder);
        }

        // group by
        query.groupBy(board);

        // JPQL tuple 타입으로 뽑아줘야된다.
        // select에 들어가는거는 실제로 추출하는 데이터 column
        // count 와 countdistinct는 중복된걸 배제하기위해서 사용되고, 조인이 곱의 방식이기때문
        JPQLQuery<Tuple> tupleQuery = query.select(board.bno, board.title, board.writer, reply.countDistinct());

        // Paging 처리
        this.getQuerydsl().applyPagination(pageable, tupleQuery);

        log.info("1------------------------");
        List<Tuple> tuples = tupleQuery.fetch();

        List<Object[]> arrList = tuples.stream().map(tuple -> tuple.toArray()).collect(Collectors.toList());
        // tuple 내부에는 toArray가 나온다.

        log.info("2------------------------");
        log.info(tuples);
        log.info("3------------------------");
        long count = tupleQuery.fetchCount();

        log.info("count: " + count);


        // Page까지 처리완료 
        return new PageImpl<>(arrList, pageable, count);

    }

    @Override
    public PageResponseDTO<BoardListRcntDTO> searchDTORcnt(PageRequestDTO requestDTO) {
        
        Pageable pageable = makePageable(requestDTO);

        QBoard board = QBoard.board;
        QReply reply = QReply.reply;

        // JPQL로 보드 관련 테이블 만드는데 board에서 만든다
        JPQLQuery<Board> query = from(board);
        // left join 항상 left join거는 쪽을 기준으로 잡는다.
        query.leftJoin(reply).on(reply.board.eq(board));

        String keyword = requestDTO.getKeyword();
        String searchType = requestDTO.getType();

        // 검색조건 추가
        if (keyword != null && searchType != null) {
            // tc => [t,c]
            String[] searchArr = searchType.split("");
            // BooleanBuilder 생성 ()
            BooleanBuilder searchBuilder = new BooleanBuilder();

            for (String type : searchArr) {

                switch (type) {
                    case "t" -> searchBuilder.or(board.title.contains(keyword));
                    case "c" -> searchBuilder.or(board.content.contains(keyword));
                    case "w" -> searchBuilder.or(board.writer.contains(keyword));
                }

            } // end for

              // for문 끝난후 where 로 searchBuilder 추가
            query.where(searchBuilder);
        }
        // paging 처리
        this.getQuerydsl().applyPagination(pageable, query);

        // group by
        query.groupBy(board);

        // 어제 했던 tuple 뽑는거 까진 똑같음
        // JPQL Query를 바로 BoardListRcntDTO로 추출하는 쿼리
        JPQLQuery<BoardListRcntDTO> listQuery =
        query.select(Projections.bean(
            BoardListRcntDTO.class, 
            board.bno, 
            board.title,
            board.writer,
            board.regDate, 
            reply.countDistinct().as("replyCount")));

        // 쿼리를 List<BoardListRcntDTO>로 추출
        List<BoardListRcntDTO> list = listQuery.fetch();

        log.info("--------------------------");
        log.info(list);

        long totalCount = listQuery.fetchCount();


        return new PageResponseDTO<>(list, totalCount, requestDTO);

    }

    
}
