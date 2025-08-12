package ru.rtk.java.attestation.attestation_01;

public class Product {
    private String name;
    private int price;

    public Product(String name, int price) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Название не может быть пустым");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Стоимость продукта не может быть отрицательной");
        }
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }

    public int getPrice() { return price; }

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
        return name.hashCode() + price;
    }
}


