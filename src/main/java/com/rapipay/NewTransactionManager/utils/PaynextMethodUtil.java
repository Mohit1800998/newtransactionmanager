package com.rapipay.NewTransactionManager.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PaynextMethodUtil {

    public static String generateRrn() {
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyDDDHHmm");
        String datetime = ft.format(date);
        String str = datetime + stan();
        return str;
    }


    public static String stan() {

        String stan1 = String.valueOf((Math.random()+1)*1000000).substring(1,7);

        return stan1;
    }
    public static String getDate() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");
        String strDate= formatter.format(date);
        return strDate;
    }
    public static String masking(String cardNumber)
    {
        if(cardNumber==null)
            return "";
        else
        {
            int masked = cardNumber.substring(0,cardNumber.length() - 4).length() - 6;
            String mask = new String(new char[masked]).replace("\0", "*");
            String maskedCardNumber=cardNumber.substring(0, 6) + mask + cardNumber.substring(cardNumber.length() - 4, cardNumber.length());
            return maskedCardNumber;
        }
    }


}
