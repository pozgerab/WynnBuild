package com.gertoxq.wynnbuild.mixins;

import com.gertoxq.wynnbuild.event.ScreenClosed;
import com.gertoxq.wynnbuild.screens.QueryStack;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientCommonNetworkHandler.class)
public class QueryStackHandlerMixin {

    @Inject(method = "sendPacket", at = @At("TAIL"))
    public void a(Packet<?> packet, CallbackInfo ci) {

        if (packet instanceof CloseHandledScreenC2SPacket) {

            if (QueryStack.getQuery().isPresent()) {

                if (QueryStack.getQuery().get().currentQueryPart.getCloseEventAmount() > 1
                        && QueryStack.getQuery().get().closes++ < QueryStack.getQuery().get().currentQueryPart.getCloseEventAmount() - 1) {
                    return;
                }
                QueryStack.getQuery().get().closes = 0;
                QueryStack.ContainerType next = QueryStack.getQuery().get().poll();
                if (next == null) {
                    QueryStack.setQuery(null);
                } else next.runQueryPart();

            } else {
                ScreenClosed.processContainerClose();
            }
        }

    }
}
