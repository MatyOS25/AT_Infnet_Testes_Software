package br.edu.infnet;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculoIMCTest {

    @Test
    void testClassificarIMC_MagrezaGrave() {
        // Teste de valor limite inferior
        assertEquals("Magreza grave", CalculoIMC.classificarIMC(15.9));
        // Teste de valor dentro da partição
        assertEquals("Magreza grave", CalculoIMC.classificarIMC(10.0));
    }

    @Test
    void testClassificarIMC_MagrezaModerada() {
        // Teste de valor limite inferior
        assertEquals("Magreza moderada", CalculoIMC.classificarIMC(16.0));
        // Teste de valor dentro da partição
        assertEquals("Magreza moderada", CalculoIMC.classificarIMC(16.5));
        // Teste de valor limite superior
        assertEquals("Magreza moderada", CalculoIMC.classificarIMC(16.9));
    }

    @Test
    void testClassificarIMC_MagrezaLeve() {
        // Teste de valor limite inferior
        assertEquals("Magreza leve", CalculoIMC.classificarIMC(17.0));
        // Teste de valor dentro da partição
        assertEquals("Magreza leve", CalculoIMC.classificarIMC(17.5));
        // Teste de valor limite superior
        assertEquals("Magreza leve", CalculoIMC.classificarIMC(18.4));
    }

    @Test
    void testClassificarIMC_Saudavel() {
        // Teste de valor limite inferior
        assertEquals("Saudável", CalculoIMC.classificarIMC(18.5));
        // Teste de valor dentro da partição
        assertEquals("Saudável", CalculoIMC.classificarIMC(22.0));
        // Teste de valor limite superior
        assertEquals("Saudável", CalculoIMC.classificarIMC(24.9));
    }

    @Test
    void testClassificarIMC_Sobrepeso() {
        // Teste de valor limite inferior
        assertEquals("Sobrepeso", CalculoIMC.classificarIMC(25.0));
        // Teste de valor dentro da partição
        assertEquals("Sobrepeso", CalculoIMC.classificarIMC(27.5));
        // Teste de valor limite superior
        assertEquals("Sobrepeso", CalculoIMC.classificarIMC(29.9));
    }

    @Test
    void testClassificarIMC_ObesidadeGrauI() {
        // Teste de valor limite inferior
        assertEquals("Obesidade Grau I", CalculoIMC.classificarIMC(30.0));
        // Teste de valor dentro da partição
        assertEquals("Obesidade Grau I", CalculoIMC.classificarIMC(32.5));
        // Teste de valor limite superior
        assertEquals("Obesidade Grau I", CalculoIMC.classificarIMC(34.9));
    }

    @Test
    void testClassificarIMC_ObesidadeGrauII() {
        // Teste de valor limite inferior
        assertEquals("Obesidade Grau II", CalculoIMC.classificarIMC(35.0));
        // Teste de valor dentro da partição
        assertEquals("Obesidade Grau II", CalculoIMC.classificarIMC(37.5));
        // Teste de valor limite superior
        assertEquals("Obesidade Grau II", CalculoIMC.classificarIMC(39.9));
    }

    @Test
    void testClassificarIMC_ObesidadeGrauIII() {
        // Teste de valor limite inferior
        assertEquals("Obesidade Grau III", CalculoIMC.classificarIMC(40.0));
        // Teste de valor dentro da partição
        assertEquals("Obesidade Grau III", CalculoIMC.classificarIMC(45.0));
    }

    @Test
    void testCalcularPeso() {
        // Teste com valores normais
        assertEquals(25.0, CalculoIMC.calcularPeso(75.0, 1.73), 0.01);
        
        // Teste com valores extremos
        assertEquals(100.0, CalculoIMC.calcularPeso(100.0, 1.0), 0.01);
        assertEquals(25.0, CalculoIMC.calcularPeso(25.0, 1.0), 0.01);
    }
} 