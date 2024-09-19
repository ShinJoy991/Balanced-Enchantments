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
    public static final EnchantmentType IS_BOW_CROSSBOW = EnchantmentType.create(
            "is_bow_crossbow",
            (item) -> item instanceof BowItem
                    || item instanceof CrossbowItem);
    public static final EnchantmentType IS_SWORD_TRIDENT_AXE = EnchantmentType.create(
            "is_sword_trident_axe",
            (item) -> item instanceof AxeItem
                    || item instanceof SwordItem
                    || item instanceof TridentItem);
    public static final EnchantmentType IS_TOOL = EnchantmentType.create(
            "is_tool",
            (item) -> item instanceof AxeItem
                    || item instanceof HoeItem
                    || item instanceof PickaxeItem
                    || item instanceof ShovelItem);
    public static final EnchantmentType IS_PICKAXE = EnchantmentType.create(
            "is_pickaxe",
            (item) -> item instanceof PickaxeItem);
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
    public static RegistryObject<Enchantment> HUNTING_INSTINCT = null;
    public static RegistryObject<Enchantment> TRUE_SHARPNESS = null;
    public static RegistryObject<Enchantment> MIGHTY_FORCE = null;
    public static RegistryObject<Enchantment> STATUS_PROTECTION = null;
    public static RegistryObject<Enchantment> BLOCK_MASK_SOUL = null;
    public static RegistryObject<Enchantment> VOLLEY = null;
    public static RegistryObject<Enchantment> MAGMA_WALKER = null;
    public static RegistryObject<Enchantment> SUPER_CHARGED = null;
    public static RegistryObject<Enchantment> HERO_LANDING = null;
    public static RegistryObject<Enchantment> C_MOON = null;
    public static RegistryObject<Enchantment> WEAPON_ART = null;
    public static RegistryObject<Enchantment> ICE_ASPECT = null;
    public static RegistryObject<Enchantment> INVISIBLE_SHIELD = null;
    public static RegistryObject<Enchantment> MINING_FOCUS = null;
    public static RegistryObject<Enchantment> AUTO_MACHINE = null;
    public static RegistryObject<Enchantment> LINKING = null;
    public static RegistryObject<Enchantment> CURSE_OF_PROVOCATION = null;
    public static RegistryObject<Enchantment> CURSE_OF_DURABILITY = null;
    public static RegistryObject<Enchantment> CURSE_OF_UNSTABLE = null;
}