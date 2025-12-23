package com.gertoxq.wynnbuild.webquery;

import com.gertoxq.wynnbuild.webquery.providers.*;
import com.wynntils.models.character.type.ClassType;

public class Providers {

    public static final AspectProvider Aspects = new AspectProvider();
    public static final ItemProvider Items = new ItemProvider();
    public static final MidProvider MajorIds = new MidProvider();
    public static final TomeProvider Tomes = new TomeProvider();
    public static final AtreeProvider Atree = new AtreeProvider();

    public static final ApiDataProvider ArcherTree = new ApiDataProvider(ClassType.ARCHER);
    public static final ApiDataProvider WarriorTree = new ApiDataProvider(ClassType.WARRIOR);
    public static final ApiDataProvider MageTree = new ApiDataProvider(ClassType.MAGE);
    public static final ApiDataProvider AssassinTree = new ApiDataProvider(ClassType.ASSASSIN);
    public static final ApiDataProvider ShamanTree = new ApiDataProvider(ClassType.SHAMAN);

}
