package ru.rtk.java.homeworks.homework_lesson23;

import java.util.*;

class PowerfulSet {

    //Возвращает пересечение двух наборов:
    public static <T> Set<T> intersection(Set<T> set1, Set<T> set2) {
        Set<T> result = new HashSet<>(set1);
        result.retainAll(set2);
        return result;
    }

    //Возвращает объединение двух наборов:
    public static <T> Set<T> union(Set<T> set1, Set<T> set2) {
        Set<T> result = new HashSet<>(set1);
        result.addAll(set2);
        return result;
    }

    //Возвращает элементы первого набора без тех, которые находятся также и во втором наборе:
    public static <T> Set<T> relativeComplement(Set<T> set1, Set<T> set2) {
        Set<T> result = new HashSet<>(set1);
        result.removeAll(set2);
        return result;
    }

    public static void main(String[] args) {
        Set<Integer> set1 = new HashSet<>(Arrays.asList(1, 2, 3));
        Set<Integer> set2 = new HashSet<>(Arrays.asList(0, 1, 2, 4));

        System.out.println("Пересечение: " + intersection(set1, set2)); // {1, 2}
        System.out.println("Объединение: " + union(set1, set2)); // {0, 1, 2, 3, 4}
        System.out.println("Разность: " + relativeComplement(set1, set2)); // {3}
    }
}
