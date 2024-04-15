package com.github.shinjoy991.balancedenchantments.register;

import com.github.shinjoy991.balancedenchantments.BalancedEnchantments;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.*;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class RegisterItem {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, BalancedEnchantments.MOD_ID);
    public static final RegistryObject<Item> ICON = ITEMS.register(
            "mod_icon",
            () -> new Item(new Item.Properties()) {
                @Override
                public void appendHoverText(ItemStack stack, World world,
                        List<ITextComponent> tooltip, ITooltipFlag flag) {
                    super.appendHoverText(stack, world, tooltip, flag);
                    tooltip.add(new StringTextComponent("This is just an icon, what are you " +
                            "planning for?"));
                }
            }
    );
    public static final ItemGroup ENCHANTMENT_TAB = new ItemGroup("enchantment_tab") {
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
                    items.add(EnchantedBookItem.createForEnchantment(new EnchantmentData(currentEnchantment, level)));
                }
            }

        }
    };
    public static final RegistryObject<BlockItem> CRUSTEDMAGMA = ITEMS.register(
            "crusted_magma_block_0",
            () -> new BlockItem(RegisterBlock.CRUSTEDMAGMA.get(),
                    new Item.Properties().tab(ENCHANTMENT_TAB)));
}