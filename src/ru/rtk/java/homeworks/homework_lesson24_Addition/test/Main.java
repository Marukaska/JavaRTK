package ru.rtk.java.homeworks.homework_lesson24_Addition.test;

import ru.rtk.java.homeworks.homework_lesson24_Addition.model.Car;
import ru.rtk.java.homeworks.homework_lesson24_Addition.repository.CarsRepository;
import ru.rtk.java.homeworks.homework_lesson24_Addition.repository.CarsRepositoryImpl;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {

        CarsRepository repository = new CarsRepositoryImpl("C:/GitHub/JavaRTK/src/ru/rtk/java/homeworks/homework_lesson24_Addition/data/cars.txt");
        List<Car> cars = repository.loadCars();

        System.out.println("Автомобили в базе:");
        System.out.println("Number Model Color Mileage Cost");
        cars.forEach(System.out::println);

        String colorToFind = "Black";
        long mileageToFind = 0L;
        long priceFrom = 700_000L;
        long priceTo = 800_000L;
        String modelToFind = "Toyota";
        String modelNotExist = "Volvo";

        // 1) Фильтр по цвету или пробегу
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

        // 3) Цвет авто с минимальной ценой
        cars.stream()
                .min(Comparator.comparingLong(Car::getCost))
                .ifPresent(c -> System.out.println("Цвет автомобиля с минимальной стоимостью: " + c.getColor()));

        // 4) Средняя цена по модели
        double avgToyota = cars.stream()
                .filter(c -> c.getModel().equalsIgnoreCase(modelToFind))
                .mapToLong(Car::getCost)
                .average().orElse(0);
        double avgVolvo = cars.stream()
                .filter(c -> c.getModel().equalsIgnoreCase(modelNotExist))
                .mapToLong(Car::getCost)
                .average().orElse(0);

        System.out.printf("Средняя стоимость модели %s: %,.2f%n", modelToFind, avgToyota);
        System.out.printf("Средняя стоимость модели %s: %,.2f%n", modelNotExist, avgVolvo);


        repository.saveCars(cars);
    }
}