package client;

import dto.EndpointHitDto;
import dto.StatDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
public class StatClient {
    private final RestTemplate template;
    private final String appName;

    public StatClient(@Value("${stat-server.url}") String url,
                      @Value("${application.name}") String appName,
                      RestTemplateBuilder template) {
        this.appName = appName;
        this.template = template
                .uriTemplateHandler(new DefaultUriBuilderFactory(url))
                .build();
    }

    public void addHit(EndpointHitDto endpointHitDto) {
        template.postForEntity("/hit",
                new HttpEntity<>(endpointHitDto),
                EndpointHitDto.class);
    }

    public ResponseEntity<List<StatDto>> getStat(String start,
                                                 String end,
                                                 List<String> uris,
                                                 boolean unique) {
        return template.exchange("/stats?start={start}&end={end}&uris={uris}&unique={unique}",
                HttpMethod.GET,
                new HttpEntity<>(null),
                new ParameterizedTypeReference<>() {
                },
                start, end, uris, unique);
    }
}
