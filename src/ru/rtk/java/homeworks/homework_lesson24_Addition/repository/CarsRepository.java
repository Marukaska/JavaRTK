package ru.rtk.java.homeworks.homework_lesson24_Addition.repository;

import ru.rtk.java.homeworks.homework_lesson24_Addition.model.Car;
import java.util.List;

public interface CarsRepository {
    List<Car> loadCars();       // загрузка из файла
    void saveCars(List<Car> cars); // сохранение в файл
}
