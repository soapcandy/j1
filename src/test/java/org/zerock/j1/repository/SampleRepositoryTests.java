package org.zerock.j1.repository;

import java.util.Optional;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.zerock.j1.domain.Sample;

import lombok.extern.log4j.Log4j2;

@SpringBootTest
@Log4j2
public class SampleRepositoryTests {
    
    @Autowired
    private SamepleRepository samepleRepository;

    @Test
    public void test1(){
        
        log.info(samepleRepository.getClass().getName());
    }

    @Test
    public void testInsert(){
        // 람다식으로 1~100까지 밀어넣는 test insert select실행후 없으면 insert실행
        // 처음문에서 안바꾸고 2번째 실행시 select문만 날린다 
        // 조금만이라도 달라지면 select문 실행후 update 실행
        IntStream.rangeClosed(1, 100).forEach((i->{
            Sample obj = Sample.builder()
                        .keyCol("u"+i)
                        .first("first")
                        .last("last"+i)
                        .build();
            samepleRepository.save(obj);
        }));
    }

    @Test
    public void testRead(){
        // Read할때에는 키값을 설정해주어야된다.
        String keyCol ="u10";

        // 아예 없을경우에는 null이니까
        Optional<Sample> result = samepleRepository.findById(keyCol);

        Sample obj =result.orElseThrow();
        
        log.info(obj);
    }

    @Test
    public void testDelete(){
        // Delete할때에는 키값을 설정해주어야된다.
        String keyCol = "u1";
        // delete를 날려도 select로 먼저 날리고 실행한다.
        // JPA가 원하는것
	    // DB정보와 관리하는 객체와 정보가 일치하길 원함. 
        // 항상 DB내에 정보를 Entity로 끌어오고나서 
        // DB에 작을 하는것이다.
        samepleRepository.deleteById(keyCol);
    }

    @Test
    public void testPaging(){
        // JPA 페이징 처리 domain쪽
        // Pageable 타입으로 들어가면 리턴도 Page타입으로 나온다
        Pageable pageable = PageRequest.of(0,10,Sort.by("keyCol").descending());
        
       Page<Sample> result = samepleRepository.findAll(pageable);
     
       log.info(result.getTotalElements());
       log.info(result.getTotalPages());

       result.getContent().forEach(obj ->log.info(obj));
    }
}
