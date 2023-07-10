package org.zerock.j1.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name="t_reply")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString(exclude = "board")
public class Reply {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rno;

    private String replyText;

    private String replyFile;

    private String replyer;
    // 필요할때 까지 쿼리를 날리지않게 하기 위해서 건다
    // 연관관계를 걸때는 기본적으로 LazyLoading을 건다.
    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

     // 수정시 쓰기위한 함수 2개 설정  바꾸는 함수들만
     public void changeText(String text){
        this.replyText = text;
    }

    public void changeFile(String fileName){

        this.replyFile = fileName;
    }

}
