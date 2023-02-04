package tk.yongangame.mc.data;

import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class PlayerData {
    public UUID uuid;
    public String name;
    public double maxHealth;
    public double health;

    public PlayerData(UUID uuid){
        this.uuid = uuid;
        this.name = null;
        this.maxHealth =0;
        this.health = 0;
    }
    public PlayerData(Player player){
        this.uuid = player.getUUID();
        this.name = player.getName().getString();
        this.maxHealth =player.getMaxHealth();
        this.health = player.getHealth();
    }
}
