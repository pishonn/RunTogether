package com.example.demo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(GreetingController.class) // 웹 레이어의 GreetingController만 테스트 환경에 로드
public class WebapplicationTest {

    @Autowired
    private MockMvc mockMvc; // MockMvc를 자동 주입 받음

    // @MockBean
    // private MyService myService; //MyService를 MockBean으로 주입

    // @MockBean
    // private UserRepository userRepository; //UserRepository를 MockBean으로 주입

    // @MockBean
    // private PostRepository postRepository; //PostRepository를 MockBean으로 주입



    @Test
    public void greetingShouldReturnDefaultMessage() throws Exception {
        this.mockMvc.perform(get("/greeting")) //"/" 엔드포인트로 GET 요청 수행
                .andDo(print()) //요청과 응답 내용을 출력
                .andExpect(status().isOk()) //HTTP 상태 코드 200 확인
                .andExpect(content().string("Hello, World!")); //반환된 컨텐츠가 ""인지 확인
    }
}
