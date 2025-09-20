package ru.rtk.java.attestation.attestation_02.dungeon.core;

import ru.rtk.java.attestation.attestation_02.dungeon.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class Game {
    private final GameState state = new GameState();
    private final Map<String, Command> commands = new LinkedHashMap<>();
    private final Map<String, Room> worldIndex = new HashMap<>();
    private byte[][] leaks = new byte[0][]; // для alloc-демо

    static { WorldInfo.touch("Game"); }

    public Game() {
        registerCommands();
        bootstrapWorld();
    }

    private void registerCommands() {
        commands.put("help", (ctx, a) -> System.out.println("Команды: " + String.join(", ", commands.keySet())));
        commands.put("gc-stats", (ctx, a) -> {
            Runtime rt = Runtime.getRuntime();
            long free = rt.freeMemory(), total = rt.totalMemory(), used = total - free;
            System.out.println("Память: used=" + used + " free=" + free + " total=" + total);
        });
        commands.put("alloc", (ctx, a) -> {
            int mb = a.isEmpty()? 10 : Integer.parseInt(a.get(0));
            byte[] block = new byte[mb * 1024 * 1024];
            byte[][] n = Arrays.copyOf(leaks, leaks.length + 1);
            n[n.length-1] = block;
            leaks = n;
            System.out.println("Выделено " + mb + " MB. Утечек-блоков: " + leaks.length + ". Используйте 'gc' или 'leak-clear'.");
        });
        commands.put("gc", (ctx, a) -> { System.gc(); System.out.println("System.gc() запрошен."); });
        commands.put("leak-clear", (ctx, a) -> { leaks = new byte[0][]; System.out.println("Ссылки очищены."); });
        commands.put("about", (ctx, a) -> System.out.println("DungeonMini. Игрок: " + ctx.getPlayer().getName() + ", очки: " + ctx.getScore()));
        commands.put("look", (ctx, a) -> System.out.println(ctx.getCurrent().describe()));
        commands.put("scores", (ctx, a) -> {
            var list = SaveLoad.readScores(10);
            if (list.isEmpty()) { System.out.println("Пока нет записей."); return; }
            System.out.println("Топ-очки:");
            int i=1; for (var s : list) System.out.println((i++) + ") " + s.player() + " — " + s.score());
        });
        commands.put("move", (ctx, a) -> {
            if (a.isEmpty()) throw new InvalidCommandException("Использование: move <north|south|east|west>");
            String dir = a.get(0).toLowerCase(Locale.ROOT);
            Room cur = ctx.getCurrent();
            var exit = cur.exit(dir).orElseThrow(() -> new InvalidCommandException("Нет выхода на '" + dir + "'"));
            if (exit.locked()) throw new InvalidCommandException("Дверь на '" + dir + "' заперта ключом '" + (exit.keyName()==null?"(любой)":exit.keyName()) + "'.");
            Room next = exit.to();
            ctx.setCurrent(next);
            System.out.println("Вы пошли на " + dir + ". " + next.describe());
        });
        commands.put("take", (ctx, a) -> {
            if (a.isEmpty()) throw new InvalidCommandException("Использование: take <item name>");
            String name = String.join(" ", a);
            Room r = ctx.getCurrent();
            Optional<Item> it = r.getItems().stream().filter(i -> i.getName().equalsIgnoreCase(name)).findFirst();
            if (it.isEmpty()) throw new InvalidCommandException("В комнате нет предмета '" + name + "'"); 
            r.getItems().remove(it.get());
            ctx.getPlayer().getInventory().add(it.get());
            System.out.println("Подобран предмет: " + it.get().getName());
        });
        commands.put("inventory", (ctx, a) -> {
            var inv = ctx.getPlayer().getInventory();
            if (inv.isEmpty()) { System.out.println("Инвентарь пуст."); return; }
            Map<String, List<Item>> grouped = inv.stream()
                    .sorted(Comparator.comparing((Item i) -> i.getClass().getSimpleName())
                            .thenComparing(Item::getName, String.CASE_INSENSITIVE_ORDER))
                    .collect(Collectors.groupingBy(i -> i.getClass().getSimpleName(), LinkedHashMap::new, Collectors.toList()));
            grouped.forEach((type, list) -> System.out.println(type + ": " + list.stream().map(Item::getName).collect(Collectors.joining(", "))));
        });
        commands.put("use", (ctx, a) -> {
            if (a.isEmpty()) throw new InvalidCommandException("Использование: use <item name>");
            String name = String.join(" ", a);
            var p = ctx.getPlayer();
            Item item = p.getInventory().stream().filter(i -> i.getName().equalsIgnoreCase(name)).findFirst()
                    .orElseThrow(() -> new InvalidCommandException("Нет предмета '" + name + "' в инвентаре"));
            item.apply(ctx);
        });
        commands.put("fight", (ctx, a) -> {
            Room r = ctx.getCurrent();
            Monster m = r.getMonster();
            if (m == null) throw new InvalidCommandException("Здесь никого нет");
            Player p = ctx.getPlayer();
            while (m.getHp() > 0 && p.getHp() > 0) {
                m.setHp(m.getHp() - p.getAttack());
                System.out.println("Вы ударили '" + m.getName() + "': HP монстра = " + Math.max(0, m.getHp()));
                if (m.getHp() <= 0) break;
                int dmg = Math.max(1, m.getLevel());
                p.setHp(p.getHp() - dmg);
                System.out.println("Монстр ударил вас на " + dmg + ". Ваше HP = " + Math.max(0, p.getHp()));
            }
            if (p.getHp() <= 0) {
                System.out.println("Вы погибли. Игра окончена.");
                SaveLoad.save(ctx, worldIndex);
                System.exit(0);
            } else {
                System.out.println("Монстр повержен!");
                r.setMonster(null);
                Item loot = new Random().nextBoolean() ? new Potion("Зелье лечения", 5) : new Weapon("Клинок новичка", 2);
                r.addItem(loot);
                System.out.println("Из монстра выпал лут: " + loot.getName());
                ctx.addScore(10);
            }
        });
        commands.put("save", (ctx, a) -> SaveLoad.save(ctx, worldIndex));
        commands.put("load", (ctx, a) -> SaveLoad.load(ctx, worldIndex));
        commands.put("exit", (ctx, a) -> { SaveLoad.save(ctx, worldIndex); System.out.println("Пока! Ваши очки сохранены."); System.exit(0); });
    }

    private void bootstrapWorld() {
        Room start = new Room("Вход", "Тесный коридор со следами когтей на стенах");
        Room hall = new Room("Зал", "Пыльный зал с обвалившимся потолком");
        Room armory = new Room("Оружейная", "Старые стойки с ржавыми клинками");
        Room lair = new Room("Логово", "Здесь нечто живет... пахнет серой");

        start.connect("east", hall);
        hall.connect("west", start).connect("south", armory).connectLocked("east", lair, "Ржавый ключ");
        armory.connect("north", hall);
        lair.connect("west", hall);

        for (Room r : List.of(start, hall, armory, lair)) worldIndex.put(r.getName(), r);

        hall.addItem(new Key("Ржавый ключ"));
        armory.addItem(new Weapon("Меч ученика", 2));
        lair.setMonster(new Monster("Гоблин", 1, 12));

        Player hero = new Player("Герой", 20, 4);
        state.setPlayer(hero);
        state.setCurrent(start);
    }

    public void run() {
        System.out.println("Добро пожаловать в DungeonMini! Введите 'help' для списка команд.");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                System.out.print("> ");
                String line = in.readLine();
                if (line == null) break;
                line = line.trim();
                if (line.isEmpty()) continue;
                List<String> parts = Arrays.asList(line.split("\\s+"));
                String cmd = parts.getFirst().toLowerCase(Locale.ROOT);
                List<String> args = parts.subList(1, parts.size());
                Command c = commands.get(cmd);
                try {
                    if (c == null) throw new InvalidCommandException("Неизвестная команда: " + cmd);
                    c.execute(state, args);
                    state.addScore(1);
                } catch (InvalidCommandException e) {
                    System.out.println("Ошибка: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("Непредвиденная ошибка: " + e.getClass().getSimpleName() + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка ввода/вывода: " + e.getMessage());
        }
    }
}
