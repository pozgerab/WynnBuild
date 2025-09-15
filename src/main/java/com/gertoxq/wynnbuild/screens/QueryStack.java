package com.gertoxq.wynnbuild.screens;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.screens.aspect.AspectInfo;
import com.gertoxq.wynnbuild.screens.atree.AbilityTreeQuery;
import com.gertoxq.wynnbuild.screens.tome.TomeQuery;
import com.wynntils.core.components.Managers;
import com.wynntils.core.components.Models;

import java.util.LinkedList;
import java.util.Queue;

public class QueryStack {

    final Queue<ContainerType> query = new LinkedList<>();
    public int closes = 0;
    public ContainerType currentQueryPart = null;

    private QueryStack() {
    }

    public static Builder builder() {
        return new QueryStack().new Builder();
    }

    public ContainerType poll() {
        return query.poll();
    }

    public enum ContainerType {
        TOME(() -> new TomeQuery().queryTomeInfo()),
        ATREE(() -> new AbilityTreeQuery().queryTree()),
        ASPECTS(() -> new AspectInfo().queryAspectInfo()),
        SKILLPOINTS((Models.SkillPoint::populateSkillPoints), 2),
        BUILD(WynnBuild::buildAfterSp);

        final int closeEventAmount;
        final Runnable runnable;

        ContainerType(Runnable runnable, int closeEventAmount) {
            this.closeEventAmount = closeEventAmount;
            this.runnable = () -> Managers.TickScheduler.scheduleNextTick(() -> {
                WynnBuild.getQuery().get().currentQueryPart = this;
                runnable.run();
            });
        }

        ContainerType(Runnable runnable) {
            this(runnable, 1);
        }

        public int getCloseEventAmount() {
            return closeEventAmount;
        }

        public void runQueryPart() {
            runnable.run();
        }
    }

    public class Builder {
        public Builder next(ContainerType type) {
            query.add(type);
            return this;
        }

        public void runQuery() {
            WynnBuild.setQuery(QueryStack.this);
            QueryStack.this.poll().runQueryPart();
        }
    }
}
