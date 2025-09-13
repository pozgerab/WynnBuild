package com.gertoxq.wynnbuild.mixins;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.screens.tome.TomeQuery;
import com.wynntils.core.components.Managers;
import com.wynntils.core.components.Models;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ClientCommonNetworkHandler.class)
public class AfterSpBuildMixin {

    @Inject(method = "sendPacket", at = @At("TAIL"))
    public void a(Packet<?> packet, CallbackInfo ci) {
        if (packet instanceof CloseHandledScreenC2SPacket) {
            if (Objects.equals(WynnBuild.dataReads, 0)) {
                WynnBuild.dataReads = 1;
                Managers.TickScheduler.scheduleNextTick(() -> new TomeQuery().queryTomeInfo());
            } else if (Objects.equals(WynnBuild.dataReads, 1)) {
                WynnBuild.dataReads = 2;
                Managers.TickScheduler.scheduleNextTick(Models.SkillPoint::populateSkillPoints);
            } else if (Objects.equals(WynnBuild.dataReads, 2)) {
                WynnBuild.dataReads = null;
                Managers.TickScheduler.scheduleLater(WynnBuild::buildAfterSp, 5);
            }
        }

    }
}
