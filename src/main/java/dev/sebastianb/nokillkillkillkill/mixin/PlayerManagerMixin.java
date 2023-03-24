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

            // set if player can pvp based on what the server.properties says for first join
            NKKKKAbilities.Abilities.PLAYER_PVP_STATUS_ABILITY.setAbilityState(player, serverPVPStatus);
            // they shouldn't be challenging a player when first joining to set to false by default
            NKKKKAbilities.Abilities.PLAYER_CURRENTLY_CHALLENGING_ABILITY.setAbilityState(player, false);

            // TODO: make configurable and optional message to send player a message that says "PVP is enabled or disabled" for first join
        }

        // this should call if they left while currently in a match
        if (NKKKKAbilities.Abilities.PLAYER_CURRENTLY_CHALLENGING_ABILITY.getAbilityState(player)) {
            // TODO: let player know they left while in a match

        }
        // everytime a player connects, they should be set to not in a challenge state if they disconnect while in challenge mode
        NKKKKAbilities.Abilities.PLAYER_CURRENTLY_CHALLENGING_ABILITY.setAbilityState(player, false);
    }

    // I think this is the disconnect method, seems to call when a player disconnects
    @Inject(at = @At(value = "TAIL"), method = "remove")
    private void onPlayerDisconnect(ServerPlayerEntity player, CallbackInfo ci) {
        // TODO: have a condition to check if the player is even in the hashmap
        // TODO: end the challenge state of the player that didn't leave and get rid of both from the challenge hashmap
        // TODO: broadcast message with the player that left and who won by default

    }

}
