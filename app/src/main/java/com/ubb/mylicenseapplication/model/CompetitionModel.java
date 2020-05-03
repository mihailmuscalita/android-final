package com.ubb.mylicenseapplication.model;

import java.io.Serializable;

public class CompetitionModel implements Serializable {

    private Integer competitionId;
    private String competitionTitle;
    private String competitionReward;
    private String isRegistered;

    public CompetitionModel(Integer idCompetition, String competitionTitle, String competitionReward,String isRegistered) {
        this.competitionId = idCompetition;
        this.competitionTitle = competitionTitle;
        this.competitionReward = competitionReward;
        this.isRegistered = isRegistered;
    }

    public CompetitionModel(){}

    public Integer getIdCompetition() {
        return competitionId;
    }

    public void setIdCompetition(Integer idCompetition) {
        this.competitionId = idCompetition;
    }

    public String getCompetitionTitle() {
        return competitionTitle;
    }

    public void setCompetitionTitle(String competitionTitle) {
        this.competitionTitle = competitionTitle;
    }

    public String getCompetitionReward() {
        return competitionReward;
    }

    public void setCompetitionReward(String competitionReward) {
        this.competitionReward = competitionReward;
    }

    public String getIsRegistered() {
        return isRegistered;
    }

    public void setIsRegistered(String isRegistered) {
        this.isRegistered = isRegistered;
    }

    @Override
    public String toString() {
        return "{" +
                "competitionId=" + competitionId +
                ", competitionTitle='" + competitionTitle + '\'' +
                ", competitionReward='" + competitionReward + '\'' +
                ", isRegistered='" + isRegistered + '\'' +
                '}';
    }
}
