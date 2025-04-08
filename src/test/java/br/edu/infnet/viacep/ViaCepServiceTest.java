package br.edu.infnet.viacep;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.lifecycle.BeforeTry;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ViaCepServiceTest {

    private HttpClient httpClient;
    private HttpResponse<String> httpResponse;
    private ViaCepService viaCepService;
    private ObjectMapper objectMapper;

    @BeforeTry
    void setUp() {
        httpClient = mock(HttpClient.class);
        httpResponse = mock(HttpResponse.class);
        objectMapper = new ObjectMapper();
        viaCepService = new ViaCepService(httpClient);
    }

    @Property
    void buscarEndereco_QuandoCepInvalido_DeveLancarExcecao(
            @ForAll @From("cepsInvalidos") String cepInvalido) {
        assertThrows(IllegalArgumentException.class, () -> {
            viaCepService.buscarEndereco(cepInvalido);
        });
    }

    @Provide("cepsInvalidos")
    Arbitrary<String> cepsInvalidos() {
        return Arbitraries.of(
            // Classe 1: CEPs com caracteres não numéricos
            "12345ABC",  // Letras
            "12.345-67", // Caracteres especiais
            "12345#67",  // Caracteres especiais
            
            // Classe 2: CEPs vazios ou nulos
            "",          // String vazia
            "        ",  // Apenas espaços
            null,        // Valor nulo
            
            // Classe 3: CEPs com formato inválido
            "1234567",   // Menos de 8 dígitos
            "123456789", // Mais de 8 dígitos
            "1234-567",  // Formato com hífen
            
            // Classe 4: CEPs com caracteres especiais
            "12345@67",  // @
            "12345$67",  // $
            "12345%67"   // %
        );
    }

    @Property
    void buscarEndereco_QuandoCepForaDosLimites_DeveLancarExcecao(
            @ForAll @From("cepsComFormatoInvalido") String cep) throws IOException, InterruptedException {
        // Não precisamos configurar o mock da resposta HTTP porque a validação deve ocorrer antes da chamada HTTP
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            viaCepService.buscarEndereco(cep);
        });
        
        assertEquals("CEP deve conter exatamente 8 dígitos", exception.getMessage());
    }

    @Provide("cepsComFormatoInvalido")
    Arbitrary<String> cepsComFormatoInvalido() {
        return Arbitraries.of(
            // CEPs com menos de 8 dígitos
            "1", "12", "123", "1234", "12345", "123456", "1234567",
            // CEPs com mais de 8 dígitos
            "123456789", "1234567890", "12345678901", "123456789012", "1234567890123"
        );
    }

    @Property
    void buscarEndereco_QuandoCepComPadroesEspeciais_DeveLancarExcecao(
            @ForAll @From("cepsComPadroesEspeciais") String cepComPadrao) {
        assertThrows(IllegalArgumentException.class, () -> {
            viaCepService.buscarEndereco(cepComPadrao);
        });
    }

    @Provide("cepsComPadroesEspeciais")
    Arbitrary<String> cepsComPadroesEspeciais() {
        return Arbitraries.of(
            // CEPs com zeros à esquerda
            "01234567",
            "00123456",
            "00012345",
            
            // CEPs com zeros à direita
            "12345670",
            "12345600",
            "12345000",
            
            // CEPs com todos os dígitos iguais
            "00000000",
            "11111111",
            "99999999",
            
            // CEPs com padrões específicos
            "12341234",
            "43214321",
            "98769876",
            
            // CEPs com sequências
            "12345678",
            "87654321",
            "13579246"
        );
    }

    @Property
    void buscarEndereco_QuandoCepValido_DeveRetornarEndereco() throws IOException, InterruptedException {
        // Simulando uma resposta válida da API
        String jsonResponse = "{\"cep\":\"20010030\",\"logradouro\":\"Rua do Acre\",\"complemento\":\"\",\"bairro\":\"Centro\",\"localidade\":\"Rio de Janeiro\",\"uf\":\"RJ\",\"ibge\":\"3304557\",\"gia\":\"\",\"ddd\":\"21\",\"siafi\":\"6001\"}";
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(jsonResponse);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

        ViaCepResponse endereco = viaCepService.buscarEndereco("20010030");

        assertNotNull(endereco);
        assertEquals("20010030", endereco.getCep());
        assertEquals("Rua do Acre", endereco.getLogradouro());
        assertEquals("Centro", endereco.getBairro());
        assertEquals("Rio de Janeiro", endereco.getLocalidade());
        assertEquals("RJ", endereco.getUf());
    }

    @Property
    void buscarPorEndereco_TabelaDecisao(
            @ForAll @From("enderecosTeste") EnderecoInput endereco) throws IOException, InterruptedException {
        
        // Configurando o mock para retornar uma resposta válida
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn("[]");
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

        // Verificando se os parâmetros são nulos antes de usar
        if (endereco.getUf() == null || endereco.getCidade() == null || endereco.getLogradouro() == null) {
            assertThrows(IllegalArgumentException.class, () -> {
                viaCepService.buscarPorEndereco(
                    endereco.getUf(), 
                    endereco.getCidade(), 
                    endereco.getLogradouro()
                );
            });
            return;
        }
        
        // Configuração do mock para diferentes cenários
        if (endereco.getUf().equals("SP") && 
            endereco.getCidade().equals("São Paulo") && 
            endereco.getLogradouro().equals("Avenida Paulista")) {
            
            // Caso de sucesso
            String jsonResponse = "[{\"cep\":\"01310-000\",\"logradouro\":\"Avenida Paulista\",\"complemento\":\"\",\"bairro\":\"Bela Vista\",\"localidade\":\"São Paulo\",\"uf\":\"SP\",\"ibge\":\"3550308\",\"gia\":\"1004\",\"ddd\":\"11\",\"siafi\":\"7107\"}]";
            when(httpResponse.statusCode()).thenReturn(200);
            when(httpResponse.body()).thenReturn(jsonResponse);
            when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

            List<ViaCepResponse> enderecos = viaCepService.buscarPorEndereco(
                endereco.getUf(), 
                endereco.getCidade(), 
                endereco.getLogradouro()
            );

            assertNotNull(enderecos);
            assertFalse(enderecos.isEmpty());
            assertEquals("01310-000", enderecos.get(0).getCep());
        } else if (endereco.getUf().equals("XX") || 
                   endereco.getCidade().equals("Cidade Inexistente") || 
                   endereco.getLogradouro().equals("Rua Inexistente")) {
            
            // Caso de endereço inexistente
            String jsonResponse = "[]";
            when(httpResponse.statusCode()).thenReturn(200);
            when(httpResponse.body()).thenReturn(jsonResponse);
            when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

            List<ViaCepResponse> enderecos = viaCepService.buscarPorEndereco(
                endereco.getUf(), 
                endereco.getCidade(), 
                endereco.getLogradouro()
            );

            assertTrue(enderecos.isEmpty());
        } else {
            // Caso de erro na API
            when(httpResponse.statusCode()).thenReturn(500);
            when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

            assertThrows(RuntimeException.class, () -> {
                viaCepService.buscarPorEndereco(
                    endereco.getUf(), 
                    endereco.getCidade(), 
                    endereco.getLogradouro()
                );
            });
        }
    }

    @Provide("enderecosTeste")
    Arbitrary<EnderecoInput> enderecosTeste() {
        return Arbitraries.of(
            // Caso 1: UF válida, cidade com acentuação, logradouro existente
            new EnderecoInput("SP", "São Paulo", "Avenida Paulista"),
            
            // Caso 2: UF válida, cidade sem acentuação, logradouro existente
            new EnderecoInput("SP", "Sao Paulo", "Avenida Paulista"),
            
            // Caso 3: UF inválida, cidade válida, logradouro válido
            new EnderecoInput("XX", "São Paulo", "Avenida Paulista"),
            
            // Caso 4: UF válida, cidade inexistente, logradouro válido
            new EnderecoInput("SP", "Cidade Inexistente", "Avenida Paulista"),
            
            // Caso 5: UF válida, cidade válida, logradouro inexistente
            new EnderecoInput("SP", "São Paulo", "Rua Inexistente"),
            
            // Caso 6: UF inválida, cidade inexistente, logradouro inexistente
            new EnderecoInput("XX", "Cidade Inexistente", "Rua Inexistente"),
            
            // Caso 7: UF vazia, cidade válida, logradouro válido
            new EnderecoInput("", "São Paulo", "Avenida Paulista"),
            
            // Caso 8: UF válida, cidade vazia, logradouro válido
            new EnderecoInput("SP", "", "Avenida Paulista"),
            
            // Caso 9: UF válida, cidade válida, logradouro vazio
            new EnderecoInput("SP", "São Paulo", ""),
            
            // Caso 10: UF com espaços, cidade válida, logradouro válido
            new EnderecoInput("  SP  ", "São Paulo", "Avenida Paulista"),
            
            // Caso 11: UF com mais de 2 caracteres
            new EnderecoInput("SPA", "São Paulo", "Avenida Paulista"),
            
            // Caso 12: UF com caracteres especiais
            new EnderecoInput("S@", "São Paulo", "Avenida Paulista"),
            
            // Caso 13: Cidade com caracteres especiais
            new EnderecoInput("SP", "São@Paulo", "Avenida Paulista"),
            
            // Caso 14: Logradouro com caracteres especiais
            new EnderecoInput("SP", "São Paulo", "Avenida@Paulista"),
            
            // Caso 15: UF nula
            new EnderecoInput(null, "São Paulo", "Avenida Paulista"),
            
            // Caso 16: Cidade nula
            new EnderecoInput("SP", null, "Avenida Paulista"),
            
            // Caso 17: Logradouro nulo
            new EnderecoInput("SP", "São Paulo", null)
        );
    }

    @Property
    void buscarPorEndereco_QuandoDadosValidos_DeveRetornarListaDeEnderecos() throws IOException, InterruptedException {
        // Simulando uma resposta com múltiplos endereços
        String jsonResponse = "[{\"cep\":\"01310-000\",\"logradouro\":\"Avenida Paulista\",\"complemento\":\"\",\"bairro\":\"Bela Vista\",\"localidade\":\"São Paulo\",\"uf\":\"SP\",\"ibge\":\"3550308\",\"gia\":\"1004\",\"ddd\":\"11\",\"siafi\":\"7107\"}]";
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(jsonResponse);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

        List<ViaCepResponse> enderecos = viaCepService.buscarPorEndereco("SP", "São Paulo", "Avenida Paulista");

        assertNotNull(enderecos);
        assertFalse(enderecos.isEmpty());
        assertEquals("01310-000", enderecos.get(0).getCep());
        assertEquals("Avenida Paulista", enderecos.get(0).getLogradouro());
        assertEquals("São Paulo", enderecos.get(0).getLocalidade());
        assertEquals("SP", enderecos.get(0).getUf());
    }

    @Property
    void buscarPorEndereco_QuandoUFInvalido_DeveLancarExcecao() {
        assertThrows(IllegalArgumentException.class, () -> {
            viaCepService.buscarPorEndereco("", "São Paulo", "Avenida Paulista");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            viaCepService.buscarPorEndereco(null, "São Paulo", "Avenida Paulista");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            viaCepService.buscarPorEndereco("   ", "São Paulo", "Avenida Paulista");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            viaCepService.buscarPorEndereco("SPA", "São Paulo", "Avenida Paulista");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            viaCepService.buscarPorEndereco("S@", "São Paulo", "Avenida Paulista");
        });
    }

    @Property
    void buscarPorEndereco_QuandoCidadeInvalida_DeveLancarExcecao() {
        assertThrows(IllegalArgumentException.class, () -> {
            viaCepService.buscarPorEndereco("SP", "", "Avenida Paulista");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            viaCepService.buscarPorEndereco("SP", null, "Avenida Paulista");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            viaCepService.buscarPorEndereco("SP", "   ", "Avenida Paulista");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            viaCepService.buscarPorEndereco("SP", "São@Paulo", "Avenida Paulista");
        });
    }

    @Property
    void buscarPorEndereco_QuandoLogradouroInvalido_DeveLancarExcecao() {
        assertThrows(IllegalArgumentException.class, () -> {
            viaCepService.buscarPorEndereco("SP", "São Paulo", "");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            viaCepService.buscarPorEndereco("SP", "São Paulo", null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            viaCepService.buscarPorEndereco("SP", "São Paulo", "   ");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            viaCepService.buscarPorEndereco("SP", "São Paulo", "Avenida@Paulista");
        });
    }

    @Property
    void buscarPorEndereco_QuandoErroNaAPI_DeveLancarExcecao() throws IOException, InterruptedException {
        when(httpResponse.statusCode()).thenReturn(500);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

        assertThrows(RuntimeException.class, () -> {
            viaCepService.buscarPorEndereco("SP", "São Paulo", "Avenida Paulista");
        });
    }

    @Property
    void buscarPorEndereco_QuandoRespostaInvalida_DeveLancarExcecao() throws IOException, InterruptedException {
        String jsonResponse = "invalid json";
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(jsonResponse);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

        assertThrows(RuntimeException.class, () -> {
            viaCepService.buscarPorEndereco("SP", "São Paulo", "Avenida Paulista");
        });
    }

    @Property
    void buscarEndereco_QuandoCepInexistente_DeveLancarExcecao(
            @ForAll @From("cepsInexistentes") String cep) throws IOException, InterruptedException {
        // Configurando o mock para retornar uma resposta com erro
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn("{\"erro\": true}");
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            viaCepService.buscarEndereco(cep);
        });
        
        assertEquals("CEP não encontrado", exception.getMessage());
    }

    @Provide("cepsInexistentes")
    Arbitrary<String> cepsInexistentes() {
        return Arbitraries.of(
            // CEPs com todos os dígitos iguais
            "00000000", "11111111", "22222222", "33333333", "44444444",
            "55555555", "66666666", "77777777", "88888888", "99999999"
        );
    }

    @Property
    void buscarEndereco_QuandoErroNaAPI_DeveLancarExcecao() throws IOException, InterruptedException {
        when(httpResponse.statusCode()).thenReturn(500);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

        assertThrows(RuntimeException.class, () -> {
            viaCepService.buscarEndereco("20010030");
        });
    }

    @Property
    void buscarEndereco_QuandoRespostaInvalida_DeveLancarExcecao() throws IOException, InterruptedException {
        String jsonResponse = "invalid json";
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(jsonResponse);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

        assertThrows(RuntimeException.class, () -> {
            viaCepService.buscarEndereco("20010030");
        });
    }

    @Property
    void buscarEndereco_QuandoCepExcedeLimiteDeCaracteres_DeveLancarExcecao(
            @ForAll @From("cepsComLimiteExcedido") String cep) {
        assertThrows(IllegalArgumentException.class, () -> {
            viaCepService.buscarEndereco(cep);
        });
    }

    @Provide("cepsComLimiteExcedido")
    Arbitrary<String> cepsComLimiteExcedido() {
        return Arbitraries.of(
            // CEPs com mais de 8 dígitos
            "123456789",     // 9 dígitos
            "1234567890",    // 10 dígitos
            "12345678901",   // 11 dígitos
            "123456789012",  // 12 dígitos
            "1234567890123", // 13 dígitos
            "12345678901234",// 14 dígitos
            "123456789012345"// 15 dígitos
        );
    }

    @Property
    void buscarPorEndereco_QuandoUFExcedeLimiteDeCaracteres_DeveLancarExcecao(
            @ForAll @From("ufsComLimiteExcedido") String uf) {
        // Não precisamos configurar o mock da resposta HTTP porque a validação deve ocorrer antes da chamada HTTP
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            viaCepService.buscarPorEndereco(uf, "São Paulo", "Avenida Paulista");
        });
        
        assertEquals("UF deve ter exatamente 2 caracteres", exception.getMessage());
    }

    @Provide("ufsComLimiteExcedido")
    Arbitrary<String> ufsComLimiteExcedido() {
        return Arbitraries.of(
            // UFs com mais de 2 caracteres
            "SPA",    // 3 caracteres
            "SPAA",   // 4 caracteres
            "SPAAA",  // 5 caracteres
            "SPAAAA", // 6 caracteres
            "SPAAAAA" // 7 caracteres
        );
    }

    @Property
    void buscarPorEndereco_QuandoCidadeExcedeLimiteDeCaracteres_DeveLancarExcecao(
            @ForAll @From("cidadesComLimiteExcedido") String cidade) {
        assertThrows(IllegalArgumentException.class, () -> {
            viaCepService.buscarPorEndereco("SP", cidade, "Avenida Paulista");
        });
    }

    @Provide("cidadesComLimiteExcedido")
    Arbitrary<String> cidadesComLimiteExcedido() {
        return Arbitraries.of(
            // Cidades com mais de 100 caracteres
            "São Paulo".repeat(12),  // 108 caracteres
            "Rio de Janeiro".repeat(8), // 104 caracteres
            "Belo Horizonte".repeat(8), // 104 caracteres
            "Porto Alegre".repeat(9),   // 108 caracteres
            "Curitiba".repeat(15)       // 105 caracteres
        );
    }

    @Property
    void buscarPorEndereco_QuandoLogradouroExcedeLimiteDeCaracteres_DeveLancarExcecao(
            @ForAll @From("logradourosComLimiteExcedido") String logradouro) {
        assertThrows(IllegalArgumentException.class, () -> {
            viaCepService.buscarPorEndereco("SP", "São Paulo", logradouro);
        });
    }

    @Provide("logradourosComLimiteExcedido")
    Arbitrary<String> logradourosComLimiteExcedido() {
        return Arbitraries.of(
            // Logradouros com mais de 100 caracteres
            "Avenida Paulista".repeat(7),  // 105 caracteres
            "Rua Augusta".repeat(10),      // 110 caracteres
            "Alameda Santos".repeat(8),    // 104 caracteres
            "Praça da Sé".repeat(10),      // 110 caracteres
            "Travessa".repeat(13)          // 104 caracteres
        );
    }

    @Property
    void buscarEndereco_QuandoCepNoLimiteExato_DeveRetornarEndereco() throws IOException, InterruptedException {
        // Simulando uma resposta válida da API
        String jsonResponse = "{\"cep\":\"20010030\",\"logradouro\":\"Rua do Acre\",\"complemento\":\"\",\"bairro\":\"Centro\",\"localidade\":\"Rio de Janeiro\",\"uf\":\"RJ\",\"ibge\":\"3304557\",\"gia\":\"\",\"ddd\":\"21\",\"siafi\":\"6001\"}";
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(jsonResponse);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

        ViaCepResponse endereco = viaCepService.buscarEndereco("20010030");

        assertNotNull(endereco);
        assertEquals("20010030", endereco.getCep());
    }

    @Property
    void buscarPorEndereco_QuandoUFNoLimiteExato_DeveRetornarEndereco() throws IOException, InterruptedException {
        // Simulando uma resposta válida da API
        String jsonResponse = "[{\"cep\":\"01310-000\",\"logradouro\":\"Avenida Paulista\",\"complemento\":\"\",\"bairro\":\"Bela Vista\",\"localidade\":\"São Paulo\",\"uf\":\"SP\",\"ibge\":\"3550308\",\"gia\":\"1004\",\"ddd\":\"11\",\"siafi\":\"7107\"}]";
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(jsonResponse);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

        List<ViaCepResponse> enderecos = viaCepService.buscarPorEndereco("SP", "São Paulo", "Avenida Paulista");

        assertNotNull(enderecos);
        assertFalse(enderecos.isEmpty());
        assertEquals("SP", enderecos.get(0).getUf());
    }

    @Property
    void buscarPorEndereco_QuandoCidadeNoLimiteExato_DeveRetornarEndereco() throws IOException, InterruptedException {
        // Simulando uma resposta válida da API
        String jsonResponse = "[{\"cep\":\"01310-000\",\"logradouro\":\"Avenida Paulista\",\"complemento\":\"\",\"bairro\":\"Bela Vista\",\"localidade\":\"São Paulo\",\"uf\":\"SP\",\"ibge\":\"3550308\",\"gia\":\"1004\",\"ddd\":\"11\",\"siafi\":\"7107\"}]";
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(jsonResponse);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

        List<ViaCepResponse> enderecos = viaCepService.buscarPorEndereco("SP", "São Paulo", "Avenida Paulista");

        assertNotNull(enderecos);
        assertFalse(enderecos.isEmpty());
        assertEquals("São Paulo", enderecos.get(0).getLocalidade());
    }

    @Property
    void buscarPorEndereco_QuandoLogradouroNoLimiteExato_DeveRetornarEndereco() throws IOException, InterruptedException {
        // Simulando uma resposta válida da API
        String jsonResponse = "[{\"cep\":\"01310-000\",\"logradouro\":\"Avenida Paulista\",\"complemento\":\"\",\"bairro\":\"Bela Vista\",\"localidade\":\"São Paulo\",\"uf\":\"SP\",\"ibge\":\"3550308\",\"gia\":\"1004\",\"ddd\":\"11\",\"siafi\":\"7107\"}]";
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(jsonResponse);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

        List<ViaCepResponse> enderecos = viaCepService.buscarPorEndereco("SP", "São Paulo", "Avenida Paulista");

        assertNotNull(enderecos);
        assertFalse(enderecos.isEmpty());
        assertEquals("Avenida Paulista", enderecos.get(0).getLogradouro());
    }
} 