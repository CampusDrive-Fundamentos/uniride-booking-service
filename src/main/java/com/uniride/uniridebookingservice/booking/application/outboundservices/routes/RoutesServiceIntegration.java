package com.uniride.uniridebookingservice.booking.application.outboundservices.routes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class RoutesServiceIntegration {

    @Value("${routes.service.url}")
    private String routesUrl;

    private final RestTemplate restTemplate = new RestTemplate(new JdkClientHttpRequestFactory());

    private HttpHeaders createHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.replace("Bearer ", ""));
        headers.set("Content-Type", "application/json");
        return headers;
    }

    public void updateVisibility(Long routeId, String visibility, String token) {
        String url = routesUrl + "/" + routeId + "/visibility?visibility=" + visibility;
        restTemplate.exchange(url, HttpMethod.PATCH, new HttpEntity<>(createHeaders(token)), Void.class);
    }

    public void deleteRoute(Long routeId, String token) {
        String url = routesUrl + "/" + routeId;
        restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(createHeaders(token)), Void.class);
    }

    public void addWaypoint(Long routeId, Double lat, Double lng, String address, String token) {
        String url = routesUrl + "/" + routeId + "/waypoints";
        Map<String, Object> body = new HashMap<>();
        body.put("lat", lat);
        body.put("lng", lng);
        body.put("address", address);
        restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(body, createHeaders(token)), Void.class);
    }

    public void removeWaypoint(Long routeId, Double lat, Double lng, String token) {
        String url = routesUrl + "/" + routeId + "/waypoints?lat=" + lat + "&lng=" + lng;
        restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(createHeaders(token)), Void.class);
    }

    public Double getDistanceToWaypoint(Long routeId, Double lat, Double lng, String token) {
        String url = routesUrl + "/" + routeId + "/distance?lat=" + lat + "&lng=" + lng;
        try {
            org.springframework.http.ResponseEntity<Double> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(createHeaders(token)), Double.class);
            return response.getBody() != null ? response.getBody() : 0.0;
        } catch (Exception e) {
            return 0.0; // Fallback en caso de error
        }
    }

    public java.util.List<Long> searchNearbyRouteIds(String campus, Double lat, Double lng, String token) {
        String url = routesUrl + "/search?campus=" + campus + "&lat=" + lat + "&lng=" + lng;
        org.springframework.http.ResponseEntity<java.util.List> response = restTemplate.exchange(
            url, HttpMethod.GET, new HttpEntity<>(createHeaders(token)), java.util.List.class);
        if (response.getBody() == null) return java.util.Collections.emptyList();

        return response.getBody().stream()
            .map(obj -> {
                java.util.Map<?, ?> map = (java.util.Map<?, ?>) obj;
                return Long.parseLong(map.get("id").toString());
            })
            .toList();
    }
}