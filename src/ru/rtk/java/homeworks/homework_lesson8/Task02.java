package ru.rtk.java.homeworks.homework_lesson8;

import java.util.Random;

public class Task02 {
    public static void main(String[] args) {
        Random random = new Random();

        int vasya = random.nextInt(3); // 0 - камень, 1 - ножницы, 2 - бумага
        int petya = random.nextInt(3);

        String[] options = {"камень", "ножницы", "бумага"};

        System.out.println("Вася: " + options[vasya]);
        System.out.println("Петя: " + options[petya]);

        if (vasya == petya) {
            System.out.println("Ничья!");
        } else if ((vasya == 0 && petya == 1) ||
                (vasya == 1 && petya == 2) ||
                (vasya == 2 && petya == 0)) {
            System.out.println("Вася победил!");
        } else {
            System.out.println("Петя победил!");
        }
    }
}