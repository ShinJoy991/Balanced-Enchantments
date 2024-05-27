package com.github.shinjoy991.balanced_enchantments.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;

import static com.github.shinjoy991.balanced_enchantments.config.ReadConfig.reloadConfig;

public class ReloadCommand {

    public ReloadCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("BalancedEnchantmentsReload")
                .executes((command) -> CustomCommand1a(command.getSource())));
    }

    private int CustomCommand1a(CommandSourceStack source) throws CommandSyntaxException {

        ServerPlayer player = source.getPlayerOrException();
        if (player.hasPermissions(4)) {
            if (!player.level.isClientSide && player.level.getServer() != null) {
                if (reloadConfig() == 0) {
                    Component message = new TextComponent("[Balanced Enchantments]")
                            .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD))
                            .append(new TextComponent(" Reloaded").setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)));
                    player.sendMessage(message, player.getUUID());
                } else {
                    Component message = new TextComponent("[Balanced Enchantments]")
                            .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD))
                            .append(new TextComponent(" Reload Error!!").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
                    player.sendMessage(message, player.getUUID());
                }
            }
        }
        return 0;
    }
}