package ru.mcfine.mycolony.mycolony.regions;

import org.bukkit.Material;

public class ConsumableType {
    public Material material = Material.STONE;
    public int amount = 0;

    public ConsumableType(Material material, int amount) {
        this.material = material;
        this.amount = amount;
    }
}
