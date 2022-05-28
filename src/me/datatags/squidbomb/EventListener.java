package me.datatags.squidbomb;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class EventListener implements Listener {
    private final SquidBomb plugin;
    public EventListener(SquidBomb plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getItem() == null || e.getItem().getType() != Material.INK_SACK) return;
        e.setCancelled(true);
        ItemStack newItem = e.getItem();
        newItem.setAmount(newItem.getAmount() - 1);
        Player player = e.getPlayer();
        player.setItemInHand(newItem);
        Item item = player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.INK_SACK));
        item.setVelocity(player.getLocation().getDirection().multiply(0.5));
        item.setPickupDelay(Integer.MAX_VALUE);
        item.setMetadata("squid", new FixedMetadataValue(plugin, 1));
        new InkRunnable(item, plugin).runTaskLater(plugin, 40);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager().hasMetadata("effect")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onMerge(ItemMergeEvent e) {
        if (e.getEntity().hasMetadata("squid") || e.getTarget().hasMetadata("squid")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDie(EntityDeathEvent e) {
        if (e.getEntity().hasMetadata("squid")) {
            e.setDroppedExp(0);
            e.getDrops().clear();
        }
    }
}
