package ru.rtk.java.attestation.attestation_01;

import java.time.LocalDate;
import java.util.*;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Map<String, Person> people = new LinkedHashMap<>();
        Map<String, Product> products = new HashMap<>();

        try {
            // Ввод покупателей
            System.out.println("Как хотите добавить покупателей?");
            System.out.println("1 - Ввести вручную");
            System.out.println("2 - Использовать готовый список");
            String choicePeople = scanner.nextLine();

            if ("1".equals(choicePeople)) {
                System.out.println("Введите покупателей в формате: Имя = Деньги; Имя = Деньги ...");
                System.out.println("Пример: Павел Андреевич = 10000; Анна Петровна = 2000; Борис = 10");
                String peopleInput = scanner.nextLine();
                for (String personEntry : peopleInput.split(";")) {
                    String[] parts = personEntry.trim().split("=");
                    String name = parts[0].trim();
                    int money = Integer.parseInt(parts[1].trim());
                    people.put(name, new Person(name, money));
                }
            } else {
                people.put("Павел Андреевич", new Person("Павел Андреевич", 10000));
                people.put("Анна Петровна", new Person("Анна Петровна", 2000));
                people.put("Борис", new Person("Борис", 10));
                System.out.println("Использован готовый список покупателей.");
            }

            // Ввод продуктов
            System.out.println("\nКак хотите добавить продукты?");
            System.out.println("1 - Ввести вручную");
            System.out.println("2 - Использовать готовый список (включая скидочные продукты)");
            String choiceProducts = scanner.nextLine();

            if ("1".equals(choiceProducts)) {
                System.out.println("Введите продукты в формате: Название = Стоимость; Название = Стоимость ...");
                System.out.println("Для продуктов со скидкой формат: Название = Цена, Скидка %, ГГГГ-ММ-ДД");
                System.out.println("Пример: Хлеб = 40; Молоко = 60; Торт = 1000, 25, 2025-08-20");
                String productsInput = scanner.nextLine();
                for (String productEntry : productsInput.split(";")) {
                    String[] parts = productEntry.trim().split("=");
                    if (parts.length < 2) {
                        System.out.println("Пропущен знак '=' в " + productEntry);
                        continue;
                    }
                    String name = parts[0].trim();
                    String[] priceParts = parts[1].trim().split(",");

                    if (priceParts.length == 1) {
                        // обычный продукт
                        int price = Integer.parseInt(priceParts[0].trim());
                        products.put(name, new Product(name, price));
                    } else if (priceParts.length == 3) {
                        // скидочный продукт
                        int price = Integer.parseInt(priceParts[0].trim());
                        int discount = Integer.parseInt(priceParts[1].trim());
                        LocalDate expiryDate = LocalDate.parse(priceParts[2].trim());
                        products.put(name, new DiscountProduct(name, price, discount, expiryDate));
                    } else {
                        System.out.println("Неверный формат для " + name);
                    }
                }

            } else {
                products.put("Хлеб", new Product("Хлеб", 40));
                products.put("Молоко", new Product("Молоко", 60));
                products.put("Торт", new Product("Торт", 1000));
                products.put("Сыр по акции", new DiscountProduct("Сыр по акции", 500, 10, LocalDate.now().plusDays(3)));
                products.put("Шоколад по акции", new DiscountProduct("Шоколад по акции", 200, 50, LocalDate.now().minusDays(1)));
                System.out.println("Использован готовый список продуктов.");
            }

            // Покупки
            System.out.println("\nКак хотите оформить покупки?");
            System.out.println("1 - Ввести вручную");
            System.out.println("2 - Выбирать из списка");
            String choiceBuy = scanner.nextLine();

            if ("1".equals(choiceBuy)) {
                System.out.println("Введите покупки в формате: Имя - Название продукта");
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

                    makePurchase(people, products, parts[0].trim(), parts[1].trim());
                }
            } else {
                List<String> peopleNames = new ArrayList<>(people.keySet());
                List<String> productNames = new ArrayList<>(products.keySet());

                while (true) {
                    System.out.println("\nВыберите покупателя (или напишите END):");
                    for (int i = 0; i < peopleNames.size(); i++) {
                        System.out.printf("%d) %s%n", i + 1, peopleNames.get(i));
                    }
                    String pChoice = scanner.nextLine();
                    if (pChoice.equalsIgnoreCase("END")) break;

                    int pIndex = Integer.parseInt(pChoice) - 1;
                    if (pIndex < 0 || pIndex >= peopleNames.size()) {
                        System.out.println("Неверный выбор.");
                        continue;
                    }

                    System.out.println("Выберите продукт:");
                    for (int i = 0; i < productNames.size(); i++) {
                        System.out.printf("%d) %s%n", i + 1, productNames.get(i));
                    }
                    int prIndex = Integer.parseInt(scanner.nextLine()) - 1;
                    if (prIndex < 0 || prIndex >= productNames.size()) {
                        System.out.println("Неверный выбор.");
                        continue;
                    }

                    makePurchase(people, products, peopleNames.get(pIndex), productNames.get(prIndex));
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

    private static void makePurchase(Map<String, Person> people, Map<String, Product> products, String personName, String productName) {
        Person person = people.get(personName);
        Product product = products.get(productName);

        if (person != null && product != null) {
            person.buyProduct(product);
        } else {
            System.out.println("Пользователь или продукт не найдены.");
        }
    }
}
