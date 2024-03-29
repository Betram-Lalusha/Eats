package com.example.eats.Emails;

import java.util.regex.Matcher;
import  java.util.regex.Pattern;

/*
Help on how to make this class was adopted from https://mkyong.com/regular-expressions/how-to-validate-email-address-with-regular-expression/
 */
public class ValidEmailTester {

    private static final String VALID_EMAIL_REGEX = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    private static final Pattern mPattern = Pattern.compile(VALID_EMAIL_REGEX);

    public ValidEmailTester() {}

    /**
     * Tests whether the email given to this class is a valid email
     * @return: true if the email is valid. False otherwise
     */
    public static boolean isValidEmail(String email) {
        Matcher matcher = mPattern.matcher(email);
        return  matcher.matches();
    }
}
