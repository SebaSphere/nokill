package dev.sebastianb.nokillkillkillkill.command.challenge;

import java.util.UUID;

public record PlayerPair(UUID challenger, UUID opponent) {
    public boolean contains(UUID uuid) {
        return (uuid.equals(challenger) || uuid.equals(opponent));
    }
}
