package com.github.shinjoy991.balanced_enchantments.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

import static com.github.shinjoy991.balanced_enchantments.BalancedEnchantments.MOD_ID;


public class RegisterItem {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
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

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final RegistryObject<CreativeModeTab>  ENCHANTMENT_TAB = CREATIVE_MODE_TAB.register("prehistorica_tab",
            () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(RegisterItem.ICON.get()))
            .displayItems((parameters, output) -> {
                output.accept(new ItemStack(RegisterItem.CRUSTEDMAGMA.get()));
                for (RegistryObject<Enchantment> enchantment : RegisterEnch.ENCHANTMENTS.getEntries()) {
                    Enchantment currentEnchantment = enchantment.get();
                    for (int level = 1; level <= currentEnchantment.getMaxLevel(); level++) {
                        output.accept(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(currentEnchantment, level)));
                    }
                }
            })
            .title(Component.translatable("itemGroup.enchantment_tab"))
            .build()
    );


//        @Override
//        public void fillItemList(NonNullList<ItemStack> items) {
//            items.add(new ItemStack(RegisterItem.CRUSTEDMAGMA.get()));
//            for (RegistryObject<Enchantment> enchantment : RegisterEnch.ENCHANTMENTS.getEntries()) {
//                Enchantment currentEnchantment = enchantment.get();
//                for (int level = 1; level <= currentEnchantment.getMaxLevel(); level++) {
//                    items.add(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(currentEnchantment, level)));
//                }
//            }
//        }
    ;


//    public static final CreativeModeTab ENCHANTMENT_TAB = CreativeModeTab.builder()
//            .icon(() -> new ItemStack(RegisterItem.ICON.get()))
//            .displayItems((features, output, hasPermissions) -> {
//                output.accept(new ItemStack(RegisterItem.CRUSTEDMAGMA.get()));
//                for (RegistryObject<Enchantment> enchantment : RegisterEnch.ENCHANTMENTS.getEntries()) {
//                    Enchantment currentEnchantment = enchantment.get();
//                    for (int level = 1; level <= currentEnchantment.getMaxLevel(); level++) {
//                        output.accept(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(currentEnchantment, level)));
//                    }
//                }
//            })
//            .title(Component.translatable("itemGroup.enchantment_tab"))
//            .build();


//    public static final RegistryObject<BlockItem> CRUSTEDMAGMA = ITEMS.register(
//            "crusted_magma_block_0",
//            () -> new BlockItem(RegisterBlock.CRUSTEDMAGMA.get(),
//                    new Item.Properties().tab(ENCHANTMENT_TAB)));
    public static final RegistryObject<BlockItem> CRUSTEDMAGMA = ITEMS.register(
            "crusted_magma_block_0",
            () -> new BlockItem(RegisterBlock.CRUSTEDMAGMA.get(), new Item.Properties()));
}