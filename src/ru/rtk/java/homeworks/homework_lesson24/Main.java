package ru.rtk.java.homeworks.homework_lesson24;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<Car> cars = Arrays.asList(
                new Car("a123me", "Mercedes", "White", 0, 8300000),
                new Car("b873of", "Volga", "Black", 0, 673000),
                new Car("w487mn", "Lexus", "Grey", 76000, 900000),
                new Car("p987hj", "Volga", "Red", 610, 704340),
                new Car("c987ss", "Toyota", "White", 254000, 761000),
                new Car("o983op", "Toyota", "Black", 698000, 740000),
                new Car("p146op", "BMW", "White", 271000, 850000),
                new Car("u893ii", "Toyota", "Purple", 210900, 440000),
                new Car("l097df", "Toyota", "Black", 108000, 780000),
                new Car("y876wd", "Toyota", "Black", 160000, 1000000)
        );

        String colorToFind = "Black";
        long mileageToFind = 0L;
        long priceFrom = 700_000L;
        long priceTo = 800_000L;
        String modelToFind = "Toyota";
        String modelNotExist = "Volvo";

        System.out.println("Автомобили в базе:");
        System.out.println("Number Model Color Mileage Cost");
        cars.forEach(System.out::println);

        // 1) Номера по цвету или пробегу
        List<String> numbers = cars.stream()
                .filter(c -> c.getColor().equalsIgnoreCase(colorToFind)
                        || c.getMileage() == mileageToFind)
                .map(Car::getNumber)
                .collect(Collectors.toList());

        System.out.println("\nНомера автомобилей по цвету или пробегу: " + String.join(" ", numbers));

        // 2) Кол-во уникальных моделей в диапазоне цен
        long uniqueModels = cars.stream()
                .filter(c -> c.getCost() >= priceFrom && c.getCost() <= priceTo)
                .map(Car::getModel)
                .distinct()
                .count();

        System.out.println("Уникальные автомобили: " + uniqueModels + " шт.");

        // 3) Цвет авто с минимальной стоимостью
        cars.stream()
                .min(Comparator.comparingLong(Car::getCost))
                .ifPresent(c -> System.out.println("Цвет автомобиля с минимальной стоимостью: " + c.getColor()));

        // 4) Средняя стоимость искомой модели
        double avgToyota = cars.stream()
                .filter(c -> c.getModel().equalsIgnoreCase(modelToFind))
                .mapToLong(Car::getCost)
                .average()
                .orElse(0);

        double avgVolvo = cars.stream()
                .filter(c -> c.getModel().equalsIgnoreCase(modelNotExist))
                .mapToLong(Car::getCost)
                .average()
                .orElse(0);

        System.out.printf("Средняя стоимость модели %s: %,.2f%n", modelToFind, avgToyota);
        System.out.printf("Средняя стоимость модели %s: %,.2f%n", modelNotExist, avgVolvo);
    }
}