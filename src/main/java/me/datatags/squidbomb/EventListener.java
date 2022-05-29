package me.datatags.squidbomb;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import com.cryptomorin.xseries.XMaterial;

public class EventListener implements Listener {
    private static final boolean OFFHAND = XMaterial.SHIELD.parseMaterial() != null;
    private final SquidBomb plugin;
    public EventListener(SquidBomb plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getItem() == null || !XMaterial.INK_SAC.isSimilar(e.getItem())) return;
        e.setCancelled(true);
        ItemStack newItem = e.getItem();
        newItem.setAmount(newItem.getAmount() - 1);
        setItemInHand(e, newItem);

        Player player = e.getPlayer();
        Item item = player.getWorld().dropItem(player.getEyeLocation(), XMaterial.INK_SAC.parseItem());
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

    @SuppressWarnings("deprecation")
    private void setItemInHand(PlayerInteractEvent e, ItemStack item) {
        if (OFFHAND) {
            if (e.getHand() == EquipmentSlot.HAND) {
                e.getPlayer().getInventory().setItemInMainHand(item);
            } else {
                e.getPlayer().getInventory().setItemInOffHand(item);
            }
        } else {
            e.getPlayer().setItemInHand(item);
        }
    }
}
