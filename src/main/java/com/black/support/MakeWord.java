package com.black.support;

import java.util.ArrayList;

/**
 * Created by Nick on 06.06.2016.
 */
public class MakeWord {
    public synchronized static String make(int word){
        //получаем входное число
        int s = word;
        //Создаем внутреннюю переменную
        String helpWord = "";
        //Массив символов char
        ArrayList<Character> charList = new ArrayList<>();

        //переменная для вычисления остатка
        int rest = 0;

        //Пока s > 0 вычисляем эквивалентное шестнадцатиричное число
        //Результат получается "перевернутым"
        while (s > 0) {
            rest = s%16;
            s = s/16;
            //Полученное значение остатка преобразовываем в символ
            char c = Character.forDigit(rest, Character.MAX_RADIX);
            charList.add(Character.toUpperCase(c));
        }

        //Устанавливаем нормальное следование чисел
        for (int i = 0; i < charList.size() / 2; i++){
            char first = charList.get(i);
            int point = charList.size() - 1 - i;
            char last = charList.get(point);

            charList.set(i, last);
            charList.set(point, first);
        }

        //Конкастенируем переменные из контейнера в одну строку
        for (char c : charList){
            helpWord += c;
        }

        return helpWord;
    }
}
