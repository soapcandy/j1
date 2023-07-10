package org.zerock.j1.repository;

import java.util.Optional;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.zerock.j1.domain.Todo;
import org.zerock.j1.dto.TodoDTO;

import lombok.extern.log4j.Log4j2;

@SpringBootTest
@Log4j2
public class TodoRepositoryTests {
    
    @Autowired
    private ModelMapper modelMapper;

    @Test
    public void testRead(){
        // library를 통한 entity를 dto로 변경
        // 한계점 --> join을 할때 변경시 한계가 걸린다. 
        Long tno = 100L;

        Optional<Todo> result = todoRepository.findById(tno);

        Todo entity = result.orElseThrow();

        log.info("ENTITY--------------------------");
        log.info(entity);

        TodoDTO dto = modelMapper.map(entity,TodoDTO.class);

        log.info(dto);

    }

    @Autowired
    private TodoRepository todoRepository;

    @Test
    public void testInsert(){

        IntStream.rangeClosed(1, 100).forEach(i->{

            Todo todo = Todo.builder().title("Title" +i).build();
            // myBatis에서 select key 하는것을 안해도 된다. 
            // JPA가 다 따와서 가져온다.
            // ID가 없으므로 select를 안날리고 insert로 일어난다.
            Todo result = todoRepository.save(todo);

            log.info(result);
        });
        
    }
    
    @Test
    public void testPaging(){
        // Entity 멤버변수 명으로
        Pageable pageable
         = PageRequest.of(0,10,Sort.by("tno").descending());

        Page<Todo> result= todoRepository.findAll(pageable);

        log.info(result);
    }

}
