package ru.mcfine.mycolony.mycolony.regions;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import ru.mcfine.mycolony.mycolony.MyColony;
import ru.mcfine.mycolony.mycolony.city.CityRegion;

public class UpgradeCondition {

    private final Condition condition;
    private int cityLevel;
    private double timePassed;
    private double moneyEarned;
    private String permission;
    private double moneyGap;
    private double popularSupport;

    public UpgradeCondition(Condition condition, int a){
        this.condition = condition;
        if(condition == Condition.CITY_LEVEL){
            this.cityLevel = a;
        }
    }

    public UpgradeCondition(Condition condition, double a){
        this.condition = condition;
        switch (condition) {
            case TIME_PASSED -> this.timePassed = a;
            case MONEY_EARNED -> this.moneyEarned = a;
            case MONEY_GAP -> this.moneyGap = a;
            case POPULAR_SUPPORT -> this.popularSupport = a;
        }
    }

    public UpgradeCondition(Condition condition, String a){
        this.condition = condition;
        if(condition == Condition.HAS_PERMISSION) this.permission = a;
    }

    public boolean satisfy(String playerName, String worldName, Region region, CityRegion cityRegion){
        switch (condition) {
            case HAS_PERMISSION:
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
                return MyColony.perms.playerHas(worldName, offlinePlayer, this.permission);
            case CITY_LEVEL:
                if (cityRegion == null) return false;
                return cityRegion.getLevel() >= this.cityLevel;
            case MONEY_EARNED:
                return (region.getTotalIncome() >= this.moneyEarned);
            case TIME_PASSED:
                return (region.getTimeSinceCreation() >= this.timePassed);
            case MONEY_GAP:
                return true;
            case POPULAR_SUPPORT:
                return true;
        }
        return true;
    }

    public enum Condition{
        CITY_LEVEL,
        TIME_PASSED,
        MONEY_EARNED,
        HAS_PERMISSION,
        MONEY_GAP,
        POPULAR_SUPPORT
    }
}
