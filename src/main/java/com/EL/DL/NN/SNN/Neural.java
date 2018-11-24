package com.EL.DL.NN.SNN;

import java.util.Random;

public class Neural {
    private double value;
    private double lossValue;
    private double weight[];

    public Neural(){}

    public Neural(int connectionsNumber){
        weight = new double[connectionsNumber];
    }

    public Neural(double weight[]){
        this.weight = weight;
    }

    public void makeWeightRandom(){
        for(int i =0;i<weight.length;i++){
            weight[i] = (new Random().nextDouble()) * weight.length/ Math.sqrt(2.0 / weight.length);
        }
    }

    public double[] getWeight() {
        return weight;
    }

    public void setWeight(double[] weight) {
        this.weight = weight;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getLossValue() {
        return lossValue;
    }

    public void setLossValue(double lossValue) {
        this.lossValue = lossValue;
    }
}
