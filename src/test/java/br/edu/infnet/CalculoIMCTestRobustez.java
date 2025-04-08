package br.edu.infnet;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculoIMCTestRobustez {

    @Test
    void testEntradaInvalida_NumerosNegativos() {
        // Teste com peso negativo
        assertThrows(NumberFormatException.class, () -> {
            CalculoIMC.programaIMC("1.0");
            System.setIn(new java.io.ByteArrayInputStream("-70\n1.75\n".getBytes()));
        });

        // Teste com altura negativa
        assertThrows(NumberFormatException.class, () -> {
            CalculoIMC.programaIMC("1.0");
            System.setIn(new java.io.ByteArrayInputStream("70\n-1.75\n".getBytes()));
        });
    }

    @Test
    void testEntradaInvalida_Letras() {
        // Teste com letras no peso
        assertThrows(NumberFormatException.class, () -> {
            CalculoIMC.programaIMC("1.0");
            System.setIn(new java.io.ByteArrayInputStream("abc\n1.75\n".getBytes()));
        });

        // Teste com letras na altura
        assertThrows(NumberFormatException.class, () -> {
            CalculoIMC.programaIMC("1.0");
            System.setIn(new java.io.ByteArrayInputStream("70\nxyz\n".getBytes()));
        });
    }

    @Test
    void testEntradaInvalida_CaracteresEspeciais() {
        // Teste com caracteres especiais no peso
        assertThrows(NumberFormatException.class, () -> {
            CalculoIMC.programaIMC("1.0");
            System.setIn(new java.io.ByteArrayInputStream("@#$%\n1.75\n".getBytes()));
        });

        // Teste com caracteres especiais na altura
        assertThrows(NumberFormatException.class, () -> {
            CalculoIMC.programaIMC("1.0");
            System.setIn(new java.io.ByteArrayInputStream("70\n!@#\n".getBytes()));
        });
    }

    @Test
    void testEntradaInvalida_NumerosMuitoAltos() {
        // Teste com peso extremamente alto
        assertThrows(NumberFormatException.class, () -> {
            CalculoIMC.programaIMC("1.0");
            System.setIn(new java.io.ByteArrayInputStream("999999\n1.75\n".getBytes()));
        });

        // Teste com altura extremamente alta
        assertThrows(NumberFormatException.class, () -> {
            CalculoIMC.programaIMC("1.0");
            System.setIn(new java.io.ByteArrayInputStream("70\n999\n".getBytes()));
        });
    }

    @Test
    void testEntradaInvalida_NumerosComVirgula() {
        // Teste com vírgula no peso
        assertThrows(NumberFormatException.class, () -> {
            CalculoIMC.programaIMC("1.0");
            System.setIn(new java.io.ByteArrayInputStream("70,5\n1.75\n".getBytes()));
        });

        // Teste com vírgula na altura
        assertThrows(NumberFormatException.class, () -> {
            CalculoIMC.programaIMC("1.0");
            System.setIn(new java.io.ByteArrayInputStream("70\n1,75\n".getBytes()));
        });
    }

    @Test
    void testEntradaInvalida_ValoresZerados() {
        // Teste com peso zero
        assertThrows(NumberFormatException.class, () -> {
            CalculoIMC.programaIMC("1.0");
            System.setIn(new java.io.ByteArrayInputStream("0\n1.75\n".getBytes()));
        });

        // Teste com altura zero
        assertThrows(NumberFormatException.class, () -> {
            CalculoIMC.programaIMC("1.0");
            System.setIn(new java.io.ByteArrayInputStream("70\n0\n".getBytes()));
        });
    }

    @Test
    void testEntradaInvalida_ValoresMuitoPequenos() {
        // Teste com peso muito pequeno
        assertThrows(NumberFormatException.class, () -> {
            CalculoIMC.programaIMC("1.0");
            System.setIn(new java.io.ByteArrayInputStream("0.0001\n1.75\n".getBytes()));
        });

        // Teste com altura muito pequena
        assertThrows(NumberFormatException.class, () -> {
            CalculoIMC.programaIMC("1.0");
            System.setIn(new java.io.ByteArrayInputStream("70\n0.0001\n".getBytes()));
        });
    }

    @Test
    void testEntradaInvalida_ValoresComEspacos() {
        // Teste com espaços no peso
        assertThrows(NumberFormatException.class, () -> {
            CalculoIMC.programaIMC("1.0");
            System.setIn(new java.io.ByteArrayInputStream(" 70 \n1.75\n".getBytes()));
        });

        // Teste com espaços na altura
        assertThrows(NumberFormatException.class, () -> {
            CalculoIMC.programaIMC("1.0");
            System.setIn(new java.io.ByteArrayInputStream("70\n 1.75 \n".getBytes()));
        });
    }
} 