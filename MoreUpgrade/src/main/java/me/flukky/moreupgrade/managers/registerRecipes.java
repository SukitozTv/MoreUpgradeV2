package me.flukky.moreupgrade.managers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingTransformRecipe;

import me.flukky.moreupgrade.MoreUpgrade;

public class registerRecipes {
    private MoreUpgrade plugin;
    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

    public registerRecipes(MoreUpgrade plugin) {
        this.plugin = plugin;
    }

    public void registerSmithingRecipes() {
        registerArmorRecipes();
        registerWeaponRecipes();
    }

    private void registerArmorRecipes() {
        File armorFolder = new File(plugin.getDataFolder(), "armors");
        if (!armorFolder.exists()) {
            armorFolder.mkdirs();
        }
    
        File[] armorFiles = armorFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (armorFiles == null || armorFiles.length == 0) {
            console.sendMessage(ChatColor.RED + "ไม่พบไฟล์ YAML ในโฟลเดอร์ armors");
            return;
        }
    
        for (File file : armorFiles) {
            console.sendMessage(ChatColor.GREEN + "Loading armor files: " + file.getName());
            FileConfiguration localArmorConfig = YamlConfiguration.loadConfiguration(file);
    
            // ดึงข้อมูลทั้งหมดในไฟล์
            for (String key : localArmorConfig.getKeys(false)) {
                Object armorData = localArmorConfig.get(key);
                if (armorData != null) {
                    String path = key + ".";
                    Material inputMaterial = Material.getMaterial(localArmorConfig.getString(path + "material"));
    
                    if (inputMaterial == null) {
                        console.sendMessage(ChatColor.RED + "Error: Invalid material for armor " + key);
                        continue;
                    }
    
                    // วนรอบระดับการอัปเกรด
                    for (int level = 1; level <= 10; level++) {
                        String levelPath = path + "upgrade_levels.level_" + level + ".";
                        Material requiredMaterial = Material.getMaterial(localArmorConfig.getString(levelPath + "required_material"));
    
                        if (requiredMaterial == null) {
                            console.sendMessage(ChatColor.RED + "Error: Invalid material for armor " + key + " at level " + level);
                            continue;
                        }
    
                        ItemStack resultItem = new ItemStack(inputMaterial);
                        RecipeChoice weaponChoice = new RecipeChoice.MaterialChoice(inputMaterial);
                        RecipeChoice materialChoice = new RecipeChoice.MaterialChoice(requiredMaterial);
                        RecipeChoice templateChoice = new RecipeChoice.MaterialChoice(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
    
                        NamespacedKey armorKey = new NamespacedKey(plugin, "custom_smithing_armor_" + key + "_level_" + level);
                        SmithingTransformRecipe recipe = new SmithingTransformRecipe(armorKey, resultItem, templateChoice, weaponChoice, materialChoice);
                        Bukkit.addRecipe(recipe);
    
                        console.sendMessage(ChatColor.GREEN + "Loaded armor recipe for level " + level + ": " + key);
                    }
                } else {
                    console.sendMessage(ChatColor.RED + "ข้อมูลชุดเกราะไม่ถูกต้องในไฟล์: " + file.getName() + " สำหรับกุญแจ: " + key);
                }
            }
        }
    }
    
    private void registerWeaponRecipes() {
        File weaponFolder = new File(plugin.getDataFolder(), "weapons");
        if (!weaponFolder.exists()) {
            weaponFolder.mkdirs();
        }
    
        File[] weaponFiles = weaponFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (weaponFiles == null || weaponFiles.length == 0) {
            console.sendMessage(ChatColor.RED + "ไม่พบไฟล์ YAML ในโฟลเดอร์ weapons");
            return;
        }
    
        for (File file : weaponFiles) {
            console.sendMessage(ChatColor.GREEN + "Loading weapon files: " + file.getName());
            FileConfiguration localWeaponConfig = YamlConfiguration.loadConfiguration(file);
    
            // ดึงข้อมูลทั้งหมดในไฟล์
            for (String key : localWeaponConfig.getKeys(false)) {
                Object weaponData = localWeaponConfig.get(key);
                if (weaponData != null) {
                    String path = key + ".";
                    Material inputMaterial = Material.getMaterial(localWeaponConfig.getString(path + "material"));
    
                    if (inputMaterial == null) {
                        console.sendMessage(ChatColor.RED + "Error: Invalid material for weapon " + key);
                        continue;
                    }
    
                    // วนรอบระดับการอัปเกรด
                    for (int level = 1; level <= 10; level++) {
                        String levelPath = path + "upgrade_levels.level_" + level + ".";
                        Material requiredMaterial = Material.getMaterial(localWeaponConfig.getString(levelPath + "required_material"));
    
                        if (requiredMaterial == null) {
                            console.sendMessage(ChatColor.RED + "Error: Invalid material for weapon " + key + " at level " + level);
                            continue;
                        }
    
                        ItemStack resultItem = new ItemStack(inputMaterial);
                        RecipeChoice weaponChoice = new RecipeChoice.MaterialChoice(inputMaterial);
                        RecipeChoice materialChoice = new RecipeChoice.MaterialChoice(requiredMaterial);
                        RecipeChoice templateChoice = new RecipeChoice.MaterialChoice(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
    
                        NamespacedKey weaponKey = new NamespacedKey(plugin, "custom_smithing_weapon_" + key + "_level_" + level);
                        SmithingTransformRecipe recipe = new SmithingTransformRecipe(weaponKey, resultItem, templateChoice, weaponChoice, materialChoice);
                        Bukkit.addRecipe(recipe);
    
                        console.sendMessage(ChatColor.GREEN + "Loaded weapon recipe for level " + level + ": " + key);
                    }
                } else {
                    console.sendMessage(ChatColor.RED + "ข้อมูลอาวุธไม่ถูกต้องในไฟล์: " + file.getName() + " สำหรับกุญแจ: " + key);
                }
            }
        }
    }    

    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }

    private void saveConfig() {
        try {
            plugin.saveConfig();
        } catch (Exception e) {
            plugin.getLogger().severe("เกิดข้อผิดพลาดในการบันทึกการกำหนดค่า: " + e.getMessage());
        }
    }
    

}
