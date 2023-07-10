package org.zerock.j1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing// Date관련으로 감시 하기위해 main에 설정해줌
public class J1Application {

	public static void main(String[] args) {
		SpringApplication.run(J1Application.class, args);
	}

}
