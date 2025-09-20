package ru.rtk.java.attestation.attestation_02.dungeon.model;

import java.util.*;

public class Room {
    public static final class Exit {
        private final Room to;
        private boolean locked;
        private String keyName; // null -> любой ключ подходит

        Exit(Room to, boolean locked, String keyName) {
            this.to = to;
            this.locked = locked;
            this.keyName = keyName;
        }
        public Room to() { return to; }
        public boolean locked() { return locked; }
        public String keyName() { return keyName; }
        public void unlock() { this.locked = false; }
    }

    private final String name;
    private final String description;
    private final Map<String, Exit> neighbors = new HashMap<>();
    private final List<Item> items = new ArrayList<>();
    private Monster monster;

    public Room(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() { return name; }

    public Room connect(String dir, Room other) {
        neighbors.put(dir.toLowerCase(Locale.ROOT), new Exit(other, false, null));
        return this;
    }

    public Room connectLocked(String dir, Room other, String keyName) {
        neighbors.put(dir.toLowerCase(Locale.ROOT), new Exit(other, true, keyName));
        return this;
    }

    public Optional<Room> neighbor(String dir) {
        Exit e = neighbors.get(dir.toLowerCase(Locale.ROOT));
        if (e == null || e.locked) return Optional.empty();
        return Optional.ofNullable(e.to);
    }

    public Optional<Exit> exit(String dir) {
        return Optional.ofNullable(neighbors.get(dir.toLowerCase(Locale.ROOT)));
    }

    public Map<String, Exit> getExits() { return neighbors; }

    public List<Item> getItems() { return items; }

    public void addItem(Item i) { items.add(i); }

    public Monster getMonster() { return monster; }

    public void setMonster(Monster monster) { this.monster = monster; }

    public String describe() {
        StringBuilder sb = new StringBuilder(name + ": " + description);
        if (!items.isEmpty()) {
            sb.append("\nПредметы: ").append(String.join(", ", items.stream().map(Item::getName).toList()));
        }
        if (monster != null) {
            sb.append("\nВ комнате монстр: ").append(monster.getName()).append(" (ур. ").append(monster.getLevel()).append(")");
        }
        if (!neighbors.isEmpty()) {
            List<String> outs = new ArrayList<>();
            for (var e : neighbors.entrySet()) {
                String label = e.getKey();
                if (e.getValue().locked) label += " (заперта)";
                outs.add(label);
            }
            sb.append("\nВыходы: ").append(String.join(", ", outs));
        }
        return sb.toString();
    }
}
