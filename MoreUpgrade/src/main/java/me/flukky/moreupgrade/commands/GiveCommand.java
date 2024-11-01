package me.flukky.moreupgrade.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.flukky.moreupgrade.MoreUpgrade;

import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GiveCommand implements CommandExecutor, TabCompleter {
    private MoreUpgrade plugin;

    public GiveCommand(MoreUpgrade plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) || sender.isOp()) {
            // คำสั่ง reload
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                plugin.reloadConfig();
                plugin.reloadRecipeConfig();
                sender.sendMessage(ChatColor.GREEN + "MoreUpgrade configuration reloaded!");
                return true;
            }

            // คำสั่ง give (ให้ไอเทม)
            if (args.length == 5 && args[0].equalsIgnoreCase("give")) {
                String itemType = args[1];
                String itemName = args[2];
                int amount;
                Player targetPlayer = Bukkit.getPlayer(args[4]);  // ใช้ args[4] สำหรับชื่อผู้เล่น

                // แปลงค่าจำนวนจาก args[3]
                try {
                    amount = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Amount must be a number.");
                    return true;
                }

                // ตรวจสอบว่าผู้เล่นเป้าหมายออนไลน์หรือไม่
                if (targetPlayer != null) {
                    // ตรวจสอบประเภทไอเทมที่ผู้ใช้ต้องการให้
                    if (itemType.equalsIgnoreCase("weapon")) {
                        handleWeaponGive(sender, itemName, amount, targetPlayer);
                    } else if (itemType.equalsIgnoreCase("armor")) {
                        handleArmorGive(sender, itemName, amount, targetPlayer);
                    } else {
                        sender.sendMessage(ChatColor.RED + "Invalid item type. Use 'weapon' or 'armor'.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Player not found.");
                }
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
        }
        return true;
    }

    private void handleWeaponGive(CommandSender sender, String weaponName, int amount, Player targetPlayer) {
        ConfigurationSection weaponSection = plugin.getWeapnConfig().getConfigurationSection(weaponName);
    
        if (weaponSection != null) {
            List<String> requiredMaterials = new ArrayList<>();
            List<Integer> requiredCustomModelDataList = new ArrayList<>();
            List<String> upgradeLevelsList = new ArrayList<>(); // เปลี่ยนเป็น List<String>
    
            // ดึงข้อมูลวัสดุและ custom_model_data จากแต่ละเลเวล
            ConfigurationSection upgradeLevels = weaponSection.getConfigurationSection("upgrade_levels");
            if (upgradeLevels != null) {
                for (String level : upgradeLevels.getKeys(false)) {
                    ConfigurationSection levelSection = upgradeLevels.getConfigurationSection(level);
                    if (levelSection != null) {
                        String requiredMaterial = levelSection.getString("required_material");
                        int requiredCustomModelDataValue = levelSection.getInt("required_custom_model_data");
    
                        if (requiredMaterial != null) {
                            requiredMaterials.add(requiredMaterial);
                            requiredCustomModelDataList.add(requiredCustomModelDataValue);
                            upgradeLevelsList.add(level); // เก็บ level ที่ใช้ใน List<String>
                        }
                    }
                }
            }
    
            // ให้วัสดุที่จำเป็นแก่ผู้เล่นโดยกำหนดค่า CustomModelData ตามแต่ละคู่
            for (int i = 0; i < requiredMaterials.size(); i++) {
                Material material = Material.getMaterial(requiredMaterials.get(i));
                int customModelDataValue = requiredCustomModelDataList.get(i);
                String level = upgradeLevelsList.get(i); // ใช้ String แทน
                String levelNumber = level.split("_")[1]; // หรือ level.substring(6)
    
                if (material != null) {
                    ItemStack item = new ItemStack(material, amount);
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        // ตั้งชื่อไอเทม
                        setLoreItem(meta, item);
                        meta.setDisplayName(ChatColor.DARK_PURPLE + "✦ " + ChatColor.GOLD + "Gem of Power" + " +" + levelNumber + ChatColor.DARK_PURPLE + " ✦");
                        meta.setCustomModelData(customModelDataValue); // กำหนดค่า CustomModelData ตามค่าแต่ละคู่
                        item.setItemMeta(meta);
                    }
                    targetPlayer.getInventory().addItem(item);
                }
            }
    
            sender.sendMessage(ChatColor.GREEN + "Gave " + amount + " " + weaponName + " and required materials to " + targetPlayer.getName());
            targetPlayer.sendMessage(ChatColor.GREEN + "You received " + amount + " " + weaponName + " and required materials!");
        } else {
            sender.sendMessage(ChatColor.RED + "Weapon not found in config.");
        }
    }    

    private void handleArmorGive(CommandSender sender, String armorName, int amount, Player targetPlayer) {
        ConfigurationSection armorSection = plugin.getArmorConfig().getConfigurationSection(armorName);
    
        if (armorSection != null) {
            List<String> requiredMaterials = new ArrayList<>();
            List<Integer> requiredCustomModelDataList = new ArrayList<>();
            List<String> upgradeLevelsList = new ArrayList<>(); // เปลี่ยนเป็น List<String>
    
            // ดึงข้อมูลวัสดุและ custom_model_data จากแต่ละเลเวล
            ConfigurationSection upgradeLevels = armorSection.getConfigurationSection("upgrade_levels");
            if (upgradeLevels != null) {
                for (String level : upgradeLevels.getKeys(false)) {
                    ConfigurationSection levelSection = upgradeLevels.getConfigurationSection(level);
                    if (levelSection != null) {
                        String requiredMaterial = levelSection.getString("required_material");
                        int requiredCustomModelDataValue = levelSection.getInt("required_custom_model_data");
    
                        if (requiredMaterial != null) {
                            requiredMaterials.add(requiredMaterial);
                            requiredCustomModelDataList.add(requiredCustomModelDataValue);
                            upgradeLevelsList.add(level); // เก็บ level ที่ใช้ใน List<String>
                        }
                    }
                }
            }
    
            // ให้วัสดุที่จำเป็นแก่ผู้เล่นโดยกำหนดค่า CustomModelData ตามแต่ละคู่
            for (int i = 0; i < requiredMaterials.size(); i++) {
                Material material = Material.getMaterial(requiredMaterials.get(i));
                int customModelDataValue = requiredCustomModelDataList.get(i);
                String level = upgradeLevelsList.get(i); // ใช้ String แทน
                String levelNumber = level.split("_")[1]; // หรือ level.substring(6)
    
                if (material != null) {
                    ItemStack item = new ItemStack(material, amount);
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        // ตั้งชื่อไอเทม
                        setLoreItem(meta, item);
                        meta.setDisplayName(ChatColor.DARK_PURPLE + "✦ " + ChatColor.GOLD + "Gem of Power" + " +" + levelNumber + ChatColor.DARK_PURPLE + " ✦");
                        meta.setCustomModelData(customModelDataValue); // กำหนดค่า CustomModelData ตามค่าแต่ละคู่
                        item.setItemMeta(meta);
                    }
                    targetPlayer.getInventory().addItem(item);
                }
            }
    
            sender.sendMessage(ChatColor.GREEN + "Gave " + amount + " " + armorName + " and required materials to " + targetPlayer.getName());
            targetPlayer.sendMessage(ChatColor.GREEN + "You received " + amount + " " + armorName + " and required materials!");
        } else {
            sender.sendMessage(ChatColor.RED + "Armor not found in config.");
        }
    }      

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) || !sender.isOp()) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            return Arrays.asList("give", "reload"); // แนะนำ "give" และ "reload"
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            return Arrays.asList("weapon", "armor"); // แนะนำ "weapon" และ "armor"
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            if (args[1].equalsIgnoreCase("armor")) {
                // คืนชื่อไฟล์ .yml ที่อยู่ในโฟลเดอร์ armors เท่านั้น
                return getArmorNames();
            } else if (args[1].equalsIgnoreCase("weapon")) {
                // คืนชื่อไฟล์ .yml ที่อยู่ในโฟลเดอร์ weapons เท่านั้น
                return getWeaponNames();
            }
        } else if (args.length == 4 && args[0].equalsIgnoreCase("give")) {
            return Collections.singletonList("1"); // แนะนำจำนวน "1" เป็นค่าเริ่มต้น
        } else if (args.length == 5 && args[0].equalsIgnoreCase("give")) {
            // คืนชื่อผู้เล่นที่ออนไลน์
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList(); // ถ้าไม่มีการเติมอัตโนมัติ
    }

    // ฟังก์ชันเพื่อจัดการการตั้งค่า lore
    private ItemMeta setLoreItem(ItemMeta meta, ItemStack item) {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "─────────────"); // Decorative divider
        lore.add(ChatColor.DARK_AQUA + "✦ Bestow power upon your weapons and armor ✦");
        lore.add(ChatColor.DARK_AQUA + "The chance to upgrade shall rise,");
        lore.add(ChatColor.RED + "but beware! Failure may lurk in the shadows...");
        lore.add("");
        lore.add(ChatColor.LIGHT_PURPLE + "Hidden powers will be unleashed");
        lore.add(ChatColor.LIGHT_PURPLE + "when this gem is used for enhancement.");
        lore.add(ChatColor.LIGHT_PURPLE + "Forge an equipment that stands the test of time!");
        lore.add(ChatColor.GRAY + "─────────────");
        meta.setLore(lore);
        return meta;
    }

    private List<String> getWeaponNames() {
        List<String> weaponNames = new ArrayList<>();
        File weaponsFolder = new File(plugin.getDataFolder(), "weapons");

        if (weaponsFolder.exists() && weaponsFolder.isDirectory()) {
            File[] files = weaponsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
            if (files != null) {
                for (File file : files) {
                    weaponNames.add(file.getName().replace(".yml", ""));
                }
            }
        }
        return weaponNames;
    }

    private List<String> getArmorNames() {
        List<String> armorNames = new ArrayList<>();
        File armorsFolder = new File(plugin.getDataFolder(), "armors");

        if (armorsFolder.exists() && armorsFolder.isDirectory()) {
            File[] files = armorsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
            if (files != null) {
                for (File file : files) {
                    armorNames.add(file.getName().replace(".yml", ""));
                }
            }
        }
        return armorNames;
    }
}
