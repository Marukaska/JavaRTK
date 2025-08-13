package ru.rtk.java.attestation.attestation_01;

import java.util.Objects;

public class Product {
    private String name;
    private double price;

    public Product(String name, double price) {
        setName(name);
        setPrice(price);
    }

    public String getName() { return name; }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Название продукта не может быть пустым");
        }
        if (name.length() < 3) {
            throw new IllegalArgumentException("Название продукта не может быть короче 3 символов");
        }
        if (name.matches("\\d+")) {
            throw new IllegalArgumentException("Название продукта не может содержать только цифры");
        }
        this.name = name;
    }

    public double getPrice() { return price; }

    public void setPrice(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Стоимость продукта должна быть положительной");
        }
        this.price = price;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product p = (Product) o;
        return name.equals(p.name) && price == p.price;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price);
    }
}


