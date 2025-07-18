package ru.rtk.java.homeworks.homework_lesson9;

import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        // Пример с ручными параметрами
        Television tv1 = new Television("Samsung", 55, false);
        tv1.showInfo();
        tv1.turnOn();
        tv1.showInfo();

        // Пример с рандомным телевизором
        Television tv2 = new Television();
        tv2.showInfo();

        // Пример с вводом с клавиатуры
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите бренд телевизора: ");
        String brand = scanner.nextLine();
        System.out.print("Введите размер телевизора в дюймах: ");
        int size = scanner.nextInt();
        Television tv3 = new Television(brand, size, false);
        tv3.showInfo();
    }
}
