package com.EL.DL.NN;

import com.EL.DL.NN.SNN.SNN;
import com.EL.DL.NN.SNN.SNNUtil;

import java.io.*;
import java.util.StringTokenizer;

public class NNBootstrap {
    public static void main(String[] args) {
        SNN snn = new SNN();
        snn.setSource("/home/eugeneliu/IdeaProjects/ELNeuralNetwork/data/Credit Card Fraud Detection/creditcard.csv")
                .dropHeader(true)
                .buildSNN()
                .trainSNNAutomatically();
//                .trainSNN(10);
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(new File("/home/eugeneliu/IdeaProjects/ELNeuralNetwork/data/Credit Card Fraud Detection/creditcard.csv")));
            for(int i = 0 ;i<542;i++){
                bufferedReader.readLine();
            }
            String line = bufferedReader.readLine();
            StringTokenizer stringTokenizer = new StringTokenizer(line, ",");
            int numbers = stringTokenizer.countTokens();
            double[] inputs = new double[numbers - 1];
            for (int i = 0; i < numbers - 1; i++) {
                inputs[i] = Double.parseDouble(stringTokenizer.nextToken());
            }
            double result = Double.parseDouble(stringTokenizer.nextToken().substring(1, 2));
            double[] realResults = new double[2];
            if(result == 1){
                realResults[0] = 0;
                realResults[1] = 1;
            } else {
                realResults[0] = 1;
                realResults[1] = 0;
            }
            double[] predictResult = snn.predict(inputs);
            snn.predictInHuman(inputs);
            double finalResult = predictResult[0]>predictResult[1]?0:1;

            System.out.println("训练后的神经网络的预测结果为" + finalResult + "   " + "实际结果为" + result);
            line = bufferedReader.readLine();
            stringTokenizer = new StringTokenizer(line, ",");
            numbers = stringTokenizer.countTokens();
            inputs = new double[numbers - 1];
            for (int i = 0; i < numbers - 1; i++) {
                inputs[i] = Double.parseDouble(stringTokenizer.nextToken());
            }
            result = Double.parseDouble(stringTokenizer.nextToken().substring(1, 2));
            realResults = new double[2];
            if(result == 1){
                realResults[0] = 0;
                realResults[1] = 1;
            } else {
                realResults[0] = 1;
                realResults[1] = 0;
            }
            predictResult = snn.predict(inputs);
            snn.predictInHuman(inputs);
            finalResult = predictResult[0]>predictResult[1]?0:1;

            System.out.println("训练后的神经网络的预测结果为" + finalResult + "   " + "实际结果为" + result);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException ignored) {

            }
        }

    }
}
