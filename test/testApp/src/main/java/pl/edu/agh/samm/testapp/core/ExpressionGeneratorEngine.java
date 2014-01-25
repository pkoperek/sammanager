package pl.edu.agh.samm.testapp.core;

import java.io.Serializable;

public class ExpressionGeneratorEngine implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8757182281106691496L;

	public static void main(String[] args) {
		System.out.println(new ExpressionGeneratorEngine()
				.generateExpression(15));
	}

	private int lvl = 0;
	private int maxLvl = -1;

	private int generateRandomIntMod(int mod) {
		return generateRandomInt() % mod;
	}

	private int generateRandomInt() {
		double random = Math.random();
		int randomInt = (int) (random * Integer.MAX_VALUE);
		return randomInt;
	}

	public String generateExpression(int maxLvl) {
		lvl = 0;
		this.maxLvl = maxLvl;
		String expression = generateExpr();
		return expression;
	}

	private String generateExpr() {
		lvl++;
		String retVal = null;

		int res = generateRandomInt() % 3;

		// if we're bounded by the max lvl - go straight to finishing option
		if (lvl >= maxLvl) {
			res = 0;
		}

		if (res == 0) {
			retVal = generateMulDiv();
		} else if (res == 1) {
			retVal = generateExpr() + " + " + generateExpr();
		} else if (res == 2) {
			retVal = generateExpr() + " - " + generateExpr();
		}

		return retVal;
	}

	private String generateMulDiv() {
		lvl++;
		String retVal = null;

		int res = generateRandomInt() % 3;

		// if we're bounded by the max lvl - go straight to finishing option
		if (lvl >= maxLvl) {
			res = 0;
		}

		if (res == 0) {
			retVal = generateFunc();
		} else if (res == 1) {
			retVal = generateMulDiv() + " / " + generateMulDiv();
		} else if (res == 2) {
			retVal = generateMulDiv() + " * " + generateMulDiv();
		}

		return retVal;
	}

	private String generateFunc() {
		lvl++;
		String retVal = null;

		int res = generateRandomInt() % 2;

		// if we're bounded by the max lvl - go straight to finishing option
		if (lvl >= maxLvl) {
			res = 0;
		}

		switch (res) {
		default:
		case 0:
			retVal = "x";
			break;
		// case 1:
		// retVal = "Math.cos(" + generateFunc() + ")";
		// break;
		// case 2:
		// retVal = "Math.sin(" + generateFunc() + ")";
		// break;
		case 1:
			retVal = "" + generateRandomIntMod(100);
			break;
		}

		return retVal;
	}
	/*
	 * Grammar: Expr: MulDiv | Expr '+' Expr | Expr '-' Expr MulDiv: MulDiv *
	 * MulDiv | MulDiv / MulDiv | Func Func: Math.sin(Func) | Math.cos(Func) | x
	 * | const
	 * 
	 * Priorities: * Math.sin | Math.cos | x | const * *|/ * +|-
	 */
}
