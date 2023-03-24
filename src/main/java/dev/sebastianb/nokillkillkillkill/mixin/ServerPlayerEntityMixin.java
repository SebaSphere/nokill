package dev.sebastianb.nokillkillkillkill.mixin;


import dev.sebastianb.nokillkillkillkill.ability.NKKKKAbilities;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    // taken from https://github.com/eliskvitka/toggle-pvp/blob/master/src/main/java/me/braunly/togglepvp/mixin/ServerPlayerEntityMixin.java
    @Inject(at = @At("HEAD"), method = "shouldDamagePlayer", cancellable = true)
    private void shouldDamagePlayer(PlayerEntity attacker, CallbackInfoReturnable<Boolean> cir) {

        // FIXME: make it so when both players are in challenge mode, no matter the PVP status, they have pvp enabled globally (without changing PLAYER_PVP_STATUS_ABILITY)
        // currently, this only allows challenge mode players to attack each-other.
        // If pvp status is on, they can attack every player with PVP on but only can attack each-other with it off

        boolean isVictimPvpEnabled = false;
        boolean isAttackerPvpEnabled = false;

        boolean isVictimPvpAbilityEnabled = NKKKKAbilities.Abilities.PLAYER_PVP_STATUS_ABILITY.getAbilityState((ServerPlayerEntity)(Object)this);
        boolean isAttackerPvpAbilityEnabled = NKKKKAbilities.Abilities.PLAYER_PVP_STATUS_ABILITY.getAbilityState(attacker);

        boolean isVictimChallengeAbilityEnabled = NKKKKAbilities.Abilities.PLAYER_CURRENTLY_CHALLENGING_ABILITY.getAbilityState((ServerPlayerEntity)(Object)this);
        boolean isAttackerChallengeAbilityEnabled = NKKKKAbilities.Abilities.PLAYER_CURRENTLY_CHALLENGING_ABILITY.getAbilityState(attacker);

        if (isVictimPvpAbilityEnabled && isAttackerPvpAbilityEnabled) {
            isVictimPvpEnabled = true;
            isAttackerPvpEnabled = true;
        } else if (isVictimChallengeAbilityEnabled && isAttackerChallengeAbilityEnabled) {
            isVictimPvpEnabled = true;
            isAttackerPvpEnabled = true;
        } else if (isVictimChallengeAbilityEnabled) {
            isVictimPvpEnabled = true;
        }
        cir.setReturnValue(isVictimPvpEnabled && isAttackerPvpEnabled);
    }

    @Inject(at = @At("HEAD"), method = "onDeath", cancellable = true)
    private void onDeath(DamageSource damageSource, CallbackInfo ci) {
        // TODO: do logic if player died to get rid of them from the challenge hashmap and set both player challenge s
        // TODO: broadcast who won the duel
    }

}
