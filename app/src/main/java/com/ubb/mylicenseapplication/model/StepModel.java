package com.ubb.mylicenseapplication.model;

public class StepModel {


    private Integer ID;
    private Integer steps;

    public StepModel(Integer ID, Integer steps) {
        this.ID = ID;
        this.steps = steps;
    }

    public StepModel(){}

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getSteps() {
        return steps;
    }

    public void setSteps(Integer steps) {
        this.steps = steps;
    }

    @Override
    public String toString() {
        return "StepModel{" +
                "ID=" + ID +
                ", steps=" + steps +
                '}';
    }
}
