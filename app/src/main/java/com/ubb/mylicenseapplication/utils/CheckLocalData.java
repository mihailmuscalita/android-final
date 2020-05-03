package com.ubb.mylicenseapplication.utils;

public class CheckLocalData {

    public static boolean comparePasswords(String firstPassword, String secondPassword){
        return firstPassword.equals(secondPassword);
    }

    public static boolean checkLenght(String userName, String password, String name, String email){

        if (userName.length() >100 || password.length() >100 || name.length() > 100 || email.length() >100){
            return false;
        }
        return true;
    }

    public static boolean checkEmail(String email){
        if (email.contains("@yahoo.com") || email.contains("@gmail.com")){
            return true;
        }
        return false;
    }

    public static boolean checkEmpty(String userName, String password, String name, String email){
        if (!userName.equals("") && !password.equals("") && !name.equals("") && !email.equals("")){
            return true;
        }
        return false;
    }

}
