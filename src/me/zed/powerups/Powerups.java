package me.zed.powerups;

import com.connorlinfoot.bountifulapi.BountifulAPI;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.handler.PickupHandler;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

import static java.lang.Math.*;

public class Powerups extends JavaPlugin implements Listener {

    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
    }

    Location poweruploc;
    Location blockloc;

    public void startTimer(Player p) {
        poweruploc = p.getLocation().getBlock().getLocation().add(0.5, 1, 0.5);
        blockloc = p.getLocation();
        new BukkitRunnable() {
            int i = 3;
            public void run() {
                i--;
                double x = cos(random()* 0.5);
                double y = p.getLocation().getY() * 0.5;
                double z = sin(random() * 0.5);

                blockloc.add(x,y,z);
                blockloc.subtract(x,y,z);
                p.playEffect(blockloc, Effect.MOBSPAWNER_FLAMES, 10);

                p.sendMessage("" + i);
                if (i <= 0) {
                    this.cancel();

                    Random random = new Random();
                    for (int n = 1; n <= 1; ++n) {
                        int randomInt = random.nextInt(5);
                        if(randomInt == 0){
                            DMG(Material.REDSTONE, p);
                            p.sendMessage(ChatColor.RED + "Red Team" + ChatColor.GRAY + " has captured a point and gained a global Damage Buff!");
                        }
                        if(randomInt == 1){
                            Regen(Material.NETHER_STAR, p);
                            p.sendMessage(ChatColor.RED + "Red Team" + ChatColor.GRAY + " has captured a point and gained a global Regeneration Buff!");
                        }
                        if(randomInt == 2){
                            blind(Material.INK_SACK, p);
                            p.sendMessage(ChatColor.BLUE + "BLU Team" + ChatColor.GRAY + " has captured a point and gained the Blinding Light Buff!");
                        }
                        if(randomInt == 3){
                            spawnMist(Material.OBSIDIAN, p);
                            p.sendMessage(ChatColor.RED + "Red Team" + ChatColor.GRAY + " has captured a point and granted The Mist Buff!");
                        }
                        if(randomInt == 4){
                            Lightening(Material.IRON_SWORD, p);
                            p.sendMessage(ChatColor.RED + "Red Team" + ChatColor.GRAY + " has captured a point and granted Thunder Storm Buff!");
                        }
                    }
                    blockloc.add(0,-1,0).getBlock().setType(Material.WOOL);
                }
            }
        }.runTaskTimer(this, 0, 20);

    }
    @EventHandler
    public void interact(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(e.getAction() == Action.LEFT_CLICK_AIR) {
            if(p.getItemInHand() != null && p.getItemInHand().getType() == Material.DIAMOND_SWORD){
                startTimer(p);
            }
        }
    }

    public void DMG(Material material, Player player){

        String text = ChatColor.RED  + "" + ChatColor.BOLD + "Damage Buff";
        ItemStack icon = new ItemStack(material);

        final Hologram hologram = HologramsAPI.createHologram(this, poweruploc);

        hologram.appendTextLine(text);
        ItemLine itemLine = hologram.appendItemLine(icon);

        itemLine.setPickupHandler(new PickupHandler() {

            @Override
            public void onPickup(Player player) {

                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1F, 2F);

                player.playEffect(hologram.getLocation(), Effect.MOBSPAWNER_FLAMES, null);

                player.sendMessage(ChatColor.GOLD.toString() + ChatColor.ITALIC + "You have been granted +10% increased damage on both melee and ability attacks for"+ ChatColor.AQUA + " 2 minutes "
                        + ChatColor.GOLD.toString() + ChatColor.ITALIC + "!");

                actionBar(player, ChatColor.RED + "Damage Buff: " , 20, 0, 120);

                hologram.delete();

            }
        });
    }

    public void Regen(Material material, Player p){
        String text = ChatColor.GREEN.toString() + ChatColor.BOLD + "Regeneration Buff";
        ItemStack icon = new ItemStack(material);
        final Hologram hologram = HologramsAPI.createHologram(this, poweruploc);

        hologram.appendTextLine(text);
        ItemLine itemLine = hologram.appendItemLine(icon);

        itemLine.setPickupHandler(new PickupHandler() {
            @Override
            public void onPickup(Player player) {
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1F, 2F);

                player.playEffect(hologram.getLocation(), Effect.MOBSPAWNER_FLAMES, null);

                for(Player p : Bukkit.getOnlinePlayers()){
                    int health = toIntExact((long)0.95 * (long)p.getMaxHealth());
                    p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 9999, health));
                }

                player.sendMessage(ChatColor.GOLD.toString() + ChatColor.ITALIC + "You have been granted +5% increased health regeneration for"+ ChatColor.AQUA + " 2 minutes "
                        + ChatColor.GOLD.toString() + ChatColor.ITALIC + "!");

                actionBar(p, ChatColor.GREEN + "Regen: ", 20, 0, 120);



                hologram.delete();
            }
        });
    }

    public void blind(Material material, Player p){
        String text = ChatColor.GRAY.toString() + ChatColor.BOLD + "Blinding Light";
        ItemStack icon = new ItemStack(material);
        final Hologram hologram = HologramsAPI.createHologram(this, poweruploc);

        hologram.appendTextLine(text);
        ItemLine itemLine = hologram.appendItemLine(icon);

        itemLine.setPickupHandler(new PickupHandler() {
            @Override
            public void onPickup(Player player) {
                player.playSound(player.getLocation(), Sound.EXPLODE, 1F, 2F);

                player.playEffect(hologram.getLocation(), Effect.PARTICLE_SMOKE, null);

                for(Player p : Bukkit.getOnlinePlayers()){
                    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5*20, 2));
                }

                player.sendMessage(ChatColor.GOLD.toString() + ChatColor.ITALIC + "You have been blinded for " +
                        ChatColor.AQUA + "5 seconds " +  ChatColor.GOLD.toString() + ChatColor.ITALIC + "by " + ChatColor.BLUE + "BLU Team :(");

                actionBar(p, ChatColor.GRAY + "Blinding Light: ", 20, 0, 5);

                hologram.delete();
            }
        });
    }

    //starts appearing after 10 minutes mark
    public void Nuke(Material material, Player p){
        String text = ChatColor.GOLD.toString() + ChatColor.BOLD + "Nuclear Missile";
        ItemStack icon = new ItemStack(material);
        final Hologram hologram = HologramsAPI.createHologram(this, poweruploc);

        hologram.appendTextLine(text);
        ItemLine itemLine = hologram.appendItemLine(icon);

        itemLine.setPickupHandler(new PickupHandler() {
            @Override
            public void onPickup(Player player) {
                player.playSound(player.getLocation(), Sound.EXPLODE, 1F, 2F);

                player.playEffect(hologram.getLocation(), Effect.EXPLOSION, null);

                spawnNuke(p, p.getLocation());
                p.sendMessage(ChatColor.RED + "Red Team " + ChatColor.GOLD.toString() + ChatColor.ITALIC + "has launched their Nuclear Missiles!");

                hologram.delete();
            }
        });
    }


    public void spawnNuke(Player p, Location loc){
        new BukkitRunnable(){
            int n = 30;
            public void run(){
                n--;
                for(int i = 5; i <= 50; i += 15){
                    double x = i*random();
                    double y = i;
                    double z = i*random();
                    loc.add(x,y,z);
                    p.getLocation().getWorld().spawnEntity(loc, EntityType.PRIMED_TNT);
                    loc.subtract(x,y,z);
                    if(n <= 0){
                        this.cancel();
                    }
                }
            }
        }.runTaskTimer(this, 0,0);
    }

    public void Lightening(Material material, Player p){
        String text = ChatColor.WHITE.toString() + ChatColor.BOLD + "Thunder Storm";
        ItemStack icon = new ItemStack(material);
        final Hologram hologram = HologramsAPI.createHologram(this, poweruploc);

        hologram.appendTextLine(text);
        ItemLine itemLine = hologram.appendItemLine(icon);

        itemLine.setPickupHandler(new PickupHandler() {
            @Override
            public void onPickup(Player player) {
                player.playSound(player.getLocation(), Sound.AMBIENCE_THUNDER, 1F, 2F);

                player.playEffect(hologram.getLocation(), Effect.EXPLOSION, null);

                thunder(p, p.getLocation());
                p.sendMessage(ChatColor.RED + "Red Team" + ChatColor.GOLD.toString() + ChatColor.ITALIC + "'s Shamans have cursed the skies with thunder!");

                hologram.delete();
            }
        });
    }

    public void thunder(Player p, Location loc){
        actionBar(p, ChatColor.WHITE + "Thunder Storm: ", 4*20, 0, 7);
        new BukkitRunnable(){
            int n = 7;
            public void run(){
                n--;
                for(Entity entity : p.getLocation().getWorld().getNearbyEntities(p.getLocation(), 100,100,100)){
                    if(n % 2 == 0){
                        entity.getLocation().getWorld().strikeLightning(entity.getLocation());
                    }
                }
                if(n <= 0){
                    this.cancel();
                }
            }
        }.runTaskTimer(this, 0, 4*20);
    }

    public void spawnMist(Material material, Player p){
        actionBar(p, ChatColor.GRAY + "The Mist: ", 20, 0, 6);
        String text = ChatColor.DARK_GRAY.toString() + ChatColor.BOLD + "The Mist";
        ItemStack icon = new ItemStack(material);
        final Hologram hologram = HologramsAPI.createHologram(this, poweruploc);

        hologram.appendTextLine(text);
        ItemLine itemLine = hologram.appendItemLine(icon);

        itemLine.setPickupHandler(new PickupHandler() {
            @Override
            public void onPickup(Player player) {
                player.playSound(player.getLocation(), Sound.FIZZ, 1F, 2F);

                player.playEffect(hologram.getLocation(), Effect.CLOUD, null);

                mistParicles(p, p.getLocation());
                p.sendMessage(ChatColor.RED + "Red Team" + ChatColor.GOLD.toString() + ChatColor.ITALIC + " has called the dead from the ashes..");

                hologram.delete();
            }
        });
    }

    public void actionBar(Player player, String string, int ticks, final int n, int b){
        new BukkitRunnable(){
            int n = b;
            @Override
            public void run() {
                n--;

                int min = n / 60;
                int sec = n % 60;

                String timer = (min > 0 ? min + ":" : "") + sec;

                BountifulAPI.sendActionBar(player, string + timer);
                if(n <= 0){
                    this.cancel();
                }
            }
        }.runTaskTimer(this, 0, ticks);
    }

    public void mistParicles(Player p, Location location){
        new BukkitRunnable(){
            int n = 90;
            public void run(){
                n--;
                for (int i = 1; i <= 50; ++i){
                    double x = i*random();
                    double y = i*random();
                    double z = i*random();
                    location.add(x,y,z);
                    p.getLocation().getWorld().playEffect(location, Effect.CLOUD, 4);

                    for(Entity entity : p.getLocation().getChunk().getEntities()){
                        if( entity.getLocation().distance(location) < 1){
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 4));
                            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 4));
                            p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20, 4));
                        }
                    }
                    location.subtract(x,y,z);
                    if(n <= 0){
                        this.cancel();
                    }
                }
            }
        }.runTaskTimer(this, 0, 0);
    }
}