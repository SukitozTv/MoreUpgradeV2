package me.flukky.moreupgrade;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import me.flukky.moreupgrade.commands.GiveCommand;
import me.flukky.moreupgrade.events.AttackListener;
import me.flukky.moreupgrade.events.SmithingListener;
import me.flukky.moreupgrade.managers.registerRecipes;
import net.md_5.bungee.api.ChatColor;

public class MoreUpgrade extends JavaPlugin {

    private registerRecipes registerRecipes;

    private FileConfiguration armorConfig;
    private FileConfiguration weaponConfig;
    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
    
    @Override
    public void onEnable() {
        saveDefaultConfig();

        registerRecipes = new registerRecipes(this);
        registerRecipes.registerSmithingRecipes();

        armorConfig = loadArmorConfig();  // Initialize armorConfig
        weaponConfig = loadWeaponConfig();  // Initialize weaponConfig

        getServer().getPluginManager().registerEvents(new SmithingListener(this), this);
        getServer().getPluginManager().registerEvents(new AttackListener(this), this);

        // ลงทะเบียนคำสั่ง
        this.getCommand("moreupgrade").setExecutor(new GiveCommand(this));
    }

    public void reloadRecipeConfig() {
        // อัปเดตค่าคอนฟิก
        armorConfig = loadArmorConfig();  // Initialize armorConfig
        weaponConfig = loadWeaponConfig();  // Initialize weaponConfig
    }

    public FileConfiguration getArmorConfig() {
        return armorConfig;
    }

    public FileConfiguration getWeapnConfig() {
        return weaponConfig;
    }
    
    private FileConfiguration loadArmorConfig() {
        File armorFolder = new File(getDataFolder(), "armors");
        File[] armorFiles = armorFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        FileConfiguration config = new YamlConfiguration();

        if (armorFiles != null) {
            for (File file : armorFiles) {
                try {
                    FileConfiguration armorConfig = YamlConfiguration.loadConfiguration(file);
                    // รวมข้อมูลจาก armorConfig เข้าไปใน config
                    for (String key : armorConfig.getKeys(false)) {
                        config.set(key, armorConfig.getConfigurationSection(key));
                        console.sendMessage(ChatColor.GREEN + "Loading armor files : " + file.getName());
                    }
                } catch (Exception e) {
                    console.sendMessage(ChatColor.RED + "Error loading armor config from " + file.getName() + ": " + e.getMessage());
                }
            }
        }
        return config;
    }

    private FileConfiguration loadWeaponConfig() {
        File weaponFolder = new File(getDataFolder(), "weapons");
        File[] weaponFiles = weaponFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        FileConfiguration config = new YamlConfiguration();

        if (weaponFiles != null) {
            for (File file : weaponFiles) {
                try {
                    FileConfiguration weaponConfig = YamlConfiguration.loadConfiguration(file);
                    // รวมข้อมูลจาก weaponConfig เข้าไปใน config
                    for (String key : weaponConfig.getKeys(false)) {
                        config.set(key, weaponConfig.getConfigurationSection(key));
                        console.sendMessage(ChatColor.GREEN + "Loading weapon files : " + file.getName());
                    }
                } catch (Exception e) {
                    console.sendMessage(ChatColor.RED + "Error loading weapon config from " + file.getName() + ": " + e.getMessage());
                }
            }
        }
        return config;
    }
    
}
