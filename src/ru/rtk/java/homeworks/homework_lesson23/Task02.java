package ru.rtk.java.homeworks.homework_lesson23;

import java.util.*;

public class Task02 {
    public static boolean isAnagram(String s, String t) {
        if (s.length() != t.length()) return false;

        char[] sArr = s.toLowerCase().toCharArray();
        char[] tArr = t.toLowerCase().toCharArray();

        Arrays.sort(sArr);
        Arrays.sort(tArr);

        return Arrays.equals(sArr, tArr);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите первую строку: ");
        String s = scanner.nextLine();
        System.out.print("Введите вторую строку: ");
        String t = scanner.nextLine();

        System.out.println(isAnagram(s, t));
    }
}