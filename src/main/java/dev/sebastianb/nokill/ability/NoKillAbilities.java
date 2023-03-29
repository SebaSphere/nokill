package dev.sebastianb.nokill.ability;

import io.github.ladysnake.pal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Arrays;

public class NoKillAbilities {

    public enum Abilities {

        PLAYER_PVP_STATUS_ABILITY("pvp_status", false),
        PLAYER_CURRENTLY_CHALLENGING_ABILITY("currently_challenging", false);

        private final Identifier abilityIdentifier;
        private final AbilitySource abilitySource;
        private final PlayerAbility playerAbility;

        Abilities(String abilityID, boolean isUpdatedCallback) {
            this.abilityIdentifier = new Identifier("nokill", abilityID);
            this.abilitySource = Pal.getAbilitySource(abilityIdentifier);
            this.playerAbility = Pal.registerAbility(abilityIdentifier, SimpleAbilityTracker::new);

            if (isUpdatedCallback) {
                // TODO: do stuff if this was actually used
                // PlayerAbilityUpdatedCallback will call PlayerAbilityEnableCallback for every source
                // example inside callback: VanillaAbilities.ALLOW_FLYING.getTracker(player).refresh(true));
            }

        }

        public void setAbilityState(PlayerEntity player, boolean state) {
            if (state) {
                abilitySource.grantTo(player, playerAbility);
            } else {
                abilitySource.revokeFrom(player, playerAbility);
            }
        }

        public boolean getAbilityState(PlayerEntity player) {
            return playerAbility.isEnabledFor(player);
        }

        private void init() {
            // just calling the method in the superclass to register constructor
        }

    }

    public static void register() {
        // Make sure everything is initialized
        Arrays.stream(Abilities.values()).forEach(Abilities::init);

    }

}
