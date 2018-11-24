package com.EL.DL.NN.SNN;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

import static java.lang.Float.NaN;

public class SNN {
    private boolean headerIsDiscarded = false;
    private String path = "";
    private int inputDimension = 0;
    private int invisibleDimension = 0;
    private int epoch = 0;//一个完整的数据集通过了神经网络一次，训练结束称为一个epoch
    private int trainingTimes = 0;
    private double learningSpeed = 0.05;//default learning speed

    private double sumReal = 0;
    private double sumPredict = 0;

    private ArrayList<Double> realResults = new ArrayList<Double>();
    private ArrayList<Double> predictResults = new ArrayList<Double>();

    private Neural[] inputLayer;
    private Neural[] invisibleLayer;
    private Neural inputBiasedNeural;
    private Neural invisibleBiasedNeural;
    private Neural outputNeural;

    public SNN setSource(String path) {
        this.path = path;
        return this;
    }

    public SNN dropHeader(boolean isDiscarded) {
        this.headerIsDiscarded = isDiscarded;
        return this;
    }

    public SNN buildSNN() {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(new File(path)));
            String line = bufferedReader.readLine();
            StringTokenizer stringTokenizer = new StringTokenizer(line, ",");
            inputDimension = stringTokenizer.countTokens() - 1;
            invisibleDimension = (inputDimension + 1) / 2;

            inputLayer = new Neural[inputDimension];
            invisibleLayer = new Neural[invisibleDimension];

            for (int i = 0; i < inputDimension; i++) {
                inputLayer[i] = new Neural();
            }

            for (int i = 0; i < invisibleDimension; i++) {
                invisibleLayer[i] = new Neural(inputDimension + 1);
                invisibleLayer[i].makeWeightRandom();
            }

            inputBiasedNeural = new Neural();
            inputBiasedNeural.setValue(new Random().nextDouble());

            invisibleBiasedNeural = new Neural();
            invisibleBiasedNeural.setValue(new Random().nextDouble());

            outputNeural = new Neural(invisibleDimension + 1);
            outputNeural.makeWeightRandom();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ignored) {

                }
            }
        }
        return this;
    }

    public void trainSNN(int epoch) {
        for (int times = 0; times < epoch; times++) {
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new FileReader(new File(path)));
                String line = bufferedReader.readLine();
                if (headerIsDiscarded) {
                    line = bufferedReader.readLine();
                }
                while (line != null) {
                    double inputs[] = new double[inputDimension];
                    StringTokenizer stringTokenizer = new StringTokenizer(line, ",");
                    for (int i = 0; i < inputDimension; i++) {
                        inputs[i] = Double.parseDouble(stringTokenizer.nextToken());
                    }
                    String result = stringTokenizer.nextToken();
                    int realResult = Integer.parseInt(result.substring(result.indexOf("\"") + 1, result.lastIndexOf("\"")));
                    double predictResult = predict(inputs);
                    predictResults.add(predictResult);
                    realResults.add(realResult * 1.0);
                    sumReal += realResult;
                    sumPredict += predictResult;
                    trainingTimes++;
                    //目前样本集的误差  一个整体模型的误差
                    double loss = 0;
                    for (int i = 0; i < realResults.size(); i++) {
//                        loss += realResults.get(i) * Math.log(predictResults.get(i)) + (1 - realResults.get(i)) * Math.log(1 - predictResults.get(i));
                        loss += Math.abs(predictResults.get(i) - realResults.get(i));
                    }
//                    loss = loss * (-1.0) / realResults.size();//正数  交叉熵代价函数
                    loss /= realResults.size();
                    double lossEfficient = SNNUtil.getCoefficientActivatedValue(loss);

                    double outputNeuralDifferenceValue = realResult - predictResult;
                    outputNeural.setLossValue(outputNeuralDifferenceValue);

                    for (int i = 0; i < invisibleDimension; i++) {
                        invisibleLayer[i].setLossValue(outputNeural.getWeight()[i] * outputNeural.getLossValue());
                    }

                    invisibleBiasedNeural.setLossValue(outputNeural.getWeight()[invisibleDimension] * outputNeural.getLossValue());

                    for (int i = 0; i < inputDimension; i++) {
                        double neuralLoss = 0;
                        for (int j = 0; j < invisibleDimension; j++) {
                            neuralLoss += invisibleLayer[j].getLossValue() * invisibleLayer[j].getWeight()[i];
                        }
                        inputLayer[i].setLossValue(neuralLoss);
                    }

                    double neuralLoss = 0;
                    for (int i = 0; i < invisibleDimension; i++) {
                        neuralLoss += invisibleLayer[i].getLossValue() * invisibleLayer[i].getWeight()[inputDimension];
                    }
                    inputBiasedNeural.setLossValue(neuralLoss);
                    printNNInformation(lossEfficient);
                    if (String.valueOf(predictResult).equals("NaN")){
                        System.exit(1);
                    }
                    System.out.println("目前训练次数为" + trainingTimes + "   代数为" + times + "   预测结果为" + predictResult + "   实际结果为" + realResult + "   准确度为" + SNNUtil.getSpecifiedPointDouble(4, 100 * (1-Math.abs(predictResult-realResult)) / 1) + "%");
//                    System.out.println("目前训练次数为" + trainingTimes + "   代数为" + epoch + "   预测结果为" + predictResult + "   实际结果为" + realResult + "   准确度为" +String.valueOf(predictResult)+"/"+String.valueOf(realResult));

                    evolveSNN(lossEfficient);

                    line = bufferedReader.readLine();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException ignored) {
                    }
                    epoch = times;
                }
            }
        }
    }

    private void evolveSNN(double lossEfficient) {
        if (epoch == 1) {
            learningSpeed = 0.05;
        } else if (epoch < 10) {
            learningSpeed = 0.005;
        } else {
            learningSpeed = 0.0005;
        }
        adjustWeights(learningSpeed, lossEfficient);
    }

    private void adjustWeights(double learningSpeed, double loseEfficient) {
        //输出层到隐含层的边权重调节
        for (int i = 0; i < invisibleDimension; i++) {
            outputNeural.getWeight()[i] = outputNeural.getWeight()[i] + learningSpeed * loseEfficient * outputNeural.getLossValue() * invisibleLayer[i].getValue();
        }

        //输出层到隐含层偏置神经元的调节
        outputNeural.getWeight()[invisibleDimension] = outputNeural.getWeight()[invisibleDimension] + learningSpeed * loseEfficient * outputNeural.getLossValue() * invisibleBiasedNeural.getValue();

        //隐含层到输入层的边权重调节
        for (int i = 0; i < inputDimension; i++) {
            for (int j = 0; j < invisibleDimension; j++) {
                invisibleLayer[j].getWeight()[i] = invisibleLayer[j].getWeight()[i] + learningSpeed * loseEfficient * invisibleLayer[j].getLossValue() * inputLayer[i].getValue();
            }
        }

        //隐含层到输入偏置神经元的权重调节
        for (int i = 0; i < invisibleDimension; i++) {
            invisibleLayer[i].getWeight()[inputDimension] = invisibleLayer[i].getWeight()[inputDimension] + learningSpeed * loseEfficient * invisibleLayer[i].getLossValue() * inputBiasedNeural.getValue();
        }
    }

    public double predict(double[] inputs) {
        for (int i = 0; i < inputDimension; i++) {
            inputLayer[i].setValue(inputs[i]);
        }

        for (int i = 0; i < invisibleDimension; i++) {
            double sum = 0;
            for (int j = 0; j < inputDimension; j++) {
                sum += inputLayer[i].getValue() * invisibleLayer[i].getWeight()[j];
            }
            sum += inputBiasedNeural.getValue() * invisibleLayer[i].getWeight()[inputDimension];
            invisibleLayer[i].setValue(SNNUtil.getActivatedValue(sum));
        }

        double returnValue = 0;
        for (int i = 0; i < invisibleDimension; i++) {
            returnValue += invisibleLayer[i].getValue() * outputNeural.getWeight()[i];
        }
        returnValue += invisibleBiasedNeural.getValue() * outputNeural.getWeight()[invisibleDimension];

        return SNNUtil.getActivatedValue(returnValue);
    }

    public void predictInHuman(double[] inputs) {//原数据里1代表欺诈，0代表正常
        double result = predict(inputs);
        if (result < 0.5) {
            System.out.println("预测结果该用户为普通用户");
        } else {
            System.out.println("预测结果该用户为欺诈用户");
        }
    }

    private void printNNInformation(double loseEfficient){
        System.out.println("输入层的偏置值"+inputBiasedNeural.getValue());

        for(int i = 0;i<invisibleDimension;i++){
            System.out.println("隐含层第"+i+"个神经元的每条边的权重");
            for(int j = 0;j<invisibleLayer[i].getWeight().length;j++){
                System.out.println(invisibleLayer[i].getWeight()[j]);
            }
        }

        System.out.println("隐含层的偏置值:"+invisibleBiasedNeural.getValue());

        System.out.println("输出层的各条边");
        for(int i = 0;i<outputNeural.getWeight().length;i++){
            System.out.println(outputNeural.getWeight()[i]);
        }

        System.out.println("损失值为"+loseEfficient);
    }
}
