package ru.rtk.java.homeworks.homework_lesson9;

import java.util.Random;

public class Television {
    // Поля (сделаны private)
    private String brand;
    private int size;
    private boolean isOn;

    // Конструктор
    public Television(String brand, int size, boolean isOn) {
        this.brand = brand;
        this.size = size;
        this.isOn = isOn;
    }

    // Дополнительный конструктор с рандомными значениями
    public Television() {
        String[] brands = {"Samsung", "LG", "Sony", "Philips", "Xiaomi"};
        Random rand = new Random();
        this.brand = brands[rand.nextInt(brands.length)];
        this.size = 32 + rand.nextInt(30); // от 32 до 61 дюйма
        this.isOn = rand.nextBoolean();
    }

    // Методы
    public void turnOn() {
        isOn = true;
        System.out.println(brand + " включен");
    }

    public void turnOff() {
        isOn = false;
        System.out.println(brand + " выключен");
    }

    public void showInfo() {
        System.out.println("Телевизор: " + brand + ", " + size + " дюймов, " + (isOn ? "включен" : "выключен"));
    }

    // Геттеры и сеттеры (свойства)
    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }
}

