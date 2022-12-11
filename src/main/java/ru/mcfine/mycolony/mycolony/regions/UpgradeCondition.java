package ru.mcfine.mycolony.mycolony.regions;

public class UpgradeCondition {

    private Condition condition;
    private int cityLevel;
    private double timePassed;
    private double moneyEarned;
    private String permission;
    private double moneyGap;
    private double popularSupport;

    public

    public enum Condition{
        CITY_LEVEL,
        TIME_PASSED,
        MONEY_EARNED,
        HAS_PERMISSION,
        MONEY_GAP,
        POPULAR_SUPPORT
    }
}
