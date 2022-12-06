package ru.mcfine.mycolony.mycolony.production;

import ru.mcfine.mycolony.mycolony.regions.ProductionItem;

import java.util.ArrayList;
import java.util.List;

public class ProductionEntry {

    private List<ProductionItem> input;
    private List<ProductionItem> output;
    private double chance = 1.0;
    private double period = 100.0;
    private String name = "no_name";
    private int priority = 1;
    private ArrayList<Reagent> reagents;

    public ProductionEntry(List<ProductionItem> input, List<ProductionItem> output,
                           double chance, double period, String name, ArrayList<Reagent> reagents, int priority) {
        this.input = input;
        this.output = output;
        this.chance = chance;
        this.period = period;
        this.name = name;
        this.reagents = reagents;
        this.priority = priority;
    }


    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public List<ProductionItem> getInput() {
        return input;
    }

    public void setInput(ArrayList<ProductionItem> input) {
        this.input = input;
    }

    public List<ProductionItem> getOutput() {
        return output;
    }

    public void setOutput(ArrayList<ProductionItem> output) {
        this.output = output;
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public double getPeriod() {
        return period;
    }

    public void setPeriod(double period) {
        this.period = period;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Reagent> getReagents() {
        return reagents;
    }

    public void setReagents(ArrayList<Reagent> reagents) {
        this.reagents = reagents;
    }
}
