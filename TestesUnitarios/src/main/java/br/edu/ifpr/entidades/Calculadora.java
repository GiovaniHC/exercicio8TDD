package br.edu.ifpr.entidades;

import br.edu.ifpr.exceptions.DivisaoPorZeroException;

public class Calculadora {

	public int somar(int num1, int num2) {
		return num1 + num2;
	}

	public int subtrair(int num1, int num2) {
		return num1 - num2;
	}

	public int dividir(int num1, int num2) throws DivisaoPorZeroException {
		
		if(num2 == 0) {
			throw new DivisaoPorZeroException("nao permitido divisoes por zero");
		}
		
		return num1 / num2;
	}

}
