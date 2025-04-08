package br.edu.infnet.viacep;

public class EnderecoInput {
    private final String uf;
    private final String cidade;
    private final String logradouro;

    public EnderecoInput(String uf, String cidade, String logradouro) {
        this.uf = uf;
        this.cidade = cidade;
        this.logradouro = logradouro;
    }

    public String getUf() {
        return uf;
    }

    public String getCidade() {
        return cidade;
    }

    public String getLogradouro() {
        return logradouro;
    }
} 