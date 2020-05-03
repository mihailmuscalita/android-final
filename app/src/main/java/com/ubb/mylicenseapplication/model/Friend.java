package com.ubb.mylicenseapplication.model;

public class Friend {

    private String friendName;

    public Friend(String friendName) {
        this.friendName = friendName;
    }

    public Friend(){}

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }


    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Friend){
            Friend element = (Friend) obj;
            if(element != null && this.friendName.equals(element.friendName)){
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "friendName='" + friendName + '\'' +
                '}';
    }
}
