package com.gertoxq.wynnbuild.screens;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.build.AtreeCoder;
import com.gertoxq.wynnbuild.screens.atree.Ability;
import com.wynntils.core.components.Models;
import com.wynntils.models.abilitytree.type.AbilityTreeInfo;
import com.wynntils.models.abilitytree.type.AbilityTreeNodeState;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class AtreeManager {

    private Set<Integer> atreeState;

    private AtreeCoder getAtreeCoder() {
        return AtreeCoder.getAtreeCoder(Models.Character.getClassType());
    }

    public Set<Integer> getState() {
        return new HashSet<>(atreeState);
    }

    public void addAbility(Ability ability) {
        atreeState.add(ability.id());
    }

    public void removeAbility(Ability ability) {
        atreeState.remove(ability.id());
    }

    public void clear() {
        atreeState.clear();
    }

    public void clearCache() {
        clear();
        saveCache();
    }

    public boolean isEmpty() {
        return atreeState.isEmpty();
    }

    public String encode() {
        return getAtreeCoder().encode_atree_reqs(atreeState).toB64();
    }

    public void saveCache() {
        WynnBuild.getConfigManager().getConfig().addTreeCache(getSuffix());
        WynnBuild.getConfigManager().saveConfig();
    }

    public String getSuffix() {
        return getAtreeCoder().encode_atree(atreeState).toB64();
    }

    public static Optional<String> getCachedAtree() {
        String cached = WynnBuild.getConfig().getProfileIdAtreeCache().get(Models.Character.getId());
        if (cached == null) return Optional.empty();
        return Optional.of(cached);
    }

    public void setFromCache() {
        atreeState = getCachedAtree().map(treeCode -> getAtreeCoder().decode_atree(treeCode)).orElse(new HashSet<>());
    }

    public void setFromCode(String code) {
        atreeState = getAtreeCoder().decode_atree(code);
    }

    public void setFromAbilityTreeInfo(AbilityTreeInfo treeInfo) {
        atreeState = treeInfo.nodes().stream()
                .filter(node -> node.abilityTreeNodeType().getState() == AbilityTreeNodeState.UNLOCKED)
                .map(Ability::idFromNode).collect(Collectors.toSet());
    }

    public int unlockedNodeAmount() {
        return atreeState.size();
    }

}
