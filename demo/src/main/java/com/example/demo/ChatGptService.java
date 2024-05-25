package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import com.logaritex.ai.api.AssistantApi;
import com.logaritex.ai.api.Data.RunRequest;
import com.logaritex.ai.api.Data.ThreadRequest;

import com.logaritex.ai.api.Data;
import com.logaritex.ai.api.Data.MessageRequest;
import com.logaritex.ai.api.Data.Role;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ChatGptService {

    private final AssistantApi assistantApi;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${openai.api.key}")
    private String apiKey;

    public ChatGptService(@Value("${openai.api.key}") String apiKey) {
        this.assistantApi = new AssistantApi(apiKey);
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public String getAssistantResponse(String assistantId, String prompt) {
        try {
            // 어시스턴트 검색
            Data.Assistant assistant = retrieveAssistant(assistantId);

            // 쓰레드 생성
            Data.Thread thread = createThread();

            // 쓰레드에 메시지 추가
            addMessageToThread(thread.id(), prompt);

            // 어시스턴트 실행
            Data.Run run = createRun(thread.id(), assistant.id());

            // 실행 상태 확인 및 완료 대기
            while (assistantApi.retrieveRun(thread.id(), run.id()).status() != Data.Run.Status.completed) {
                Thread.sleep(500);
            }

            // 어시스턴트 응답 메시지 가져오기
            return getAssistantMessages(thread.id());
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    public Data.Assistant retrieveAssistant(String assistantId) {
        Assert.hasText(assistantId, "Assistant ID cannot be empty.");

        String url = "https://api.openai.com/v1/assistants/{assistant_id}";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("OpenAI-Beta", "assistants=v2");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Data.Assistant> response = this.restTemplate.exchange(url, HttpMethod.GET, entity, Data.Assistant.class, assistantId);
        return response.getBody();
    }

    private Data.Thread createThread() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("OpenAI-Beta", "assistants=v2");

        HttpEntity<ThreadRequest> entity = new HttpEntity<>(new ThreadRequest(), headers);
        ResponseEntity<Data.Thread> response = this.restTemplate.exchange("https://api.openai.com/v1/threads", HttpMethod.POST, entity, Data.Thread.class);
        return response.getBody();
    }

    private void addMessageToThread(String threadId, String prompt) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("OpenAI-Beta", "assistants=v2");

        MessageRequest messageRequest = new MessageRequest(Role.user, prompt);
        HttpEntity<MessageRequest> entity = new HttpEntity<>(messageRequest, headers);
        this.restTemplate.exchange("https://api.openai.com/v1/threads/{thread_id}/messages", HttpMethod.POST, entity, Data.Message.class, threadId);
    }

    private Data.Run createRun(String threadId, String assistantId) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("OpenAI-Beta", "assistants=v2");

        RunRequest runRequest = new RunRequest(assistantId);
        HttpEntity<RunRequest> entity = new HttpEntity<>(runRequest, headers);
        ResponseEntity<Data.Run> response = this.restTemplate.exchange("https://api.openai.com/v1/threads/{thread_id}/runs", HttpMethod.POST, entity, Data.Run.class, threadId);
        return response.getBody();
    }

    private String getAssistantMessages(String threadId) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("OpenAI-Beta", "assistants=v2");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = this.restTemplate.exchange("https://api.openai.com/v1/threads/{thread_id}/messages", HttpMethod.GET, entity, String.class, threadId);
        
        JsonNode root = objectMapper.readTree(response.getBody());
        StringBuilder responseText = new StringBuilder();
        for (JsonNode message : root.path("data")) {
            if (message.path("role").asText().equals("assistant")) {
                responseText.append(message.path("content").get(0).path("text").path("value").asText()).append("\n");
            }
        }
        return responseText.toString().trim();
    }
}
