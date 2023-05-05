package dev.sebastianb.nokill.command.challenge.pairstructs;

import dev.sebastianb.nokill.command.challenge.PlayerPair;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.Optional;

public class PlayerInviteList extends ArrayList<InvitePair> {
    /** Removes an InvitePair that contains the given pair */
    public void remove(PlayerPair pair) throws IndexOutOfBoundsException {
        for (int i = 0; i < this.size(); i++) {
            if (this.get(i).pair.equals(pair)) {
                this.remove(i);
                break;
            }
        }
    }

    public Optional<PlayerPair> find(ServerPlayerEntity player) {
        for (var invite : this) {
            if (invite.pair.contains(player)) return Optional.of(invite.pair);
        }

        return Optional.empty();
    }
}
