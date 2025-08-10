package com.gertoxq.wynnbuild.base;

import com.gertoxq.wynnbuild.identifications.TypedID;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ItemSet {

    private final String name;
    private final List<String> itemNames;
    private final List<Map<TypedID<Integer>, Integer>> bonuses;
    // null if all legal, otherwise max pieces that can be worn
    private final @Nullable Integer legalPieceCount;

    public ItemSet(String name, List<String> itemNames, List<Map<TypedID<Integer>, Integer>> bonuses) {
        this(name, itemNames, bonuses, null);
    }

    public ItemSet(String name, List<String> itemNames, List<Map<TypedID<Integer>, Integer>> bonuses, @Nullable Integer legalPieceCount) {
        this.name = name;
        this.itemNames = itemNames;
        this.bonuses = bonuses;
        this.legalPieceCount = legalPieceCount;
    }

    public Optional<Integer> getLegalPieceCount() {
        return Optional.ofNullable(legalPieceCount);
    }

    public int maxPieces() {
        return bonuses.size();
    }

    public Map<TypedID<Integer>, Integer> getBonuses(int pieces) {
        assert pieces >= 0 : "Negative set pieces while getting bonuses";
        if (pieces == 0) {
            return new HashMap<>();
        }
        return bonuses.get(Math.min(pieces, maxPieces()) - 1);
    }
}
