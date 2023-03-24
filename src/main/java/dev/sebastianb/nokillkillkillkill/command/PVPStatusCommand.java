package dev.sebastianb.nokillkillkillkill.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.sebastianb.nokillkillkillkill.SebaUtils;
import dev.sebastianb.nokillkillkillkill.ability.NKKKKAbilities;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

// TODO: prevent any command from running when in challenge mode and message the player saying "You can't do this while challenging another player"
public class PVPStatusCommand implements ICommand {

    @Override
    public String commandName() {
        return "status";
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> registerNode() {
        return CommandManager.literal(commandName())
                .executes(this::execute);
    }

    private int execute(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        boolean playerPVPStatus = NKKKKAbilities.Abilities.PLAYER_PVP_STATUS_ABILITY.getAbilityState(player);

        if (playerPVPStatus) {
            SebaUtils.ChatUtils.saySimpleMessage(context, Text.translatable("nokillkillkillkill.command.pvp.status_enabled"));
        } else {
            SebaUtils.ChatUtils.saySimpleMessage(context, Text.translatable("nokillkillkillkill.command.pvp.status_disabled"));
        }
        return Command.SINGLE_SUCCESS;
    }

    static class PVPOnCommand implements ICommand {

        @Override
        public String commandName() {
            return "on";
        }

        @Override
        public LiteralArgumentBuilder<ServerCommandSource> registerNode() {
            return CommandManager.literal(commandName())
                    .executes(this::execute);
        }

        private int execute(CommandContext<ServerCommandSource> context) {
            ServerPlayerEntity player = context.getSource().getPlayer();
            boolean playerPVPStatus = NKKKKAbilities.Abilities.PLAYER_PVP_STATUS_ABILITY.getAbilityState(player);

            if (!playerPVPStatus) {
                SebaUtils.ChatUtils.saySimpleMessage(context, Text.translatable("nokillkillkillkill.command.pvp.enabled"));
            } else {
                SebaUtils.ChatUtils.saySimpleMessage(context, Text.translatable("nokillkillkillkill.command.pvp.already_enabled"));
            }

            NKKKKAbilities.Abilities.PLAYER_PVP_STATUS_ABILITY.setAbilityState(player, true);
            return Command.SINGLE_SUCCESS;
        }

    }

    static class PVPOffCommand implements ICommand {

        @Override
        public String commandName() {
            return "off";
        }

        @Override
        public LiteralArgumentBuilder<ServerCommandSource> registerNode() {
            return CommandManager.literal(commandName())
                    .executes(this::execute);
        }

        private int execute(CommandContext<ServerCommandSource> context) {
            ServerPlayerEntity player = context.getSource().getPlayer();
            boolean playerPVPStatus = NKKKKAbilities.Abilities.PLAYER_PVP_STATUS_ABILITY.getAbilityState(player);

            if (playerPVPStatus) {
                SebaUtils.ChatUtils.saySimpleMessage(context, Text.translatable("nokillkillkillkill.command.pvp.disabled"));
            } else {
                SebaUtils.ChatUtils.saySimpleMessage(context, Text.translatable("nokillkillkillkill.command.pvp.already_disabled"));
            }

            NKKKKAbilities.Abilities.PLAYER_PVP_STATUS_ABILITY.setAbilityState(player, false);
            return Command.SINGLE_SUCCESS;
        }

    }

}
