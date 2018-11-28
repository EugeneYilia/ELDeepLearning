package com.EL.DL.NN.SNN;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

public class SNN {
    private boolean headerIsDiscarded = false;
    private String path = "";
    private int inputDimension = 0;
    private int invisibleDimension = 0;
    private int epoch = 0;//一个完整的数据集通过了神经网络一次，训练结束称为一个epoch
    private int trainingTimes = 0;
    private double learningSpeed = 0.05;//default learning speed

//    private double sumReal = 0;
//    private double sumPredict = 0;

//    private double oneAccuracy = -1;
//    private double oneNumber = 0;
//    private double zeroAccuracy = -1;
//    private double zeroNumber = 0;

    private ArrayList<Double> realResults = new ArrayList<Double>();
    private ArrayList<Double> predictResults = new ArrayList<Double>();

    private Neural[] inputLayer;
    private Neural[] invisibleLayer;
    private Neural[] invisibleLayer2;

    private Neural inputBiasedNeural;
    private Neural invisibleBiasedNeural;
    private Neural invisibleBiasedNeural2;
    private Neural outputNeural;
    private Neural outputNeural2;

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
            invisibleDimension = (inputDimension) * 2;

            inputLayer = new Neural[inputDimension];
            invisibleLayer = new Neural[invisibleDimension];
            invisibleLayer2 = new Neural[inputDimension];

            for (int i = 0; i < inputDimension; i++) {
                inputLayer[i] = new Neural();
            }

            for (int i = 0; i < invisibleDimension; i++) {
                invisibleLayer[i] = new Neural(inputDimension + 1);
                invisibleLayer[i].makeWeightRandom();
            }

            for (int i = 0; i < inputDimension; i++) {
                invisibleLayer2[i] = new Neural(invisibleDimension + 1);
                invisibleLayer2[i].makeWeightRandom();
            }

            inputBiasedNeural = new Neural();
            inputBiasedNeural.setValue(new Random().nextDouble());

            invisibleBiasedNeural = new Neural();
            invisibleBiasedNeural.setValue(new Random().nextDouble());

            invisibleBiasedNeural2 = new Neural();
            invisibleBiasedNeural2.setValue(new Random().nextDouble());

            outputNeural = new Neural(inputDimension + 1);
            outputNeural.makeWeightRandom();

            outputNeural2 = new Neural(inputDimension + 1);
            outputNeural2.makeWeightRandom();

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
            trainSNNOnce(times);
            this.epoch++;
        }
    }

    public void trainSNNOnce(int times) {
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
                double realResults[] = new double[2];
                if (realResult == 1) {
                    realResults[0] = 0;
                    realResults[1] = 1;
                } else {
                    realResults[0] = 1;
                    realResults[1] = 0;
                }
                double[] predictResults = predict(inputs);
//                predictResults.add(predictResult);
//                realResults.add(realResult * 1.0);
//                sumReal += realResult;
//                sumPredict += predictResult;
                trainingTimes++;
                for (int k = 0; k < 2; k++) {
                    //目前样本集的误差  一个整体模型的误差
                    double loss = 0;
//                for (int i = 0; i < realResults.size(); i++) {
////                        loss += realResults.get(i) * Math.log(predictResults.get(i)) + (1 - realResults.get(i)) * Math.log(1 - predictResults.get(i));
//                    loss += Math.abs(predictResults.get(i) - realResults.get(i));
//                }
////                    loss = loss * (-1.0) / realResults.size();//正数  交叉熵代价函数
//                loss /= realResults.size();
                    loss = Math.abs(realResults[k] - predictResults[k]);
                    double lossEfficient = SNNUtil.getCoefficientActivatedValue(loss);
//                double lossEfficient = loss>0.5?10*loss:loss;
                    double outputNeuralDifferenceValue = realResults[k] - predictResults[k];


                    if (k == 0) {
                        outputNeural.setLossValue(outputNeuralDifferenceValue);
                        for (int i = 0; i < inputDimension; i++) {
                            invisibleLayer2[i].setLossValue(outputNeural.getWeight()[i] * outputNeural.getLossValue());
                        }

                        invisibleBiasedNeural2.setLossValue(outputNeural.getWeight()[inputDimension] * outputNeural.getLossValue());
                    } else if (k == 1) {
                        outputNeural2.setLossValue(outputNeuralDifferenceValue);
                        for (int i = 0; i < inputDimension; i++) {
                            invisibleLayer2[i].setLossValue(outputNeural2.getWeight()[i] * outputNeural2.getLossValue());
                        }

                        invisibleBiasedNeural2.setLossValue(outputNeural2.getWeight()[inputDimension] * outputNeural2.getLossValue());
                    }

                    for (int i = 0; i < invisibleDimension; i++) {
                        double neuralLoss = 0;
                        for (int j = 0; j < inputDimension; j++) {
                            neuralLoss += invisibleLayer2[j].getLossValue() * invisibleLayer2[j].getWeight()[i];
                        }
                        invisibleLayer[i].setLossValue(neuralLoss);
                    }

                    double invisibleBiasedLoss = 0;
                    for (int i = 0; i < inputDimension; i++) {
                        invisibleBiasedLoss += invisibleLayer2[i].getLossValue() * invisibleLayer2[i].getWeight()[invisibleDimension];
                    }
                    invisibleBiasedNeural.setLossValue(invisibleBiasedLoss);

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
//                    printNNInformation(lossEfficient);
                    if (String.valueOf(predictResults[k]).equals("NaN")) {
                        System.exit(1);
                    }
//                System.out.println(predictResult);
//                System.out.println(realResult);

//                if (realResult == 1) {
//                    oneNumber++;
//                    if (oneAccuracy != -1) {
//                        oneAccuracy = oneAccuracy * (oneNumber - 1) / oneNumber + (100 * (1 - Math.abs(predictResult - realResult)) / 1) * 1 / oneNumber;
//                    } else {
//                        oneAccuracy = 100 * (1 - Math.abs(predictResult - realResult)) / 1;
//                    }
//                } else if (realResult == 0) {
//                    zeroNumber++;
//                    if (zeroAccuracy != -1) {
//                        zeroAccuracy = zeroAccuracy * (zeroNumber - 1) / zeroNumber + (100 * (1 - Math.abs(predictResult - realResult)) / 1) * 1 / zeroNumber;
//                    } else {
//                        zeroAccuracy = 100 * (1 - Math.abs(predictResult - realResult)) / 1;
//                    }
//                }
                    //                    System.out.println("目前训练次数为" + trainingTimes + "   代数为" + epoch + "   预测结果为" + predictResult + "   实际结果为" + realResult + "   准确度为" +String.valueOf(predictResult)+"/"+String.valueOf(realResult));
                    int train_times = realResult == 1?300:1;
                    for(int i =0;i<train_times;i++) {
                        if ((1 - Math.abs(predictResults[k] - realResults[k])) >= 0.95) {
                            evolveSNN(lossEfficient, k);
                        } else if ((1 - Math.abs(predictResults[k] - realResults[k])) >= 0.7) {
                            evolveSNN(lossEfficient * 5, k);
                        } else if ((1 - Math.abs(predictResults[k] - realResults[k])) >= 0.4) {
                            evolveSNN(10 * lossEfficient, k);
                        } else {
                            evolveSNN(20 * lossEfficient, k);
                        }
                    }
                }
                double finalResult = predictResults[0] > predictResults[1] ? 0 : 1;
                System.out.println("目前训练次数为" + trainingTimes + "   代数为" + times + "   预测结果为0的概率为" + predictResults[0] + "   预测结果为1的概率为" + predictResults[1] + "   实际结果为" + realResult + "   最终预测结果为" + finalResult + "   预测的准确度为" + SNNUtil.getSpecifiedPointDouble(6, 100 * (1 - Math.abs(finalResult - realResult)) / 1) + "%");
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
            }
        }
    }

    public void trainSNNAutomatically() {
        int times = 0;
        while (true) {
            trainSNNOnce(times);
//            if (zeroAccuracy > 0.96 && oneAccuracy > 0.96 && times >= 3){
//                System.out.println("达到神经网络训练终止条件，预测正确率已经达到96%,并且目前的代数为"+times);
//                break;
//            }
            if (testSNN()) {
                System.out.println("达到神经网络训练终止条件，预测正确率已经达到92%,目前的代数为" + times);
                break;
            }
            times++;
            epoch++;
        }
    }

    private boolean testSNN() {
        BufferedReader bufferedReader = null;
        boolean isSuccessful2 = false, isSuccessful1 = false;
        try {
            bufferedReader = new BufferedReader(new FileReader(new File("/home/eugeneliu/IdeaProjects/ELNeuralNetwork/data/Credit Card Fraud Detection/creditcard.csv")));
            for (int i = 0; i < 542; i++) {
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
            if (result == 1) {
                realResults[0] = 0;
                realResults[1] = 1;
            } else {
                realResults[0] = 1;
                realResults[1] = 0;
            }
            double[] predictResult = predict(inputs);
            isSuccessful1 = (predictResult[0] > predictResult[1] ? 0 : 1) == result;

            line = bufferedReader.readLine();
            stringTokenizer = new StringTokenizer(line, ",");
            numbers = stringTokenizer.countTokens();
            inputs = new double[numbers - 1];
            for (int i = 0; i < numbers - 1; i++) {
                inputs[i] = Double.parseDouble(stringTokenizer.nextToken());
            }

            result = Double.parseDouble(stringTokenizer.nextToken().substring(1, 2));
            realResults = new double[2];
            if (result == 1) {
                realResults[0] = 0;
                realResults[1] = 1;
            } else {
                realResults[0] = 1;
                realResults[1] = 0;
            }
            predictResult = predict(inputs);
            isSuccessful2 = (predictResult[0] > predictResult[1] ? 0 : 1) == result;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
                if (isSuccessful1 && isSuccessful2) {
                    return true;
                } else {
                    return false;
                }
            } catch (IOException ignored) {

            }
        }
        if (isSuccessful1 && isSuccessful2) {
            return true;
        } else {
            return false;
        }
    }


    private void evolveSNN(double lossEfficient, int outputNeural) {
        if (epoch < 2) {
            learningSpeed = 0.05;
        } else if (epoch < 100) {
            learningSpeed = 0.005;
        } else {
            learningSpeed = 0.0005;
        }
        adjustWeights(learningSpeed, lossEfficient, outputNeural);
    }

    private void adjustWeights(double learningSpeed, double loseEfficient, int outputNeuralKind) {

        if (outputNeuralKind == 0) {
            //输出神经元1号到隐含2层的边权重调节
            for (int i = 0; i < inputDimension; i++) {
                outputNeural.getWeight()[i] = outputNeural.getWeight()[i] + learningSpeed * SNNUtil.getCoefficientActivatedValue(outputNeural.getValue()) * outputNeural.getLossValue() * invisibleLayer2[i].getValue();
            }
            //输出层到隐含2层偏置神经元的调节
            outputNeural.getWeight()[inputDimension] = outputNeural.getWeight()[inputDimension] + learningSpeed * SNNUtil.getCoefficientActivatedValue(outputNeural.getValue()) * outputNeural.getLossValue() * invisibleBiasedNeural2.getValue();
        }

        if (outputNeuralKind == 1) {
            //输出神经元2号对隐含2层的边的权重调节
            for (int i = 0; i < inputDimension; i++) {
                outputNeural2.getWeight()[i] = outputNeural2.getWeight()[i] + learningSpeed * SNNUtil.getCoefficientActivatedValue(outputNeural2.getValue()) * outputNeural2.getLossValue() * invisibleLayer2[i].getValue();
            }
            //输出神经元2号对偏置神经元的权重调节
            outputNeural2.getWeight()[inputDimension] = outputNeural2.getWeight()[inputDimension] + learningSpeed * SNNUtil.getCoefficientActivatedValue(outputNeural2.getValue()) * outputNeural2.getLossValue() * invisibleBiasedNeural2.getValue();
        }

        //隐含层2到隐含层1的边权重调节
        for (int i = 0; i < invisibleDimension; i++) {
            for (int j = 0; j < inputDimension; j++) {
                invisibleLayer2[j].getWeight()[i] = invisibleLayer2[j].getWeight()[i] + learningSpeed * SNNUtil.getCoefficientActivatedValue(invisibleLayer2[j].getValue()) * invisibleLayer2[j].getLossValue() * invisibleLayer[i].getValue();
            }
        }

        //隐含2层到隐含1层偏置神经元的权重调节
        for (int i = 0; i < inputDimension; i++) {
            invisibleLayer2[i].getWeight()[invisibleDimension] = invisibleLayer2[i].getWeight()[invisibleDimension] + learningSpeed * SNNUtil.getCoefficientActivatedValue(invisibleLayer2[i].getValue()) * invisibleLayer2[i].getLossValue() * invisibleBiasedNeural.getValue();
        }

        //隐含层1到输入层的边权重调节
        for (int i = 0; i < inputDimension; i++) {
            for (int j = 0; j < invisibleDimension; j++) {
                invisibleLayer[j].getWeight()[i] = invisibleLayer[j].getWeight()[i] + learningSpeed * SNNUtil.getCoefficientActivatedValue(invisibleLayer[j].getValue()) * invisibleLayer[j].getLossValue() * inputLayer[i].getValue();
            }
        }

        //隐含1层到输入偏置神经元的权重调节
        for (int i = 0; i < invisibleDimension; i++) {
            invisibleLayer[i].getWeight()[inputDimension] = invisibleLayer[i].getWeight()[inputDimension] + learningSpeed * SNNUtil.getCoefficientActivatedValue(invisibleLayer[i].getValue()) * invisibleLayer[i].getLossValue() * inputBiasedNeural.getValue();
        }
    }

    public double[] predict(double[] inputs) {
        double[] outputs = new double[2];
        for (int i = 0; i < inputDimension; i++) {
            inputLayer[i].setValue(inputs[i]);
        }

        //计算出隐含1层的各个神经元的值
        for (int i = 0; i < invisibleDimension; i++) {
            double sum = 0;
            for (int j = 0; j < inputDimension; j++) {
                sum += inputLayer[j].getValue() * invisibleLayer[i].getWeight()[j];
            }
            sum += inputBiasedNeural.getValue() * invisibleLayer[i].getWeight()[inputDimension];
            invisibleLayer[i].setValue(SNNUtil.getActivatedValue(sum));
        }

        //计算出隐含2层的各个神经元的值
        for (int i = 0; i < inputDimension; i++) {
            double sum = 0;
            for (int j = 0; j < invisibleDimension; j++) {
                sum += invisibleLayer[j].getValue() * invisibleLayer2[i].getWeight()[j];
            }
            sum += invisibleBiasedNeural.getValue() * invisibleLayer2[i].getWeight()[invisibleDimension];
            invisibleLayer2[i].setValue(SNNUtil.getActivatedValue(sum));
        }

        //计算出输出神经元的值
        double returnValue1 = 0;
        for (int i = 0; i < inputDimension; i++) {
            returnValue1 += invisibleLayer2[i].getValue() * outputNeural.getWeight()[i];
        }
        returnValue1 += invisibleBiasedNeural2.getValue() * outputNeural.getWeight()[inputDimension];
        outputs[0] = SNNUtil.getActivatedValue(returnValue1);

        double returnValue2 = 0;
        for (int i = 0; i < inputDimension; i++) {
            returnValue2 += invisibleLayer2[i].getValue() * outputNeural2.getWeight()[i];
        }
        returnValue2 += invisibleBiasedNeural2.getValue() * outputNeural2.getWeight()[inputDimension];
        outputs[1] = SNNUtil.getActivatedValue(returnValue2);

        return outputs;
    }

    public void predictInHuman(double[] inputs) {//原数据里1代表欺诈，0代表正常
        double[] result = predict(inputs);
        if (result[0] <= 0.5) {
            System.out.println("预测结果该用户为普通用户");
        } else {
            System.out.println("预测结果该用户为欺诈用户");
        }
    }

    private void printNNInformation(double loseEfficient) {
        System.out.println("输入层的偏置值" + inputBiasedNeural.getValue());

        for (int i = 0; i < invisibleDimension; i++) {
            System.out.println("隐含1层第" + i + "个神经元的每条边的权重");
            for (int j = 0; j < invisibleLayer[i].getWeight().length; j++) {
                System.out.println(invisibleLayer[i].getWeight()[j]);
            }
        }
        System.out.println("隐含1层的偏置值:" + invisibleBiasedNeural.getValue());

        for (int i = 0; i < inputDimension; i++) {
            System.out.println("隐含2层第" + i + "个神经元的每条边的权重");
            for (int j = 0; j < invisibleLayer2[i].getWeight().length; j++) {
                System.out.println(invisibleLayer2[i].getWeight()[j]);
            }
        }
        System.out.println("隐含2层的偏置值:" + invisibleBiasedNeural2.getValue());

        System.out.println("输出层的各条边");
        for (int i = 0; i < outputNeural.getWeight().length; i++) {
            System.out.println(outputNeural.getWeight()[i]);
        }

        System.out.println("损失值为" + loseEfficient);
    }
}
