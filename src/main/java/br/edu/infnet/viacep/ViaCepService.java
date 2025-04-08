package br.edu.infnet.viacep;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ViaCepService {
    private static final String BASE_URL = "https://viacep.com.br/ws/";
    private static final String FORMAT = "/json";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ViaCepService() {
        this(HttpClient.newHttpClient());
    }

    public ViaCepService(HttpClient httpClient) {
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper();
    }

    public ViaCepResponse buscarEndereco(String cep) throws IOException, InterruptedException {
        if (cep == null || cep.trim().isEmpty()) {
            throw new IllegalArgumentException("CEP não pode ser nulo ou vazio");
        }

        if (!cep.matches("\\d{8}")) {
            throw new IllegalArgumentException("CEP deve conter exatamente 8 dígitos");
        }

        String url = BASE_URL + cep + FORMAT;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            try {
                ViaCepResponse viaCepResponse = objectMapper.readValue(response.body(), ViaCepResponse.class);
                if (viaCepResponse.getErro()) {
                    throw new IllegalArgumentException("CEP não encontrado");
                }
                return viaCepResponse;
            } catch (com.fasterxml.jackson.core.JsonParseException e) {
                throw new RuntimeException("Erro ao processar resposta JSON: " + e.getMessage(), e);
            }
        } else {
            throw new RuntimeException("Erro ao consultar CEP: " + response.statusCode());
        }
    }

    public List<ViaCepResponse> buscarPorEndereco(String uf, String cidade, String logradouro) throws IOException, InterruptedException {
        if (uf == null || cidade == null || logradouro == null) {
            throw new IllegalArgumentException("UF, cidade e logradouro não podem ser nulos");
        }

        if (uf.length() != 2) {
            throw new IllegalArgumentException("UF deve ter exatamente 2 caracteres");
        }

        // Codificando os parâmetros para URL
        String ufEncoded = URLEncoder.encode(uf, StandardCharsets.UTF_8);
        String cidadeEncoded = URLEncoder.encode(cidade, StandardCharsets.UTF_8);
        String logradouroEncoded = URLEncoder.encode(logradouro, StandardCharsets.UTF_8);

        String url = String.format("%s%s/%s/%s/json/", BASE_URL, ufEncoded, cidadeEncoded, logradouroEncoded);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Erro ao buscar endereço: " + response.statusCode());
        }

        String jsonResponse = response.body();
        if (jsonResponse == null || jsonResponse.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            return objectMapper.readValue(jsonResponse, new TypeReference<List<ViaCepResponse>>() {});
        } catch (com.fasterxml.jackson.core.JsonParseException e) {
            throw new RuntimeException("Erro ao processar resposta JSON: " + e.getMessage(), e);
        }
    }
} 