package com.github.shinjoy991.balanced_enchantments.register;

import com.github.shinjoy991.balanced_enchantments.BalancedEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegisterEnch {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS,
                    BalancedEnchantments.MOD_ID);
    public static final EnchantmentType IS_HOE = EnchantmentType.create("is_hoe",
            (item) -> item instanceof HoeItem);
    public static final EnchantmentType IS_BOOTS = EnchantmentType.create("is_boots",
            (item) -> item instanceof ArmorItem && ((ArmorItem) item).getSlot() == EquipmentSlotType.FEET);
    public static final EnchantmentType IS_BOW = EnchantmentType.create("is_bow",
            (item) -> item instanceof BowItem);
    public static final EnchantmentType IS_TOOL_AND_WEAPON = EnchantmentType.create(
            "is_tool_and_weapon",
            (item) -> item instanceof AxeItem
                    || item instanceof HoeItem
                    || item instanceof PickaxeItem
                    || item instanceof ShovelItem
                    || item instanceof SwordItem
                    || item instanceof TridentItem
    );
    public static final EnchantmentType IS_TOOL_AND_WEAPON_AND_ARMOR = EnchantmentType.create(
            "is_tool_and_weapon",
            (item) -> item instanceof AxeItem
                    || item instanceof HoeItem
                    || item instanceof PickaxeItem
                    || item instanceof ShovelItem
                    || item instanceof SwordItem
                    || item instanceof TridentItem
                    || item instanceof ArmorItem
    );
    public static RegistryObject<Enchantment> BLOCK_MASK_SOUL = null;
    public static RegistryObject<Enchantment> VOLLEY = null;
    public static RegistryObject<Enchantment> MAGMA_WALKER = null;
    public static RegistryObject<Enchantment> SUPER_CHARGED = null;
    public static RegistryObject<Enchantment> CURSE_OF_DURABILITY = null;
}