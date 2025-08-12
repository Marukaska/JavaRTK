package ru.rtk.java.attestation.attestation_01;

import java.util.*;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Map<String, Person> people = new LinkedHashMap<>();
        Map<String, Product> products = new HashMap<>();

        try {
            // Ввод покупателей
            System.out.println("Введите покупателей в формате: Имя = Деньги; Имя = Деньги ...");
            System.out.println("Пример: Павел Андреевич = 10000; Анна Петровна = 2000; Борис = 10");
            String peopleInput = scanner.nextLine();
            for (String personEntry : peopleInput.split(";")) {
                String[] parts = personEntry.trim().split("=");
                String name = parts[0].trim();
                int money = Integer.parseInt(parts[1].trim());
                people.put(name, new Person(name, money));
            }

            // Ввод продуктов
            System.out.println("\nВведите продукты в формате: Название = Стоимость; Название = Стоимость ...");
            System.out.println("Пример: Хлеб = 40; Молоко = 60; Торт = 1000");
            String productsInput = scanner.nextLine();
            for (String productEntry : productsInput.split(";")) {
                String[] parts = productEntry.trim().split("=");
                String name = parts[0].trim();
                int price = Integer.parseInt(parts[1].trim());
                products.put(name, new Product(name, price));
            }

            // Покупки
            System.out.println("\nВведите покупки в формате: Имя - Название продукта");
            System.out.println("Пример: Павел Андреевич - Хлеб");
            System.out.println("Когда закончите ввод — напишите END");

            while (true) {
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("END")) break;

                String[] parts = input.split(" - ");
                if (parts.length < 2) {
                    System.out.println("Некорректный формат. Используйте: Имя - Название продукта");
                    continue;
                }

                String personName = parts[0].trim();
                String productName = parts[1].trim();

                Person person = people.get(personName);
                Product product = products.get(productName);

                if (person != null && product != null) {
                    person.buyProduct(product);
                } else {
                    System.out.println("Пользователь или продукт не найдены.");
                }
            }

            // Итоговая информация
            System.out.println("\nРезультаты покупок:");
            for (Person person : people.values()) {
                System.out.println(person);
            }

        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}
