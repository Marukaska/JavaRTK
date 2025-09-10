package ru.rtk.java.attestation.attestation_01;

import java.time.LocalDate;

public class DiscountProduct extends Product {
    private double discount; // в процентах
    private LocalDate expirationDate;

    public DiscountProduct(String name, double price, double discount, LocalDate expirationDate) {
        super(name, price);
        setDiscount(discount);
        setExpirationDate(expirationDate);
    }

    public double getDiscount() { return discount; }

    public void setDiscount(double discount) {
        if (discount < 0 || discount > 100) {
            throw new IllegalArgumentException("Скидка должна быть в диапазоне 0–100%");
        }
        this.discount = discount;
    }

    public LocalDate getExpirationDate() { return expirationDate; }

    public void setExpirationDate(LocalDate expirationDate) {
        if (expirationDate == null) {
            throw new IllegalArgumentException("Дата окончания скидки должна быть указана");
        }
        this.expirationDate = expirationDate;
    }

    @Override
    public double getPrice() {
        if (LocalDate.now().isBefore(expirationDate) || LocalDate.now().isEqual(expirationDate)) {
            return super.getPrice() * (1 - discount / 100);
        }
        return super.getPrice();
    }

    @Override
    public String toString() {
        String status = LocalDate.now().isBefore(expirationDate) || LocalDate.now().isEqual(expirationDate)
                ? "Скидка " + discount + "% до " + expirationDate
                : "Скидка не действует";
        return super.getName() + " (" + getPrice() + " руб., " + status + ")";
    }
}
