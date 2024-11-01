package me.flukky.moreupgrade.managers;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import me.flukky.moreupgrade.MoreUpgrade;

public class registerCombine {
    private MoreUpgrade plugin;

    public registerCombine(MoreUpgrade plugin) {
        this.plugin = plugin;
    }

    public void registerCombines() {
        FileConfiguration config = plugin.getConfig();
        // โหลด combineItems จาก config.yml
        if (config.contains("combineItems")) {
            for (String key : config.getConfigurationSection("combineItems").getKeys(false)) {
                String inputName = config.getString("combineItems." + key + ".input.name");
                String inputItemName = config.getString("combineItems." + key + ".input.item");
                int inputCustomModelData = config.getInt("combineItems." + key + ".input.custom_model_data");
                
                String outputName = config.getString("combineItems." + key + ".output.name");
                String outputItemName = config.getString("combineItems." + key + ".output.item");
                int outputCustomModelData = config.getInt("combineItems." + key + ".output.custom_model_data");
                
                List<String> shape = config.getStringList("combineItems." + key + ".shape");

                // สร้าง ItemStack สำหรับ input และ output
                ItemStack inputItem = createCustomItem(inputName, Material.matchMaterial(inputItemName), inputCustomModelData);
                ItemStack outputItem = createCustomItem(outputName, Material.matchMaterial(outputItemName), outputCustomModelData);

                // สร้าง ShapedRecipe
                ShapedRecipe recipe = new ShapedRecipe(NamespacedKey.fromString(key, plugin), outputItem);
                recipe.shape(shape.toArray(new String[0])); // ใช้รูปแบบจาก config
                recipe.setIngredient('G', inputItem.getType()); // ใช้ getType() เพื่อเอา Material

                // เพิ่มสูตรการรวมไอเท็ม
                plugin.getServer().addRecipe(recipe);
            }
        }
    }

    private ItemStack createCustomItem(String name, Material material, int customModelData) {
        if (material == null) {
            plugin.getLogger().warning("Invalid material name: " + name);
            return new ItemStack(Material.AIR);
        }
        
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setCustomModelData(customModelData);
            item.setItemMeta(meta);
        }
        return item;
    }
}
