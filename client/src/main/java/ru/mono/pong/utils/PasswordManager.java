package ru.mono.pong.utils;

import java.util.Arrays;

public class PasswordManager {
    public static boolean containsLetters(String pass){
        char[] chars = pass.toCharArray();
        char[] passUpper = pass.toCharArray();
        for(int i = 0; i < passUpper.length; i++) passUpper[i] = Character.toUpperCase(passUpper[i]);
        char[] passLower = pass.toCharArray();
        for(int i = 0; i < passLower.length; i++) passLower[i] = Character.toLowerCase(passLower[i]);
        return !Arrays.equals(chars, passUpper) && !Arrays.equals(chars, passLower);
    }

    public static boolean containsNumber(String pass){
        return pass.matches(".*\\d.*");
    }

    public static boolean hasCorrectLength(String pass){
        return (pass.length()>=10);
    }
}