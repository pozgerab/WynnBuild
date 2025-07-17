package com.gertoxq.wynnbuild.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntryListWidget.class)
public class HighlightEntryListHover {

    @Shadow @Final protected MinecraftClient client;

    @Inject(method = "renderEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/EntryListWidget$Entry;drawBorder(Lnet/minecraft/client/gui/DrawContext;IIIIIIIZF)V"))
    private void highlightHover(DrawContext context, int mouseX, int mouseY, float delta, int index, int x, int y, int entryWidth, int entryHeight, CallbackInfo ci) {
        var entry = ((EntryListWidget<?>) (Object) this).children().get(index);
        if (entry.isMouseOver(mouseX, mouseY)) {
            context.fill(x, y, x + entryWidth, y + entryHeight, Formatting.BLACK.getColorIndex());
        }
    }
}
