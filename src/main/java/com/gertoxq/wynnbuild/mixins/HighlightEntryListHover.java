package com.gertoxq.wynnbuild.mixins;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.EntryListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntryListWidget.class)
public class HighlightEntryListHover {

    @Inject(method = "renderEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/EntryListWidget$Entry;drawBorder(Lnet/minecraft/client/gui/DrawContext;IIIIIIIZF)V"))
    private void highlightHover(DrawContext context, int mouseX, int mouseY, float delta, int index, int x, int y, int entryWidth, int entryHeight, CallbackInfo ci) {
        var entry = ((EntryListWidget<?>) (Object) this).children().get(index);
        if (entry.isMouseOver(mouseX, mouseY)) {
            context.fill(x, y, x + entryWidth, y + entryHeight, 0x808080);
        }
    }
}
