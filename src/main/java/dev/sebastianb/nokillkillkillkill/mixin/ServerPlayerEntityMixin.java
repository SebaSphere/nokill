package dev.sebastianb.nokillkillkillkill.mixin;


import dev.sebastianb.nokillkillkillkill.ability.NKKKKAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Shadow protected abstract boolean isPvpEnabled();

    // taken from https://github.com/eliskvitka/toggle-pvp/blob/master/src/main/java/me/braunly/togglepvp/mixin/ServerPlayerEntityMixin.java
    @Inject(at = @At("HEAD"), method = "shouldDamagePlayer", cancellable = true)
    private void shouldDamagePlayer(PlayerEntity attacker, CallbackInfoReturnable<Boolean> cir) {

        boolean isVictimPvpEnabled = NKKKKAbilities.Abilities.PLAYER_PVP_STATUS_ABILITY.getAbilityState((ServerPlayerEntity)(Object)this);
        boolean isAttackerPvpEnabled = NKKKKAbilities.Abilities.PLAYER_PVP_STATUS_ABILITY.getAbilityState(attacker);

        cir.setReturnValue(isVictimPvpEnabled && isAttackerPvpEnabled);
    }

}
