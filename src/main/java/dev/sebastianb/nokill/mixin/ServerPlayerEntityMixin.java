package dev.sebastianb.nokill.mixin;

import dev.sebastianb.nokill.ability.NoKillAbilities;
import dev.sebastianb.nokill.command.challenge.ChallengeCommand;
import dev.sebastianb.nokill.state.ChallengesState;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
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

        boolean isVictimPvpAbilityEnabled = NoKillAbilities.Abilities.PLAYER_PVP_STATUS_ABILITY.getAbilityState((ServerPlayerEntity)(Object)this);
        boolean isAttackerPvpAbilityEnabled = NoKillAbilities.Abilities.PLAYER_PVP_STATUS_ABILITY.getAbilityState(attacker);

        boolean isVictimChallengeAbilityEnabled = NoKillAbilities.Abilities.PLAYER_CURRENTLY_CHALLENGING_ABILITY.getAbilityState((ServerPlayerEntity)(Object)this);
        boolean isAttackerChallengeAbilityEnabled = NoKillAbilities.Abilities.PLAYER_CURRENTLY_CHALLENGING_ABILITY.getAbilityState(attacker);

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
        // IDK if casting is the best approach, but it works... taken from:
        // https://docs.spongepowered.org/stable/en/contributing/implementation/mixins.html#
        var player = (ServerPlayerEntity) (Object) this;

        // get rid of player from the challenges and set both player challenge states
        var maybePair = ChallengeCommand.challenges.find(player);
        if (maybePair.isEmpty()) return; // player was not in a duel.

        var pair = maybePair.get();
        // by default if you die first (whatever cause) the other player is the winner, maybe in the future we could
        // change this to be only if you die by your opponent
        var winner = player == pair.challenger() ? pair.opponent() : pair.challenger();
        ChallengeCommand.challenges.remove(pair); // remove pair from challenges list
        ChallengesState.doWithPlayerState(winner, playerState -> playerState.challengeWins++); // count as new win

        NoKillAbilities.Abilities.PLAYER_CURRENTLY_CHALLENGING_ABILITY.setAbilityState(pair.challenger(), false);
        NoKillAbilities.Abilities.PLAYER_CURRENTLY_CHALLENGING_ABILITY.setAbilityState(pair.opponent(), false);

        // broadcast who won the duel
        for (var p : player.server.getPlayerManager().getPlayerList())  {
            if (pair.contains(p)) continue; // tell everyone except winner/loser
            p.sendMessage(Text.translatable("nokill.command.pvp.challenge.broadcast_win", winner.getName(), player.getName()));
        }

        player.sendMessage(Text.translatable("nokill.command.pvp.challenge.lost", winner.getName()));
        winner.sendMessage(Text.translatable("nokill.command.pvp.challenge.won", player.getName()));
    }
}
