package me.flukky.moreupgrade.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import me.flukky.moreupgrade.MoreUpgrade;

public class SmithingListener implements Listener {
    private final MoreUpgrade plugin;
    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

    public SmithingListener(MoreUpgrade plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPrepareSmithing(PrepareSmithingEvent event) {
        ItemStack result = null;
        ItemStack baseItem = event.getInventory().getItem(1); // อาวุธ
        ItemStack upgradeMaterial = event.getInventory().getItem(2); // วัสดุอัพเกรด
        Player player = (Player) event.getViewers().get(0);

        if (baseItem != null && upgradeMaterial != null) {
            Material inputMaterial = baseItem.getType();
            
            if (plugin.getConfig().getBoolean("upgrade_settings.show_debug")) {
                player.sendMessage(ChatColor.YELLOW + "Input Material: " + inputMaterial);
            }

            if (isArmor(inputMaterial)) {

            }

            NamespacedKey upgradeKey = new NamespacedKey(plugin, "upgrade_level");
            FileConfiguration armorConfig = plugin.getArmorConfig(); // ใช้ getArmorConfig()
            for (String key : armorConfig.getKeys(false)) {
                if (inputMaterial.name().equalsIgnoreCase(armorConfig.getString(key + ".material"))) {
                    result = baseItem.clone();
                    ItemMeta meta = result.getItemMeta();
                    PersistentDataContainer data = meta.getPersistentDataContainer();
                    int currentLevel = data.getOrDefault(upgradeKey, PersistentDataType.INTEGER, 0);
                    int maxLevel = armorConfig.getInt(key + ".max_upgrade_level");
                    String upgradePath = key + ".upgrade_levels.level_" + (currentLevel + 1) + ".";
                    int failUpgrade = armorConfig.getInt(upgradePath + "fail_upgrade", 0); // ค่าเริ่มต้นคือ 0% ล้มเหลว
                    Random random = new Random();
                    String displayName = armorConfig.getString(key + ".name");

                    double baseDefender = armorConfig.getDouble(key + ".base_defense");
                    double baseToughness = armorConfig.getDouble(key + ".base_defense_toughess");
                    double baseKnockback = armorConfig.getDouble(key + ".base_defense_knockback");
                    double baseDurability = armorConfig.getDouble(key + ".durability");
                    meta.setDisplayName(ChatColor.DARK_PURPLE + displayName + " +" + ChatColor.AQUA + (currentLevel + 1) + "");

                    if (plugin.getConfig().getBoolean("upgrade_settings.show_debug")) {
                        player.sendMessage(ChatColor.YELLOW + "Current Level: " + currentLevel + ", Max Level: " + maxLevel);
                    }

                    if (currentLevel < maxLevel) {
                        String requiredMaterialName = armorConfig.getString(upgradePath + "required_material");
                        Material requiredMaterial = Material.getMaterial(requiredMaterialName);
                        Integer customModelData = armorConfig.getInt(upgradePath + "custom_model_data");

                        double defenderIncrease = armorConfig.getDouble(upgradePath + "defender_increase");
                        double defender_Toughness_Increase = armorConfig.getDouble(upgradePath + "defender_toughness_increase");
                        double defender_knockback_increase = armorConfig.getDouble(upgradePath + "defender_knockback_increase");
                        int durabilityIncrease = armorConfig.getInt(upgradePath + "durability_increase");
                        Integer requiredCustomModelData = armorConfig.getInt(upgradePath + "required_custom_model_data");

                        Integer poisonChance = armorConfig.getInt(upgradePath + "effect_upgrade.poison.chance");
                        Integer reflectPoisonChance = armorConfig.getInt(upgradePath + "effect_upgrade.reflect_poison.chance");

                        if (plugin.getConfig().getBoolean("upgrade_settings.show_debug")) {
                            player.sendMessage(ChatColor.YELLOW + "Required Material: " + requiredMaterial);
                        }

                        if (upgradeMaterial.getType() == requiredMaterial) {
                            ItemMeta upgradeMeta = upgradeMaterial.getItemMeta();
                            if (upgradeMeta.hasCustomModelData() && requiredCustomModelData != null && upgradeMeta.getCustomModelData() == requiredCustomModelData) {
                                if (random.nextInt(100) < failUpgrade) {
                                    player.sendMessage(ChatColor.RED + "Upgrade failed! Try again.");

                                    ItemStack materialItem = event.getInventory().getItem(2);
                                    if (materialItem != null && materialItem.getAmount() > 0) {
                                        materialItem.setAmount(materialItem.getAmount() - 1);
                                        event.getInventory().setItem(2, materialItem.getAmount() == 0 ? null : materialItem);
                                    }
                                    return; // ออกจากฟังก์ชัน
                                } else {
                                    EquipmentSlot slot;

                                    if (inputMaterial == Material.LEATHER_HELMET || inputMaterial == Material.IRON_HELMET || inputMaterial == Material.DIAMOND_HELMET || inputMaterial == Material.NETHERITE_HELMET || inputMaterial == Material.TURTLE_HELMET) {
                                        slot = EquipmentSlot.HEAD;
                                    } else if (inputMaterial == Material.LEATHER_CHESTPLATE || inputMaterial == Material.IRON_CHESTPLATE || inputMaterial == Material.DIAMOND_CHESTPLATE || inputMaterial == Material.NETHERITE_CHESTPLATE) {
                                        slot = EquipmentSlot.CHEST;
                                    } else if (inputMaterial == Material.LEATHER_LEGGINGS || inputMaterial == Material.IRON_LEGGINGS || inputMaterial == Material.DIAMOND_LEGGINGS || inputMaterial == Material.NETHERITE_LEGGINGS) {
                                        slot = EquipmentSlot.LEGS;
                                    } else if (inputMaterial == Material.LEATHER_BOOTS || inputMaterial == Material.IRON_BOOTS || inputMaterial == Material.DIAMOND_BOOTS || inputMaterial == Material.NETHERITE_BOOTS) {
                                        slot = EquipmentSlot.FEET;
                                    } else {
                                        return; // หากไม่ตรงกับเงื่อนไขใดๆ
                                    }

                                    currentLevel++;
                                    data.set(upgradeKey, PersistentDataType.INTEGER, currentLevel);

                                    if (customModelData != null) {
                                        meta.setCustomModelData(customModelData);
                                        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "custom_model_data"), PersistentDataType.INTEGER, customModelData);
                                        
                                        if (plugin.getConfig().getBoolean("upgrade_settings.show_debug")) {
                                            player.sendMessage(ChatColor.GREEN + "Custom Model Data: " + customModelData + "!");
                                        }
                                    }

                                    double totalDefender = baseDefender + (defenderIncrease * currentLevel); // เพิ่ม damageIncrease ตามระดับ
                                    double totalToughness = baseToughness + (defender_Toughness_Increase * currentLevel); // เพิ่ม damageIncrease ตามระดับ
                                    double totalKnockback = baseKnockback + (defender_knockback_increase * currentLevel); // เพิ่ม damageIncrease ตามระดับ
                                    double totalDurability = baseDurability + (durabilityIncrease * currentLevel); // เพิ่ม damageIncrease ตามระดับ

                                    Collection<AttributeModifier> defenderModifiers = meta.getAttributeModifiers(Attribute.GENERIC_ARMOR);
                                    if (defenderModifiers != null) {
                                        for (AttributeModifier modifier : defenderModifiers) {
                                            meta.removeAttributeModifier(Attribute.GENERIC_ARMOR, modifier);
                                        }
                                    }

                                    Collection<AttributeModifier> defender_Toughness_Modifiers = meta.getAttributeModifiers(Attribute.GENERIC_ARMOR_TOUGHNESS);
                                    if (defender_Toughness_Modifiers != null) {
                                        for (AttributeModifier modifier : defender_Toughness_Modifiers) {
                                            meta.removeAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, modifier);
                                        }
                                    }

                                    Collection<AttributeModifier> defender_Knockback_Modifiers = meta.getAttributeModifiers(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
                                    if (defender_Knockback_Modifiers != null) {
                                        for (AttributeModifier modifier : defender_Knockback_Modifiers) {
                                            meta.removeAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, modifier);
                                        }
                                    }

                                    if (plugin.getConfig().getBoolean("upgrade_settings.show_debug")) {
                                        player.sendMessage(ChatColor.AQUA + "defenderModifiers " + defenderModifiers);
                                        player.sendMessage(ChatColor.AQUA + "defender_Toughness_Modifiers " + defender_Toughness_Modifiers);
                                        player.sendMessage(ChatColor.AQUA + "defender_Knockback_Modifiers " + defender_Knockback_Modifiers);
                                    }

                                    List<String> lore = new ArrayList<>();
                                    lore.add("");
                                    lore.add(ChatColor.GOLD + "★ " + ChatColor.RED + "Powerful armors"); // ชื่อบ่งบอกถึงพลังของอาวุธ
                                    lore.add(ChatColor.YELLOW + "Defender: +" + String.format("%.2f", totalDefender)); // ความเร็วที่เพิ่มขึ้น
                                    lore.add(ChatColor.AQUA + "Toughness: +" + String.format("%.2f", totalToughness)); // ความเร็วที่เพิ่มขึ้น
                                    lore.add(ChatColor.LIGHT_PURPLE + "Knockback: +" + String.format("%.2f", totalKnockback)); // ความเร็วที่เพิ่มขึ้น
                                    lore.add(ChatColor.BLUE + "Durability: +" + totalDurability); // ความเร็วที่เพิ่มขึ้น
                                    lore.add(ChatColor.RED + "Poison Chance: " + poisonChance + "%"); // โอกาสติดพิษ
                                    lore.add(ChatColor.RED + "Reflect Poison: " + reflectPoisonChance + "%"); // โอกาสสะท้อนพิษ
                                    lore.add(ChatColor.GRAY + "─────────────");
                                    meta.setLore(lore);

                                    meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "level"), PersistentDataType.INTEGER, currentLevel);
                    
                                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), "generic.armor", totalDefender, AttributeModifier.Operation.ADD_NUMBER, slot));
                                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier(UUID.randomUUID(), "generic.armorToughness", totalToughness, AttributeModifier.Operation.ADD_NUMBER, slot));
                                    meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier(UUID.randomUUID(), "generic.knockbackResistance", totalKnockback, AttributeModifier.Operation.ADD_NUMBER, slot));

                                    result.setItemMeta(meta);
                                    event.setResult(result);
                                    applyUpgradeEffects(player, armorConfig, currentLevel);
                                    if (plugin.getConfig().getBoolean("upgrade_settings.show_debug")) {
                                        player.sendMessage(ChatColor.GREEN + "Upgrading " + displayName + " to level " + currentLevel);
                                    }

                                }
                            } else {
                                event.setResult(null);
                                if (!player.hasMetadata("UpgradeMessageDisplayed")) {
                                    player.sendMessage(ChatColor.RED + "You must have" + ChatColor.GOLD + " Gem of Power +"+ (currentLevel +1) + ChatColor.WHITE + " to upgrade " + ChatColor.DARK_PURPLE + displayName);
                                    player.setMetadata("UpgradeMessageDisplayed", new FixedMetadataValue(plugin, true));
                                } 
                            }
                        } else {
                            event.setResult(null);
                            player.sendMessage(ChatColor.RED + "You need " + requiredMaterialName + " to upgrade " + ChatColor.DARK_PURPLE + displayName);
                        }

                    } else {
                        event.setResult(null);
                        player.sendMessage(ChatColor.RED + "Maximum upgrade level reached for " + displayName);
                    }
                    break;

                }
            }

            FileConfiguration weaponConfig = plugin.getWeapnConfig(); // ใช้ getArmorConfig()
            for (String key : weaponConfig.getKeys(false)) {
                if (inputMaterial.name().equalsIgnoreCase(weaponConfig.getString(key + ".material"))) {
                    result = baseItem.clone();
                    ItemMeta meta = result.getItemMeta();
                    PersistentDataContainer data = meta.getPersistentDataContainer();

                    int currentLevel = data.getOrDefault(upgradeKey, PersistentDataType.INTEGER, 0);
                    int maxLevel = weaponConfig.getInt(key + ".max_upgrade_level");
                    String upgradePath = key + ".upgrade_levels.level_" + (currentLevel + 1) + ".";
                    int failUpgrade = weaponConfig.getInt(upgradePath + "fail_upgrade", 0); // ค่าเริ่มต้นคือ 0% ล้มเหลว
                    Random random = new Random();
                    String displayName = weaponConfig.getString(key + ".name");

                    double baseDamage = weaponConfig.getDouble(key + ".base_damage");
                    double baseSpeed = weaponConfig.getDouble(key + ".attack_speed");
                    double baseDurability = armorConfig.getDouble(key + ".durability");
                    meta.setDisplayName(ChatColor.DARK_PURPLE + displayName + " +" + ChatColor.AQUA + (currentLevel + 1) + "");

                    if (plugin.getConfig().getBoolean("upgrade_settings.show_debug")) {
                        player.sendMessage(ChatColor.YELLOW + "Current Level: " + currentLevel + ", Max Level: " + maxLevel);
                    }

                    if (currentLevel < maxLevel) {
                        String requiredMaterialName = weaponConfig.getString(upgradePath + "required_material");
                        Material requiredMaterial = Material.getMaterial(requiredMaterialName);
                        Integer customModelData = weaponConfig.getInt(upgradePath + "custom_model_data");
                    
                        double damageIncrease = weaponConfig.getDouble(upgradePath + "damage_increase");
                        double attackSpeedIncrease = weaponConfig.getDouble(upgradePath + "attack_speed_increase");
                        int durabilityIncrease = weaponConfig.getInt(upgradePath + "durability_increase");
                        Integer requiredCustomModelData = weaponConfig.getInt(upgradePath + "required_custom_model_data");
                    
                        Integer poisonChance = weaponConfig.getInt(upgradePath + "effect_upgrade.poison.chance");
                        Integer reflectPoisonChance = weaponConfig.getInt(upgradePath + "effect_upgrade.reflect_poison.chance");
                    
                        if (plugin.getConfig().getBoolean("upgrade_settings.show_debug")) {
                            player.sendMessage(ChatColor.YELLOW + "Required Material: " + requiredMaterial);
                        }

                        if (upgradeMaterial.getType() == requiredMaterial) {
                            ItemMeta upgradeMeta = upgradeMaterial.getItemMeta();
                            if (upgradeMeta.hasCustomModelData() && requiredCustomModelData != null && upgradeMeta.getCustomModelData() == requiredCustomModelData) {
                                if (random.nextInt(100) < failUpgrade) {
                                    player.sendMessage(ChatColor.RED + "Upgrade failed! Try again.");

                                    ItemStack materialItem = event.getInventory().getItem(2);
                                    if (materialItem != null && materialItem.getAmount() > 0) {
                                        materialItem.setAmount(materialItem.getAmount() - 1);
                                        event.getInventory().setItem(2, materialItem.getAmount() == 0 ? null : materialItem);
                                    }
                                    return; // ออกจากฟังก์ชัน
                                } else {
                                    currentLevel++;
                                    data.set(upgradeKey, PersistentDataType.INTEGER, currentLevel);
                    
                                    if (customModelData != null) {
                                        meta.setCustomModelData(customModelData);
                                        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "custom_model_data"), PersistentDataType.INTEGER, customModelData);
                                        if (plugin.getConfig().getBoolean("upgrade_settings.show_debug")) {
                                            player.sendMessage(ChatColor.GREEN + "Custom Model Data: " + customModelData + "!");
                                        }
                                        
                                    }

                                    double totalDamage = baseDamage + (damageIncrease * currentLevel); // เพิ่ม damageIncrease ตามระดับ
                                    double totalDamageProjectlies = baseDamage + (damageIncrease * currentLevel); // เพิ่ม damageIncrease ตามระดับ
                                    double totalSpeed = baseSpeed + (attackSpeedIncrease * currentLevel); // เพิ่ม attackSpeedIncrease ตามระดับ
                                    double totalDurability = baseDurability + (durabilityIncrease * currentLevel); // เพิ่ม attackSpeedIncrease ตามระดับ
                                    
                                    Collection<AttributeModifier> damageModifiers = meta.getAttributeModifiers(Attribute.GENERIC_ATTACK_DAMAGE);
                                    if (damageModifiers != null) {
                                        for (AttributeModifier modifier : damageModifiers) {
                                            meta.removeAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, modifier);
                                        }
                                    }

                                    Collection<AttributeModifier> speedModifiers = meta.getAttributeModifiers(Attribute.GENERIC_ATTACK_SPEED);
                                    if (speedModifiers != null) {
                                        for (AttributeModifier modifier : speedModifiers) {
                                            meta.removeAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, modifier);
                                        }
                                    }

                                    List<String> lore = new ArrayList<>();
                                    lore.add("");
                                    lore.add(ChatColor.GOLD + "★ " + ChatColor.RED + "Powerful weapons"); // ชื่อบ่งบอกถึงพลังของอาวุธ
                                    lore.add(ChatColor.YELLOW + "Damage: +" + totalDamage); // ความเสียหายที่เพิ่มขึ้น
                                    lore.add(ChatColor.AQUA + "Speed: +" + String.format("%.2f", totalSpeed)); // ความเร็วที่เพิ่มขึ้น
                                    lore.add(ChatColor.BLUE + "Durability: +" + totalDurability); // ความเร็วที่เพิ่มขึ้น
                                    lore.add(ChatColor.RED + "Poison Chance: " + poisonChance + "%"); // โอกาสติดพิษ
                                    lore.add(ChatColor.RED + "Reflect Poison: " + reflectPoisonChance + "%"); // โอกาสสะท้อนพิษ
                                    //lore.add(ChatColor.RED + "คำเตือน: มีโอกาสติดพิษ: " + additionalStatus + "%"); // โอกาสติดพิษจากการโจมตี
                                    lore.add(ChatColor.GRAY + "─────────────");
                                    meta.setLore(lore);

                                    meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "level"), PersistentDataType.INTEGER, currentLevel);
                    
                                    if (isWeaponProjectile(inputMaterial)) {
                                        meta.getPersistentDataContainer().has(new NamespacedKey(plugin, "is_projectile"), PersistentDataType.BOOLEAN);
                                        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "damage_projectile"), PersistentDataType.DOUBLE, totalDamageProjectlies);
                                        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "speed_projectile"), PersistentDataType.DOUBLE, totalSpeed);
                                        if (plugin.getConfig().getBoolean("upgrade_settings.show_debug")) {
                                            player.sendMessage(ChatColor.GREEN + "Projectile");
                                        }

                                    } else {
                                        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", totalDamage, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
                                        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", totalSpeed, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));

                                        if (plugin.getConfig().getBoolean("upgrade_settings.show_debug")) {
                                            player.sendMessage(ChatColor.GREEN + "Malee");
                                        }
                                    }

                                    result.setItemMeta(meta);
                                    event.setResult(result);
                                    applyUpgradeEffects(player, weaponConfig, currentLevel);
                                    if (plugin.getConfig().getBoolean("upgrade_settings.show_debug")) {
                                        player.sendMessage(ChatColor.GREEN + "Upgrading " + displayName + " to level " + currentLevel);
                                    }
                                }
                            } else {
                                event.setResult(null);
                                if (!player.hasMetadata("UpgradeMessageDisplayed")) {
                                    player.sendMessage(ChatColor.RED + "You must have" + ChatColor.GOLD + " Gem of Power +"+ (currentLevel +1) + ChatColor.WHITE + " to upgrade " + ChatColor.DARK_PURPLE + displayName);
                                    player.setMetadata("UpgradeMessageDisplayed", new FixedMetadataValue(plugin, true));
                                } 
                            }
                        } else {
                            event.setResult(null);
                            player.sendMessage(ChatColor.RED + "You need " + requiredMaterialName + " to upgrade " + displayName);
                        }

                    } else {
                        event.setResult(null);
                        player.sendMessage(ChatColor.RED + "Maximum upgrade level reached for " + displayName);
                    }
                    break;
                }
            }

            
        } else {
            event.setResult(null);
        }
    }  

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getType() == InventoryType.SMITHING) {
            if (event.getSlotType() == InventoryType.SlotType.RESULT) {
                ItemStack result = event.getInventory().getItem(3); // อาวุธในช่อง 3
                ItemMeta meta = result.getItemMeta();

                if (result != null) {
                    // ให้ผู้เล่นหยิบอาวุธออกจากช่อง 3
                    if (meta.getPersistentDataContainer().has(new NamespacedKey(plugin, "level"), PersistentDataType.INTEGER)) {
                        event.getWhoClicked().getInventory().addItem(result);
                        event.getInventory().setItem(0, null); // ลบอาวุธออกจากช่อง 3
                        event.getInventory().setItem(1, null); // ลบอาวุธออกจากช่อง 3
                        ItemStack materialItem = event.getInventory().getItem(2);
                        if (materialItem != null && materialItem.getAmount() > 0) {
                            materialItem.setAmount(materialItem.getAmount() - 1);
                            event.getInventory().setItem(2, materialItem.getAmount() == 0 ? null : materialItem);
                        }
                        
                        String playerName = event.getWhoClicked().getName();
                        String upgradeMessage = playerName + ChatColor.GOLD + " Upgraded to " + meta.getDisplayName() + "!";
                        Bukkit.broadcastMessage(upgradeMessage);
                    }
                }
            }

            // บล็อกการวางวัสดุในช่อง 2 หากมีไอเท็มอยู่ในช่อง 3
            if (event.getSlot() == 2 && event.getInventory().getItem(3) != null) {
                event.setCancelled(true); // บล็อกการคลิกในช่อง 2
            }

            if (event.getSlot() == 2) {
                if (event.getViewers().size() > 0 && event.getViewers().get(0) instanceof Player) {
                    Player player = (Player) event.getViewers().get(0);
                    player.removeMetadata("UpgradeMessageDisplayed", plugin);
                }
            }
        }
    }

    private void applyUpgradeEffects(Player player, ConfigurationSection upgradeConfig, int currentLevel) {
        for (String key : upgradeConfig.getKeys(false)) {
            String upgradePath = key + ".upgrade_levels.level_" + (currentLevel + 1) + ".effect_upgrade";
        
            ConfigurationSection effects = upgradeConfig.getConfigurationSection(upgradePath);
        
            if (effects != null) {
                // Handle poison effect
                if (effects.isConfigurationSection("poison")) {
                    ConfigurationSection poisonEffect = effects.getConfigurationSection("poison");
                    int level = poisonEffect.getInt("level", 0); // ดึงค่าจาก poisonEffect
                    int chance = poisonEffect.getInt("chance", 0); // ดึงค่าจาก poisonEffect
    
                    player.setMetadata("poison_chance_level", new FixedMetadataValue(plugin, level));
                    player.setMetadata("poison_chance", new FixedMetadataValue(plugin, chance));
                }
        
                // Handle reflect_poison effect
                if (effects.isConfigurationSection("reflect_poison")) {
                    ConfigurationSection reflectEffect = effects.getConfigurationSection("reflect_poison");
                    int reflectChance = reflectEffect.getInt("chance", 0); // ดึงค่าจาก reflectEffect
                    player.setMetadata("reflect_poison_chance", new FixedMetadataValue(plugin, reflectChance));
                }
            }
        }
    }    


    private boolean isWeapon(Material material) {
        return material == Material.WOODEN_SWORD || material == Material.STONE_SWORD || material == Material.IRON_SWORD ||
            material == Material.DIAMOND_SWORD || material == Material.NETHERITE_SWORD || material == Material.WOODEN_AXE ||
            material == Material.STONE_AXE || material == Material.IRON_AXE || material == Material.DIAMOND_AXE ||
            material == Material.NETHERITE_AXE || material == Material.BOW || material == Material.CROSSBOW || 
            material == Material.TRIDENT;
    }

    private boolean isWeaponProjectile(Material material) {
        return material == Material.BOW || material == Material.CROSSBOW || 
        material == Material.TRIDENT;
    }

    private boolean isArmor(Material material) {
        return material == Material.LEATHER_HELMET || material == Material.LEATHER_CHESTPLATE || material == Material.LEATHER_LEGGINGS || material == Material.LEATHER_BOOTS ||
            material == Material.IRON_HELMET || material == Material.IRON_CHESTPLATE || material == Material.IRON_LEGGINGS || material == Material.IRON_BOOTS ||
            material == Material.DIAMOND_HELMET || material == Material.DIAMOND_CHESTPLATE || material == Material.DIAMOND_LEGGINGS || material == Material.DIAMOND_BOOTS ||
            material == Material.NETHERITE_HELMET || material == Material.NETHERITE_CHESTPLATE || material == Material.NETHERITE_LEGGINGS || material == Material.NETHERITE_BOOTS ||
            material == Material.CHAINMAIL_HELMET || material == Material.CHAINMAIL_CHESTPLATE || material == Material.CHAINMAIL_LEGGINGS || material == Material.CHAINMAIL_BOOTS ||
            material == Material.GOLDEN_HELMET || material == Material.GOLDEN_CHESTPLATE || material == Material.GOLDEN_LEGGINGS || material == Material.GOLDEN_BOOTS ||
            material == Material.TURTLE_HELMET || material == Material.SHIELD;
    }
    
}
