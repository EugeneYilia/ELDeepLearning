package com.EL.DL.NN;

import com.EL.DL.NN.SNN.SNN;

import java.io.*;
import java.util.StringTokenizer;

public class NNBootstrap {
    public static void main(String[] args) {
        SNN snn = new SNN();
        snn.setSource("/home/eugeneliu/IdeaProjects/ELNeuralNetwork/data/Credit Card Fraud Detection/creditcard.csv")
                .dropHeader(true)
                .buildSNN()
                .trainSNN(10);
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(new File("/home/eugeneliu/IdeaProjects/ELNeuralNetwork/data/Credit Card Fraud Detection/creditcard.csv")));
            bufferedReader.readLine();
            String line = bufferedReader.readLine();
            StringTokenizer stringTokenizer = new StringTokenizer(line, ",");
            int numbers = stringTokenizer.countTokens();
            double[] inputs = new double[numbers - 1];
            for (int i = 0; i < numbers - 1; i++) {
                inputs[i] = Double.parseDouble(stringTokenizer.nextToken());
            }
            double result = Double.parseDouble(stringTokenizer.nextToken().substring(1, 2));
            double predictResult = snn.predict(inputs);
            snn.predictInHuman(inputs);
            System.out.println("训练后的神经网络的预测结果为" + predictResult + "   " + "实际结果为" + result);
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
