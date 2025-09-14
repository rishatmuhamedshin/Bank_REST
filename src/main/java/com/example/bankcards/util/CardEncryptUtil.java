package com.example.bankcards.util;

import com.example.bankcards.exception.exceptions.WrongCardNumberException;

public class CardEncryptUtil {

    public static String maskCardNumber(String cardNumber){
        if(cardNumber.length() != 16 || !cardNumber.matches("[0-9]+")){
            throw new WrongCardNumberException("Не правильный номер карты");
        }

        var lastFourNumber = cardNumber.substring(cardNumber.length() - 4);

        return "**** **** **** " + lastFourNumber;
    }


}
