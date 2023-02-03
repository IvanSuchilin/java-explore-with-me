package client;

import dto.EndpointHitDto;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.Map;


public class StatClient {
    protected final RestTemplate rest;

    public StatClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<Object> post(EndpointHitDto body) {
        return makeAndSendRequest(HttpMethod.POST, "/hit", null, body);
    }

    protected ResponseEntity<Object> get(String start, String end, Collection<String> uris, boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique);
        return makeAndSendRequest(HttpMethod.GET, "/stats", parameters, null);
    }

    private ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, @Nullable Map<String, Object> parameters,
                                                      @Nullable EndpointHitDto body) {
        HttpEntity requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<Object> mainServerResponse;
        try {
            if (parameters.size() != 0){
                mainServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                mainServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            }

        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(mainServerResponse);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }
        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());
        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }
        return responseBuilder.build();
    }
}
