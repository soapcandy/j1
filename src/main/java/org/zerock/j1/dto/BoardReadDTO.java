package org.zerock.j1.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

// 단점 jsonFormat jsonIgnore 안먹힘
public interface BoardReadDTO {

    Long getBno();

    String getTitle();

    String getContent();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime getRegDate();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime getModDate();

}
