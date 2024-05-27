package com.github.shinjoy991.balanced_enchantments.register;

import com.github.shinjoy991.balanced_enchantments.BalancedEnchantments;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class RegisterItem {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, BalancedEnchantments.MOD_ID);
    public static final RegistryObject<Item> ICON = ITEMS.register(
            "mod_icon",
            () -> new Item(new Item.Properties()) {
                @Override
                public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag) {
                    super.appendHoverText(stack, world, tooltip, flag);
                    tooltip.add(Component.literal("This is just an icon, what are you planning for?"));
                }
            }
    );
    public static final CreativeModeTab ENCHANTMENT_TAB = new CreativeModeTab("enchantment_tab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(RegisterItem.ICON.get());
        }

        @Override
        public void fillItemList(NonNullList<ItemStack> items) {
            items.add(new ItemStack(RegisterItem.CRUSTEDMAGMA.get()));
            for (RegistryObject<Enchantment> enchantment : RegisterEnch.ENCHANTMENTS.getEntries()) {
                Enchantment currentEnchantment = enchantment.get();
                for (int level = 1; level <= currentEnchantment.getMaxLevel(); level++) {
                    items.add(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(currentEnchantment, level)));
                }
            }

        }
    };
    public static final RegistryObject<BlockItem> CRUSTEDMAGMA = ITEMS.register(
            "crusted_magma_block_0",
            () -> new BlockItem(RegisterBlock.CRUSTEDMAGMA.get(),
                    new Item.Properties().tab(ENCHANTMENT_TAB)));
}