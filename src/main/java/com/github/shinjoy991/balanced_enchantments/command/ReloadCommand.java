package com.github.shinjoy991.balanced_enchantments.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

import static com.github.shinjoy991.balanced_enchantments.config.ReadConfig.reloadConfig;

public class ReloadCommand {

    public ReloadCommand(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("BalancedEnchantmentsReload")
                .executes((command) -> CustomCommand1a(command.getSource())));
    }

    private int CustomCommand1a(CommandSource source) throws CommandSyntaxException {

        ServerPlayerEntity player = source.getPlayerOrException();
        if (player.hasPermissions(4)) {
            if (!player.level.isClientSide && player.level.getServer() != null) {
                if (reloadConfig() == 0) {
                    IFormattableTextComponent message = new StringTextComponent("[Balanced Enchantments]")
                            .setStyle(Style.EMPTY.withColor(TextFormatting.GOLD))
                            .append(new StringTextComponent(" Reloaded").setStyle(Style.EMPTY.withColor(TextFormatting.GREEN)));
                    player.sendMessage(message, player.getUUID());
                } else {
                    IFormattableTextComponent message = new StringTextComponent("[Balanced Enchantments]")
                            .setStyle(Style.EMPTY.withColor(TextFormatting.GOLD))
                            .append(new StringTextComponent(" Reload Error!!").setStyle(Style.EMPTY.withColor(TextFormatting.RED)));
                    player.sendMessage(message, player.getUUID());
                }
            }
        }
        return 0;
    }
}