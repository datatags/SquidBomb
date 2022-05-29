package me.datatags.squidbomb;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Squid;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.cryptomorin.xseries.XMaterial;

import java.util.HashSet;
import java.util.Set;

public class InkRunnable extends BukkitRunnable {
    private static final Set<PotionEffect> EFFECTS = new HashSet<>();
    private static final FireworkMeta FIREWORK_META = (FireworkMeta) XMaterial.FIREWORK_ROCKET.parseItem().getItemMeta();
    static {
        FireworkEffect effect = FireworkEffect.builder().with(Type.BALL_LARGE).withColor(Color.GRAY, Color.BLACK).build();
        FIREWORK_META.addEffect(effect);
        FIREWORK_META.setPower(1);
        EFFECTS.add(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0));
        EFFECTS.add(new PotionEffect(PotionEffectType.SLOW, 60, 0));
    }

    private final Item item;
    private final SquidBomb plugin;
    public InkRunnable(Item item, SquidBomb plugin) {
        this.item = item;
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (!item.isValid()) return;
        Firework firework = (Firework) item.getWorld().spawnEntity(item.getLocation(), EntityType.FIREWORK);
        firework.setFireworkMeta(FIREWORK_META);
        firework.setMetadata("effect", new FixedMetadataValue(plugin, 1));
        Bukkit.getScheduler().runTaskLater(plugin, () -> firework.detonate(), 1);
        for (Entity entity : item.getNearbyEntities(3, 3, 3)) {
            if (entity instanceof Player) {
                ((Player)entity).addPotionEffects(EFFECTS);
            }
        }

        Squid squid = (Squid) item.getWorld().spawnEntity(item.getLocation(), EntityType.SQUID);
        squid.setVelocity(new Vector(0, 3, 0));
        squid.setMetadata("squid", new FixedMetadataValue(plugin, 1));
        Bukkit.getScheduler().runTaskLater(plugin, () -> squid.remove(), 20);
        item.remove();
    }
}
