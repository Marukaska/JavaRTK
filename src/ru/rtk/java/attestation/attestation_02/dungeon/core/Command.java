package ru.rtk.java.attestation.attestation_02.dungeon.core;

import ru.rtk.java.attestation.attestation_02.dungeon.model.GameState;
import java.util.List;

@FunctionalInterface
public interface Command { void execute(GameState ctx, List<String> args); }
