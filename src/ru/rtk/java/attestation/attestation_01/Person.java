package ru.rtk.java.attestation.attestation_01;

import java.util.ArrayList;
import java.util.List;

public class Person {
    private String name;
    private int money;
    private List<Product> bag;

    public Person(String name, int money) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }
        if (name.length() < 3) {
            throw new IllegalArgumentException("Имя не может быть короче 3 символов");
        }
        if (money < 0) {
            throw new IllegalArgumentException("Деньги не могут быть отрицательными");
        }
        this.name = name;
        this.money = money;
        this.bag = new ArrayList<>();
    }

    public String getName() { return name; }

    public int getMoney() { return money; }

    public void buyProduct(Product product) {
        if (money >= product.getPrice()) {
            money -= product.getPrice();
            bag.add(product);
            System.out.println(name + " купил " + product.getName());
        } else {
            System.out.println(name + " не может позволить себе " + product.getName());
        }
    }

    public boolean hasBoughtAnything() {
        return !bag.isEmpty();
    }

    @Override
    public String toString() {
        if (bag.isEmpty()) {
            return name + " - Ничего не куплено";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" - ");
        for (int i = 0; i < bag.size(); i++) {
            sb.append(bag.get(i).getName());
            if (i < bag.size() - 1) sb.append(", ");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person p = (Person) o;
        return name.equals(p.name) && money == p.money && bag.equals(p.bag);
    }

    @Override
    public int hashCode() {
        return name.hashCode() + money + bag.hashCode();
    }
}

