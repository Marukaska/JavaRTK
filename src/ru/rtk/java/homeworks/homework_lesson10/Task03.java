package ru.rtk.java.homeworks.homework_lesson10;

import java.util.Arrays;
import java.util.Scanner;

public class Task03 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите два слова через проблел:");
        String line = scanner.nextLine().toLowerCase();

        String[] words = line.split(" ");
        for (int i = 0; i < words.length; i++) {
            char[] chars = words[i].toCharArray();
            Arrays.sort(chars);
            words[i] = new String(chars);
        }

        System.out.println("Результат:" + String.join(" ", words));
    }
}
