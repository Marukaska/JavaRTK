package ru.rtk.java.homeworks.homework_lesson10;

import java.util.Scanner;

public class Task01 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите букву английского алфавита:");
        String input = scanner.nextLine().toLowerCase();

        String layout = "qwertyuiopasdfghjklzxcvbnm";

        int idx = layout.indexOf(input);

        if (input.equals("q")) {
            System.out.println("m");
        } else if (idx > 0) {
            System.out.println(layout.charAt(idx - 1));
        }
    }
}
