package com.gertoxq.wynnbuild.mixins;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.client.WynnBuildClient;
import com.gertoxq.wynnbuild.util.DebugContainer;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.input.KeyInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public class KeyboardPress {

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void onKeyPressed(KeyInput input, CallbackInfoReturnable<Boolean> cir) {
        if (WynnBuild.isDebug() && WynnBuildClient.SAVE_ITEM_JSON_KEYBIND.matchesKey(input)) {
            DebugContainer.snapshotItem();
            cir.setReturnValue(true);
        }
    }
}
