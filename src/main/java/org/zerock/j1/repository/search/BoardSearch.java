package org.zerock.j1.repository.search;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.zerock.j1.domain.Board;
import org.zerock.j1.dto.BoardListRcntDTO;
import org.zerock.j1.dto.PageRequestDTO;
import org.zerock.j1.dto.PageResponseDTO;

// 검색기능을위한 interface 
// 이름 일치시켜야된다. BoardSearch --> BoardSearchImpl
public interface BoardSearch {
    
    
    // List를 뽑는 메소드
    
    // List<Board> search1();

    //Page<Board> search1 (Pageable pageable);
    // 기본 게시판의 검색조건
    Page<Board> search1 (String searchType, String searchKeyword, Pageable pageable);

    // 기본 게시판 검색에 + 댓글
    Page<Object[]> searchWithRcnt(String searchType, String searchKeyword, Pageable pageable);


    // DTO로 받는 검색방식 method
    PageResponseDTO<BoardListRcntDTO> searchDTORcnt(PageRequestDTO requestDTO);

    // Pageable을 반환하는 것을 만들어주는 method
    default Pageable makePageable(PageRequestDTO requestDTO){

        Pageable pageable = PageRequest.of(
            requestDTO.getPage() -1,
            requestDTO.getSize(),
            Sort.by("bno").descending()
            );
            
            return pageable;
    }
}
