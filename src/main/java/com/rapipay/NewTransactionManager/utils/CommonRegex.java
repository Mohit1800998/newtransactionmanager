package com.rapipay.NewTransactionManager.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonRegex {

    private static Pattern pattern;
    private static Matcher matcher;
    private static String regex="";

    private static final String alphaNumeric="^[a-zA-Z0-9]{1,300}$";
    private static final String dateRegex="^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[1,3-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$";
    private static final String integerValidation="^[0-9]*[1-9][0-9]*$";

    public static boolean validateAlphaNumeric(String value) {
        pattern = Pattern.compile(alphaNumeric, Pattern.CASE_INSENSITIVE);
        matcher =pattern.matcher(value);
        return matcher.matches();
    }
    public static boolean validateDateFormat(String value) {
        pattern = Pattern.compile(dateRegex, Pattern.CASE_INSENSITIVE);
        matcher =pattern.matcher(value);
        return matcher.matches();
    }
    public static boolean validateInteger(String value) {
        pattern = Pattern.compile(integerValidation, Pattern.CASE_INSENSITIVE);
        matcher =pattern.matcher(value);
        return matcher.matches();
    }

    public static boolean validateAlphaNumericVariablelen(String value, int len) {
        regex = "^[0-9a-zA-Z]{1,"+len+"}$";
        pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        matcher =pattern.matcher(value);
        return matcher.matches();
    }

    public static boolean validateAlphaNumSpclSymblFixedlen(String value, String spclchar, int len) {
        regex = "^[a-zA-Z0-9"+spclchar+"]{"+len+"}$";
        pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        matcher =pattern.matcher(value);
        return matcher.matches();
    }

    public static boolean validateNumeric(String value, int len) {
        regex = "^\\d{"+len+"}$";
        pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        matcher =pattern.matcher(value);
        return matcher.matches();
    }
    public static boolean validateAlphaNumericFixedlen(String value, int len) {
        regex = "^[0-9a-zA-Z]{"+len+"}$";
        pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        matcher =pattern.matcher(value);
        return matcher.matches();
    }
}
