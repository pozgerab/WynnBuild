package com.gertoxq.wynnbuild.build;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.net.URI;
import java.util.List;

public record BuildState(
        String url,
        int activeAbilities,
        boolean hasAbilityTree,
        List<Boolean> items) {

    private boolean hasItems() {
        return items.stream().allMatch(e -> e);
    }

    public boolean isComplete() {
        return hasAbilityTree && hasItems();
    }

    public Text finalText() {
        return Text.empty()
                .append(StatusLine.build(this))
                .append("\n")
                .append(ActionBar.build(url, this))
                .append("\n")
                .append(SummaryLine.build(this));
    }

    public static class StatusLine {
        private static final String[] GEAR_NAMES = {
                "Helmet",
                "Chestplate",
                "Leggings",
                "Boots",
                "Ring 1",
                "Ring 2",
                "Bracelet",
                "Necklace"
        };

        public static Text build(BuildState state) {
            if (state.isComplete()) {
                return Text.literal("[✔ Build Ready]")
                        .formatted(Formatting.GREEN, Formatting.BOLD);
            }

            StringBuilder missing = new StringBuilder();

            if (!state.hasAbilityTree()) missing.append("Ability Tree, ");

            for (int i = 0; i < GEAR_NAMES.length; i++) {
                if (!state.items.get(i)) {
                    missing.append(GEAR_NAMES[i]).append(", ");
                }
            }

            String msg = missing.substring(0, missing.length() - 2);

            return Text.literal("[⚠ Incomplete: " + msg + "]")
                    .styled(style -> style
                            .withColor(Formatting.YELLOW)
                            .withBold(true)
                            .withHoverEvent(new HoverEvent.ShowText(
                                    Text.literal("Missing: " + msg)
                                            .formatted(Formatting.GRAY)
                            )));
        }
    }

    public static class ActionBar {
        public static Text build(String url, BuildState state) {
            return Text.empty()
                    .append(button("Options", Formatting.DARK_AQUA,
                            "/build config", "Open config"))
                    .append(space())
                    .append(button("Help", Formatting.RED,
                            "/build issue", "Click for help"))
                    .append(Text.literal("     "))
                    .append(copy(url))
                    .append(space())
                    .append(open(url))
                    .append(space())
                    .append(refresh(state));
        }

        private static Text button(String text, Formatting color, String cmd, String hover) {
            return Text.literal("[" + text + "]")
                    .styled(style -> style
                            .withColor(color)
                            .withBold(true)
                            .withClickEvent(new ClickEvent.RunCommand(cmd))
                            .withHoverEvent(new HoverEvent.ShowText(
                                    Text.literal(hover).formatted(Formatting.GRAY)
                            )));
        }

        private static Text copy(String url) {
            return Text.literal("COPY")
                    .styled(style -> style
                            .withColor(Formatting.GREEN)
                            .withUnderline(true)
                            .withClickEvent(new ClickEvent.CopyToClipboard(url))
                            .withHoverEvent(new HoverEvent.ShowText(
                                    Text.literal(url).formatted(Formatting.GRAY)
                            )));
        }

        private static Text open(String url) {
            return Text.literal("OPEN")
                    .styled(style -> style
                            .withColor(Formatting.RED)
                            .withUnderline(true)
                            .withClickEvent(new ClickEvent.OpenUrl(URI.create(url))));
        }

        private static Text refresh(BuildState state) {
            String label = state.hasAbilityTree() ? "REFRESH" : "GENERATE TREE";

            return Text.literal(label)
                    .styled(style -> style
                            .withColor(Formatting.YELLOW)
                            .withUnderline(true)
                            .withClickEvent(new ClickEvent.RunCommand("/build withAtreeRefresh")));
        }

        private static Text space() {
            return Text.literal(" ");
        }
    }

    public static class SummaryLine {
        public static Text build(BuildState state) {
            return Text.empty()
                    .append(indicator("Abilities Active",
                            String.valueOf(state.activeAbilities()),
                            state.hasAbilityTree()))
                    .append(Text.literal("   "))
                    .append(indicator("Items", null, state.hasItems()));
        }

        private static Text indicator(String label, String value, boolean ok) {
            Formatting color = ok ? Formatting.GREEN : Formatting.RED;
            String symbol = ok ? "✔" : "✖";

            return Text.literal(label + ": ")
                    .formatted(Formatting.GRAY)
                    .append(Text.literal(
                            (value != null ? value + " " : "") + symbol
                    ).formatted(color, Formatting.BOLD));
        }
    }
}
