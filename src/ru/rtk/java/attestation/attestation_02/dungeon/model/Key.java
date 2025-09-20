package ru.rtk.java.attestation.attestation_02.dungeon.model;

public class Key extends Item {
    public Key(String name) { super(name); }

    @Override
    public void apply(GameState ctx) {
        var room = ctx.getCurrent();
        var opt = room.getExits().entrySet().stream()
                .filter(e -> e.getValue().locked() && (e.getValue().keyName() == null || e.getValue().keyName().equalsIgnoreCase(getName())))
                .findFirst();
        if (opt.isPresent()) {
            opt.get().getValue().unlock();
            System.out.println("Вы открыли дверь на '" + opt.get().getKey() + "' ключом '" + getName() + "'.");
            ctx.getPlayer().getInventory().remove(this);
        } else {
            System.out.println("Ключ '" + getName() + "' пока некуда применить.");
        }
    }
}
