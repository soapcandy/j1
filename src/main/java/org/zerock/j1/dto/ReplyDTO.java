package org.zerock.j1.dto;

import lombok.Data;

@Data
public class ReplyDTO {
    
    private Long rno;

    private String replyText;

    private String replyFile;

    private String replyer;
    // bno값 추가
    private Long bno;
    


}
