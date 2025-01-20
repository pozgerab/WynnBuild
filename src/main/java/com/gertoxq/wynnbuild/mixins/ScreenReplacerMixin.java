package com.gertoxq.wynnbuild.mixins;

import com.gertoxq.wynnbuild.screens.ContainerScreenHandler;
import com.gertoxq.wynnbuild.screens.atree.AtreeScreen;
import com.gertoxq.wynnbuild.screens.atree.AtreeScreenHandler;
import com.gertoxq.wynnbuild.screens.charinfo.CharacterInfoScreen;
import com.gertoxq.wynnbuild.screens.charinfo.CharacterInfoScreenHandler;
import com.gertoxq.wynnbuild.screens.tome.TomeScreen;
import com.gertoxq.wynnbuild.screens.tome.TomeScreenHandler;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.gertoxq.wynnbuild.client.WynnBuildClient.client;

@Mixin(GenericContainerScreen.class)
public class ScreenReplacerMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void inject(GenericContainerScreenHandler handler, PlayerInventory inventory, Text title, CallbackInfo ci) {
        if (handler instanceof ContainerScreenHandler) return;
        String titleString = title.getString();
        if (AtreeScreen.TITLE_PATTERN.matcher(titleString).matches()) {
            client.execute(() -> client.setScreen(new AtreeScreen(new AtreeScreenHandler(handler.syncId, inventory, handler.getInventory()), inventory, title)));
        } else if (CharacterInfoScreen.TITLE_PATTERN.matcher(titleString).matches()) {
            client.execute(() -> client.setScreen(new CharacterInfoScreen(new CharacterInfoScreenHandler(handler.syncId, inventory, handler.getInventory()), inventory, title)));
        } else if (TomeScreen.TITLE_PATTERN.matcher(titleString).matches()) {
            client.execute(() -> client.setScreen(new TomeScreen(new TomeScreenHandler(handler.syncId, inventory, handler.getInventory()), inventory, title)));
        }
    }
}