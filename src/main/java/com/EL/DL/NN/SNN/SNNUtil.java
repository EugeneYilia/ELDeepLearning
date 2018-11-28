package com.EL.DL.NN.SNN;


import static java.lang.Math.E;

public class SNNUtil {
    public static double getActivatedValue(double input) {//sigmoid函数
        return (1/(1+Math.pow(E,-input)));
//        if (input >= 0) {
//            return input;
//        } else {
//            return 0.1 * input;
//        }
    }

    public static double getCoefficientActivatedValue(double input) {//sigmoid函数的导数
//        if (input >= 0) {
//            return 1 / input;
//        } else {
//            return 0.1 / input;
//        }
                return getActivatedValue(input) * (1 - getActivatedValue(input));
    }

    public static double getSpecifiedPointDouble(int point, double number) {
//        System.out.println(number);
        int intPart = (int) Math.floor(number);
        String decimalPart = String.valueOf(number).replaceAll("\\d\\.", "");
        if (decimalPart.length() > 4) {
            String returnDecimal = decimalPart.substring(0, point);
            String completeReturnPart = intPart + "." + returnDecimal;
            return Double.parseDouble(completeReturnPart);
        } else {
            return number;
        }
    }

//    public static double getLossValue(double fpValue,double bpValue){
//
//    }
}
