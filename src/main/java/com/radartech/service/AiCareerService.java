package com.radartech.service;

import com.radartech.domain.AppUser;
import com.radartech.domain.CandidateExperience;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class AiCareerService {

    private static final String SYSTEM_PROMPT = """
            Você é o assistente de carreira do Radar Tech. Responda em português do Brasil,
            com orientações curtas, respeitosas e práticas. Use apenas o contexto profissional
            fornecido. Não invente experiências, qualificações, empresas ou vagas.
            """;

    private final RestClient restClient;
    private final boolean enabled;
    private final String apiKey;
    private final String model;

    public AiCareerService(RestClient.Builder restClientBuilder,
                           @Value("${ai.provider.base-url:https://api.openai.com}") String baseUrl,
                           @Value("${ai.provider.api-key:}") String apiKey,
                           @Value("${ai.provider.model:gpt-4o-mini}") String model,
                           @Value("${ai.provider.enabled:false}") boolean enabled) {
        this.restClient = restClientBuilder.baseUrl(normalizeBaseUrl(baseUrl)).build();
        this.apiKey = apiKey;
        this.model = model;
        this.enabled = enabled;
    }

    public String generateCareerAdvice(AppUser user, List<CandidateExperience> experiences, String question) {
        if (!enabled) {
            throw new IllegalStateException("O assistente de IA está desativado.");
        }
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("A integração com o provedor de IA ainda não foi configurada.");
        }
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("A pergunta não pode estar vazia.");
        }

        ChatRequest request = new ChatRequest(model, List.of(
                new Message("system", SYSTEM_PROMPT),
                new Message("user", profileContext(user, experiences) + "\n\nPergunta: " + question.trim())
        ), 0.3);

        ChatResponse response = restClient.post()
                .uri("/v1/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBearerAuth(apiKey))
                .body(request)
                .retrieve()
                .body(ChatResponse.class);

        if (response == null || response.choices() == null || response.choices().isEmpty()
                || response.choices().getFirst().message() == null
                || response.choices().getFirst().message().content() == null
                || response.choices().getFirst().message().content().isBlank()) {
            throw new IllegalStateException("O provedor de IA não retornou uma orientação válida.");
        }
        return response.choices().getFirst().message().content().trim();
    }

    private String profileContext(AppUser user, List<CandidateExperience> experiences) {
        String biography = user.getBiography() == null || user.getBiography().isBlank()
                ? "não informada"
                : user.getBiography().trim();
        String experienceSummary = experiences == null || experiences.isEmpty()
                ? "nenhuma experiência cadastrada"
                : experiences.stream()
                .limit(8)
                .map(experience -> "%s em %s (%s a %s)".formatted(
                        experience.getRoleTitle(),
                        experience.getCompany(),
                        experience.getStartedOn(),
                        experience.getEndedOn() == null ? "atual" : experience.getEndedOn()))
                .reduce((left, right) -> left + "; " + right)
                .orElse("nenhuma experiência cadastrada");
        return """
                Contexto profissional:
                Nome: %s
                Biografia: %s
                Experiências: %s
                """.formatted(user.getName(), biography, experienceSummary);
    }

    private static String normalizeBaseUrl(String baseUrl) {
        String value = baseUrl == null || baseUrl.isBlank() ? "https://api.openai.com" : baseUrl.trim();
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    public record Message(String role, String content) {
    }

    public record ChatRequest(String model, List<Message> messages, double temperature) {
    }

    public record Choice(Message message) {
    }

    public record ChatResponse(List<Choice> choices, Map<String, Object> usage) {
    }
}
