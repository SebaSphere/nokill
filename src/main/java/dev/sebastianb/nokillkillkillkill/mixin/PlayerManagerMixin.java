package dev.sebastianb.nokillkillkillkill.mixin;


import dev.sebastianb.nokillkillkillkill.ability.NKKKKAbilities;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {

    @Shadow public abstract MinecraftServer getServer();

    @Inject(at = @At(value = "TAIL"), method = "onPlayerConnect")
    private void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        // should only call when player joins for the first time
        if (player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.LEAVE_GAME)) < 1) {
            boolean serverPVPStatus = getServer().isPvpEnabled();

            // set if player can pvp based on what the server config says for first join
            NKKKKAbilities.Abilities.PLAYER_PVP_STATUS_ABILITY.setAbilityState(player, serverPVPStatus);

            // TODO: make configurable message to send player a message that says "PVP is enabled or disabled" for first join
        }
    }

}
