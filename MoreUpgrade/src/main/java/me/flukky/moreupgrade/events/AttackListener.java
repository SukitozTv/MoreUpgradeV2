package me.flukky.moreupgrade.events;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.flukky.moreupgrade.MoreUpgrade;

public class AttackListener implements Listener {
    private MoreUpgrade plugin;

    public AttackListener(MoreUpgrade plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();

        if (damager instanceof Player && entity instanceof LivingEntity) {
            Player player = (Player) damager;
            LivingEntity target = (LivingEntity) entity;
            ItemStack weapon = player.getInventory().getItemInMainHand();

            if (weapon != null && weapon.hasItemMeta()) {
                ItemMeta meta = weapon.getItemMeta();

                if (meta.getPersistentDataContainer().has(new NamespacedKey(plugin, "level"), PersistentDataType.INTEGER)) {
                    int level = player.getMetadata("poison_chance_level").get(0).asInt();
                    int chance = player.getMetadata("poison_chance").get(0).asInt();

                    if (player.hasMetadata("poison_chance")) {
                        if (new Random().nextInt(100) < chance) {
                            target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, level)); 
                            if (plugin.getConfig().getBoolean("upgrade_settings.show_debug")) {
                                player.sendMessage(ChatColor.RED + "You poisoned your target!");
                            }
                        }
                    }
                }
            }
        }

        if (entity instanceof Player && damager instanceof LivingEntity) {
            Player player = (Player) entity;
            LivingEntity attacker = (LivingEntity) damager;

            for (ItemStack armor : player.getInventory().getArmorContents()) {
                if (armor != null && armor.hasItemMeta()) {
                    ItemMeta armorMeta = armor.getItemMeta();

                    if (armorMeta != null && armorMeta.getPersistentDataContainer().has(new NamespacedKey(plugin, "level"), PersistentDataType.INTEGER)) {
                        int level = armorMeta.getPersistentDataContainer().get(new NamespacedKey(plugin, "level"), PersistentDataType.INTEGER);
                        int reflectChance = player.getMetadata("reflect_poison_chance").get(0).asInt();

                        // โอกาสสะท้อนพิษ
                        if (player.hasMetadata("reflect_poison_chance")) {
                            if (new Random().nextInt(100) < reflectChance) {
                                attacker.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 1));
                                if (plugin.getConfig().getBoolean("upgrade_settings.show_debug")) {
                                    player.sendMessage(ChatColor.GREEN + "You reflected poison back!");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onProjectileHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager(); // แปลงประเภท
            if (projectile.getShooter() instanceof Player) {
                Player player = (Player) projectile.getShooter(); // แปลงประเภท
                ItemStack weapon = player.getInventory().getItemInMainHand();

                // ตรวจสอบว่าอาวุธมี metadata ที่ต้องการหรือไม่
                if (weapon.hasItemMeta()) {
                    ItemMeta meta = weapon.getItemMeta();
                    if (meta != null && meta.getPersistentDataContainer().has(new NamespacedKey(plugin, "level"), PersistentDataType.INTEGER)) {
                        double damage = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "damage_projectile"), PersistentDataType.DOUBLE);
                        double speed = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "speed_projectile"), PersistentDataType.DOUBLE);
                        double originalDamage = event.getDamage();
                        double newDamage = originalDamage + damage; // ปรับดาเมจเป็น 150%
                        event.setDamage(newDamage);
                        
                        // แสดงข้อมูล debug ในคอนโซล
                        if (plugin.getConfig().getBoolean("upgrade_settings.show_debug")) {
                            player.sendMessage(ChatColor.GREEN + "Projectile hit! Shooter: " + player.getName() + ", Increase Damage: " + damage + ", Original Damage: " + originalDamage + ", New Damage: " + newDamage + " Speed: " + speed);
                        }
                    } else {
                        // ถ้าไม่มี metadata จะแสดงข้อความ debug
                        if (plugin.getConfig().getBoolean("upgrade_settings.show_debug")) {
                            player.sendMessage(ChatColor.GREEN + "Weapon does not have the required damage metadata.");
                        }
                    }
                } else {
                    // ถ้าอาวุธไม่มี metadata จะแสดงข้อความ debug
                    if (plugin.getConfig().getBoolean("upgrade_settings.show_debug")) {
                        player.sendMessage(ChatColor.GREEN + "The player's weapon does not have item meta.");
                    }
                }
            }
        }
    }
}
