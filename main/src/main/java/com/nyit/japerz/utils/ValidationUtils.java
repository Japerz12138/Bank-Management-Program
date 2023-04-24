package com.nyit.japerz.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {

    public static boolean dateChecker(int month, int day, int year) {
        if (year < 0 || month < 1 || month > 12 || day < 1) {
            return false;
        }

        int daysInMonth;
        switch (month) {
            case 2:
                daysInMonth = ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) ? 29 : 28;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                daysInMonth = 30;
                break;
            default:
                daysInMonth = 31;
        }

        return day <= daysInMonth;
    }

    public static boolean emailChecker(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}