package ru.rtk.java.attestation.attestation_02.dungeon.core;

import ru.rtk.java.attestation.attestation_02.dungeon.model.*;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class SaveLoad {
    private static final Path SAVE = Paths.get("save.txt");
    private static final Path SCORES = Paths.get("scores.csv");

    public static void save(GameState s, Map<String, Room> worldIndex) {
        try (BufferedWriter w = Files.newBufferedWriter(SAVE)) {
            Player p = s.getPlayer();
            w.write("player;" + p.getName() + ";" + p.getHp() + ";" + p.getAttack());
            w.newLine();
            String inv = p.getInventory().stream()
                    .map(i -> i.getClass().getSimpleName() + ":" + i.getName())
                    .collect(Collectors.joining(","));
            w.write("inventory;" + inv);
            w.newLine();
            w.write("current;" + s.getCurrent().getName());
            w.newLine();
            for (Room r : worldIndex.values()) {
                w.write("room;" + r.getName()); w.newLine();
                String items = r.getItems().stream()
                        .map(i -> i.getClass().getSimpleName() + ":" + i.getName())
                        .collect(Collectors.joining(","));
                w.write("items;" + items); w.newLine();
                Monster m = r.getMonster();
                if (m != null) w.write("monster;" + m.getName() + ";" + m.getLevel() + ";" + m.getHp());
                else w.write("monster;");
                w.newLine();
                String exits = r.getExits().entrySet().stream().map(e -> {
                    Room.Exit ex = e.getValue();
                    String key = ex.keyName() == null ? "" : ex.keyName();
                    return e.getKey() + "->" + ex.to().getName() + ":" + (ex.locked() ? "1" : "0") + ":" + key;
                }).collect(Collectors.joining("|"));
                w.write("exits;" + exits); w.newLine();
                w.write("--"); w.newLine();
            }
            System.out.println("Сохранено в " + SAVE.toAbsolutePath());
            writeScore(p.getName(), s.getScore());
        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось сохранить игру", e);
        }
    }

    public static void load(GameState s, Map<String, Room> worldIndex) {
        if (!Files.exists(SAVE)) throw new IllegalStateException("Нет сохранения");
        try (BufferedReader r = Files.newBufferedReader(SAVE)) {
            Map<String, String> header = new HashMap<>();
            List<List<String>> blocks = new ArrayList<>();
            List<String> cur = null;
            for (String line; (line = r.readLine()) != null;) {
                if (line.startsWith("player;") || line.startsWith("inventory;") || line.startsWith("current;")) {
                    String[] p = line.split(";", 2);
                    if (p.length == 2) header.put(p[0], p[1]);
                    continue;
                }
                if (line.equals("--")) { if (cur != null) { blocks.add(cur); cur = null; } continue; }
                if (line.startsWith("room;")) { cur = new ArrayList<>(); }
                if (cur != null) cur.add(line);
            }
            Player p = s.getPlayer();
            String[] pp = header.getOrDefault("player", ";;0;0").split(";");
            if (pp.length >= 4) {
                p.setName(pp[0].isBlank() ? p.getName() : pp[0]);
                p.setHp(Integer.parseInt(pp[2]));
                p.setAttack(Integer.parseInt(pp[3]));
            }
            p.getInventory().clear();
            String inv = header.getOrDefault("inventory", "");
            if (!inv.isBlank()) for (String tok : inv.split(",")) {
                String[] t = tok.split(":", 2);
                if (t.length < 2) continue;
                switch (t[0]) {
                    case "Potion" -> p.getInventory().add(new Potion(t[1], 5));
                    case "Key" -> p.getInventory().add(new Key(t[1]));
                    case "Weapon" -> p.getInventory().add(new Weapon(t[1], 3));
                }
            }
            Map<String, PendingExits> pending = new HashMap<>();
            for (List<String> b : blocks) {
                String roomName = b.stream().filter(l -> l.startsWith("room;")).findFirst().orElse("room;").split(";",2)[1];
                Room room = worldIndex.get(roomName);
                if (room == null) continue;
                String itemsLine = b.stream().filter(l -> l.startsWith("items;")).findFirst().orElse("items;").split(";",2)[1];
                room.getItems().clear();
                if (!itemsLine.isBlank()) for (String tok : itemsLine.split(",")) {
                    String[] t = tok.split(":",2);
                    if (t.length<2) continue;
                    switch (t[0]) {
                        case "Potion" -> room.addItem(new Potion(t[1], 5));
                        case "Key" -> room.addItem(new Key(t[1]));
                        case "Weapon" -> room.addItem(new Weapon(t[1], 2));
                    }
                }
                String monLine = b.stream().filter(l -> l.startsWith("monster;")).findFirst().orElse("monster;").split(";",2)[1];
                if (monLine.isBlank()) room.setMonster(null);
                else {
                    String[] mm = monLine.split(";");
                    if (mm.length>=3) room.setMonster(new Monster(mm[0], Integer.parseInt(mm[1]), Integer.parseInt(mm[2])));
                }
                String exLine = b.stream().filter(l -> l.startsWith("exits;")).findFirst().orElse("exits;").split(";",2)[1];
                pending.put(roomName, new PendingExits(exLine));
            }
            for (var e : pending.entrySet()) {
                String from = e.getKey();
                Room rf = worldIndex.get(from);
                if (rf == null) continue;
                e.getValue().apply(rf, worldIndex);
            }
            Room current = worldIndex.get(header.getOrDefault("current",""));
            if (current != null) s.setCurrent(current);
            System.out.println("Загружено из " + SAVE.toAbsolutePath());
        } catch (IOException ex) {
            throw new UncheckedIOException("Не удалось загрузить игру", ex);
        }
    }

    private static class PendingExits {
        final String raw;
        PendingExits(String raw) { this.raw = raw; }
        void apply(Room from, Map<String,Room> idx) {
            if (raw.isBlank()) return;
            for (String part : raw.split("\\|")) {
                String[] a = part.split("->");
                if (a.length!=2) continue;
                String dir = a[0];
                String[] b = a[1].split(":",3);
                String toName = b.length>0? b[0] : "";
                boolean locked = b.length>1 && "1".equals(b[1]);
                String key = b.length>2? b[2] : null;
                Room to = idx.get(toName);
                if (to==null) continue;
                if (locked) from.connectLocked(dir, to, key);
                else from.connect(dir, to);
            }
        }
    }

    public static List<Score> readScores(int limit) {
        if (!Files.exists(SCORES)) return List.of();
        try (BufferedReader r = Files.newBufferedReader(SCORES)) {
            List<Score> res = new ArrayList<>();
            String header = r.readLine();
            for (String line; (line = r.readLine()) != null;) {
                String[] t = line.split(",");
                if (t.length >= 3) {
                    String player = t[t.length-2];
                    int score = Integer.parseInt(t[t.length-1]);
                    res.add(new Score(player, score));
                }
            }
            res.sort(Comparator.comparingInt(Score::score).reversed());
            if (res.size() > limit) return res.subList(0, limit);
            return res;
        } catch (IOException e) {
            return List.of();
        }
    }

    private static void writeScore(String player, int score) {
        try {
            boolean header = !Files.exists(SCORES);
            try (BufferedWriter w = Files.newBufferedWriter(SCORES, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                if (header) { w.write("ts,player,score"); w.newLine(); }
                w.write(LocalDateTime.now() + "," + player + "," + score);
                w.newLine();
            }
        } catch (IOException e) {
            System.err.println("Не удалось записать очки: " + e.getMessage());
        }
    }

    public record Score(String player, int score) {}
}
