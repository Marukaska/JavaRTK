package ru.rtk.java.homeworks.homework_lesson23;

import java.util.*;

public class Task01 {
    public static <T> Set<T> getUniqueElements(ArrayList<T> list) {
        return new HashSet<>(list);
    }

    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<>(Arrays.asList("a", "b", "a", "c", "b"));
        Set<String> unique = getUniqueElements(list);
        System.out.println(unique);
    }
}