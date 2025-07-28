package ru.rtk.java.homeworks.homework_lesson10;

import java.util.Scanner;

public class Task02 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите строку содержащую только только '>', '<', '-':");
        String input = scanner.nextLine();

        int countRight = (input.length() - input.replace(">>-->", "").length()) / 5;
        int countLeft  = (input.length() - input.replace("<--<<", "").length()) / 5;

        System.out.println("Стрел в строке:" + (countRight + countLeft));
    }
}
