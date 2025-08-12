package ru.rtk.java.attestation.attestation_01;

import java.util.*;

public class App_autotest {
    public static void main(String[] args) {
        List<String> testInput = List.of(
                "Павел Андреевич = 10000; Анна Петровна = 2000; Борис = 10",
                "Хлеб = 40; Молоко = 60; Торт = 1000; Кофе растворимый = 879; Масло = 150",
                "Павел Андреевич - Хлеб",
                "Павел Андреевич - Масло",
                "Анна Петровна - Кофе растворимый",
                "Анна Петровна - Молоко",
                "Анна Петровна - Молоко",
                "Анна Петровна - Молоко",
                "Анна Петровна - Торт",
                "Борис - Торт",
                "Павел Андреевич - Торт",
                "END"
        );

        Map<String, Person> people = new LinkedHashMap<>();
        Map<String, Product> products = new HashMap<>();
        Iterator<String> input = testInput.iterator();

        try {
            // Ввод покупателей
            String peopleInput = input.next();
            for (String personEntry : peopleInput.split(";")) {
                String[] parts = personEntry.trim().split("=");
                String name = parts[0].trim();
                int money = Integer.parseInt(parts[1].trim());
                people.put(name, new Person(name, money));
            }

            // Ввод продуктов
            String productsInput = input.next();
            for (String productEntry : productsInput.split(";")) {
                String[] parts = productEntry.trim().split("=");
                String name = parts[0].trim();
                int price = Integer.parseInt(parts[1].trim());
                products.put(name, new Product(name, price));
            }

            // Ввод покупок
            while (input.hasNext()) {
                String line = input.next();
                if (line.equalsIgnoreCase("END")) break;

                String[] parts = line.split(" - ");
                String personName = parts[0].trim();
                String productName = parts[1].trim();

                Person person = people.get(personName);
                Product product = products.get(productName);

                if (person != null && product != null) {
                    person.buyProduct(product);
                }
            }

            // Итог
            System.out.println("\nРезультаты покупок:");
            for (Person person : people.values()) {
                System.out.println(person);
            }

        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}
