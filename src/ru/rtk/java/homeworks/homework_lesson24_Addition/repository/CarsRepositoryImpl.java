package ru.rtk.java.homeworks.homework_lesson24_Addition.repository;

import ru.rtk.java.homeworks.homework_lesson24_Addition.model.Car;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class CarsRepositoryImpl implements CarsRepository {
    private final String filePath;

    public CarsRepositoryImpl(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public List<Car> loadCars() {
        try {
            return Files.lines(Paths.get(filePath))
                    .map(line -> line.split("\\|"))
                    .map(parts -> new Car(
                            parts[0],
                            parts[1],
                            parts[2],
                            Long.parseLong(parts[3]),
                            Long.parseLong(parts[4])
                    ))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public void saveCars(List<Car> cars) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {
            for (Car car : cars) {
                writer.write(String.format("%s|%s|%s|%d|%d",
                        car.getNumber(),
                        car.getModel(),
                        car.getColor(),
                        car.getMileage(),
                        car.getCost()));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
