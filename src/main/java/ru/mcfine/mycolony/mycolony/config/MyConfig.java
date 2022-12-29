package ru.mcfine.mycolony.mycolony.config;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import ru.mcfine.mycolony.mycolony.MyColony;
import ru.mcfine.mycolony.mycolony.production.ProductionEntry;
import ru.mcfine.mycolony.mycolony.regimes.Regime;
import ru.mcfine.mycolony.mycolony.regimes.RegimeBuff;
import ru.mcfine.mycolony.mycolony.regions.BuildingMaterial;
import ru.mcfine.mycolony.mycolony.regions.ProductionItem;
import ru.mcfine.mycolony.mycolony.regions.RegionType;
import ru.mcfine.mycolony.mycolony.requirements.Requirement;
import ru.mcfine.mycolony.mycolony.shop.ShopGroup;

import java.io.File;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MyConfig {

    private File configFile;
    private static final Map<String, RegionType> regionTypes = new HashMap<>();
    private static final Map<String, Regime> regimeTypes = new HashMap<>();
    private static final Map<String, RegionGroup> regionGroups = new HashMap<>();
    private static final Map<String, ShopGroup> shopGroups = new HashMap<>();
    public static HashMap<String, HashSet<Material>> buildingMaterialGroups = new HashMap<>();

    public MyConfig() {
        MyColony.plugin.saveDefaultConfig();
        loadConfig();

        String fileSeparator = FileSystems.getDefault().getSeparator();
        File regions = new File(MyColony.plugin.getDataFolder() + fileSeparator + "regions");
        if (!regions.exists()) regions.mkdirs();

        try (Stream<Path> stream = Files.walk(Paths.get(regions.getPath()))) {
            stream.filter(path -> path.toString().endsWith(".yml")).forEach(path -> {
                System.out.println("Path: "+path);
                readRegionFile(path.toFile());
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Reflections reflections = new Reflections("regions", Scanners.Resources);
        Set<String> set = reflections.getResources(".*").stream().map(s -> '/' + s).collect(Collectors.toSet());
        String path = MyColony.plugin.getDataFolder() + fileSeparator;
        for (String s : set) {
            System.out.println(s);
            if(!s.endsWith(".yml")) continue;
            copy(MyColony.class.getResourceAsStream(s), (path + s));
        }
    }

    public static void copy(InputStream source, String destination) {
        try {
            File file = new File(destination);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                Files.copy(source, Paths.get(destination));
                readRegionFile(file);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void readRegionFile(File file) {
        FileConfiguration data = YamlConfiguration.loadConfiguration(file);
        String name = data.getString("region-id", "default-id");

        // Building Materials
        ArrayList<BuildingMaterial> matList = new ArrayList<>();
        ConfigurationSection buildingSection = data.getConfigurationSection("building-materials");
        if (buildingSection != null) {
            Map<String, Object> buildingMaterialMap = buildingSection.getValues(false);
            for (Map.Entry<String, Object> entry : buildingMaterialMap.entrySet()) {
                try {
                    BuildingMaterial bMat = null;
                    if (buildingMaterialGroups.containsKey(entry.getKey())) {
                        bMat = new BuildingMaterial(entry.getKey(), (Integer) entry.getValue());
                    } else {
                        bMat = new BuildingMaterial(Material.matchMaterial(entry.getKey()), (Integer) entry.getValue());
                    }
                    matList.add(bMat);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        int level = data.getInt("level", 1);
        int xRight = data.getInt("right-offset", 3);
        int xLeft = data.getInt("left-offset", 3);
        int zForward = data.getInt("forward-offset", 3);
        int zBackward = data.getInt("backward-offset", 3);
        int yUp = data.getInt("up-offset", 3);
        int yDown = data.getInt("down-offset", 3);
        boolean isCity = data.getBoolean("is-city", false);
        Material dynmapMarker = Material.matchMaterial(data.getString("dynmap-marker", "CAKE"));
        Material shopIcon = Material.matchMaterial(data.getString("shop-icon", "BEDROCK"));
        double price = data.getDouble("price", 0.0);
        String displayName = data.getString("display-name", "display-name");
        boolean enabled = data.getBoolean("enabled", true);
        List<?> effectList = data.getList("effects");
        String shopGroup = data.getString("shop-group", "default");
        int shopAmount = data.getInt("shop-amount", 1);
        int chunkRadius = data.getInt("chunk-radius", 5);
        List<String> groupNames = data.getStringList("groups");


        if (effectList != null) {
            try {
                effectList = effectList;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        List<ProductionEntry> productionEntries = parseProductionEntries(data.getConfigurationSection("production"));
        List<Requirement> requirementEntries = parseReqs(data.getConfigurationSection(""));

        if(productionEntries != null) productionEntries.sort(Comparator.comparingInt(ProductionEntry::getPriority));

        RegionType regionType = new RegionType(groupNames, productionEntries,
                level, 1, 0, null, matList,
                xRight, xLeft, zBackward, zForward, yDown, yUp, isCity, dynmapMarker,
                shopIcon, price, displayName, enabled, requirementEntries, shopGroup, shopAmount, name, chunkRadius, true);


        regionTypes.put(name, regionType);
        ShopGroup sg = shopGroups.get(shopGroup);
        if(sg == null) sg=shopGroups.get("default");
        System.out.println(regionType.getDisplayName()+" | "+file.getPath());
        sg.addRegionType(regionType);
    }

    private static List<Requirement> parseReqs(ConfigurationSection section) {
        if (section == null) return null;
        List<Requirement> result = new ArrayList<>();
        List<String> strings = section.getStringList("requirements");
        for (String s : strings) {
            if (!s.contains("=")) continue;
            String[] entries = s.split("=", 2);
            if (entries[0].equalsIgnoreCase("city_level")) {
                List<Integer> levels = new ArrayList<>();
                Arrays.stream(entries[1].split(",")).forEach(tmp -> levels.add(Integer.parseInt(tmp)));
                Requirement requirement = new Requirement(levels, Requirement.Req.CITY_LEVEL);
                result.add(requirement);
            } else if (entries[0].equalsIgnoreCase("permission")) {
                String permission = entries[1];
                Requirement requirement = new Requirement(permission, Requirement.Req.HAS_PERMISSION);
                result.add(requirement);
            } else if (entries[0].equalsIgnoreCase("has_regions")) {
                List<String> regionTypes = new ArrayList<>(Arrays.asList(entries[1].split(",")));
                Map<String, Integer> regionMap = new HashMap<>();
                for (String s2 : regionTypes) {
                    Integer amount = null;
                    String[] regionSplit = s2.split(":");
                    if (regionSplit.length == 2) {
                        amount = Integer.parseInt(regionSplit[1]);
                    } else amount = 1;
                    regionMap.put(regionSplit[0], amount);
                }
                Requirement requirement = new Requirement(regionMap, Requirement.Req.HAS_REGIONS);
                result.add(requirement);
            }
        }
        return result;
    }

    private static List<ProductionEntry> parseProductionEntries(ConfigurationSection section) {
        if (section == null) return null;
        List<ProductionEntry> result = new ArrayList<>();
        for (String prodKey : section.getKeys(false)) {
            String name = section.getString(prodKey + ".name", "no_name");
            int priority = section.getInt(prodKey + ".priority", 1);
            double period = section.getDouble(prodKey + ".period", 100);
            double chance = section.getDouble(prodKey + ".chance", 1.0);
            List<String> inputStrings = section.getStringList(prodKey + ".input");
            List<String> outputStrings = section.getStringList(prodKey + ".output");

            List<ProductionItem> input = new ArrayList<>();
            for (String s : inputStrings) {
                String[] matArray = s.split(":");
                boolean isTool = false;
                boolean isMoney = false;
                if (matArray.length > 1) isTool = matArray[1].equalsIgnoreCase("tool");
                if (matArray.length > 1) isMoney = matArray[1].equalsIgnoreCase("money");

                if (!isTool && !isMoney) {
                    int amount;
                    if (matArray.length > 1) amount = Integer.parseInt(matArray[1]);
                    else amount = 1;

                    HashSet<Material> group = getMaterialGroup(matArray[0]);
                    ProductionItem entry;
                    if (group != null) entry = new ProductionItem(matArray[0], amount, ProductionItem.Type.GROUP);
                    else {
                        Material material = Material.matchMaterial(matArray[0]);
                        if (material == null) {
                            MyColony.plugin.getLogger().severe("Error parsing string! " + s);
                            continue;
                        }
                        entry = new ProductionItem(material, amount, ProductionItem.Type.MATERIAL);
                    }
                    input.add(entry);
                } else if (isTool) {
                    int durability;
                    if (matArray.length > 2) durability = Integer.parseInt(matArray[2]);
                    else durability = 1;

                    HashSet<Material> group = getMaterialGroup(matArray[0]);
                    ProductionItem entry;
                    if (group != null)
                        entry = new ProductionItem(matArray[0], durability, ProductionItem.Type.TOOL_GROUP);
                    else {
                        Material material = Material.matchMaterial(matArray[0]);
                        if (material == null) {
                            MyColony.plugin.getLogger().severe("Error parsing string! " + s);
                            continue;
                        }
                        entry = new ProductionItem(material, durability, ProductionItem.Type.TOOL);
                    }
                    input.add(entry);
                } else {
                    double amount = Double.parseDouble(matArray[0]);
                    input.add(new ProductionItem(amount));
                }
            }

            List<ProductionItem> output = new ArrayList<>();
            for (String s : outputStrings) {
                String[] matArray = s.split(":");
                boolean isMoney = false;
                if (matArray.length > 1) isMoney = matArray[1].equalsIgnoreCase("money");

                if (!isMoney) {
                    int amount;
                    if (matArray.length > 1) amount = Integer.parseInt(matArray[1]);
                    else amount = 1;

                    HashSet<Material> group = getMaterialGroup(matArray[0]);
                    ProductionItem entry;
                    if (group != null) entry = new ProductionItem(matArray[0], amount, ProductionItem.Type.GROUP);
                    else {
                        Material material = Material.matchMaterial(matArray[0]);
                        if (material == null) {
                            MyColony.plugin.getLogger().severe("Error parsing string! " + s);
                            continue;
                        }
                        entry = new ProductionItem(material, amount, ProductionItem.Type.MATERIAL);
                    }
                    output.add(entry);
                } else {
                    double amount = Double.parseDouble(matArray[0]);
                    output.add(new ProductionItem(amount));
                }
            }
            result.add(new ProductionEntry(input, output, chance, period, name, new ArrayList<>(), priority));
        }
        return result;
    }

    private void readRegimeFile(File file){
        FileConfiguration data = YamlConfiguration.loadConfiguration(file);
        String id = data.getString("regime-id", null);
        if(id == null) {
            MyColony.plugin.getLogger().severe("File "+file+" has no regime-id! Skipping!");
            return;
        }

        String iconString = data.getString("icon-material", "BEDROCK");
        Material material = Material.matchMaterial(iconString);
        if(material == null) material = Material.BEDROCK;
        String displayName = data.getString("display-name", "No display name");
        List<RegimeBuff> regimeBuffs = parseRegimeBuffs(data.getConfigurationSection("regime-buffs"));
        Regime regime = new Regime(material, displayName, regimeBuffs);
        regimeTypes.put(id, )

    }

    private List<RegimeBuff> parseRegimeBuffs(ConfigurationSection section){
        if(section == null) return new ArrayList<>();
        List<RegimeBuff> regimeBuffs = new ArrayList<>();
        for(String key : section.getKeys(false)){
            String typeStr = section.getString(key+".type", null);
            RegimeBuff.Type type = RegimeBuff.matchType(typeStr);
            if(type == null){
                MyColony.plugin.getLogger().severe("Regime buff with key "+key+" does not have a type.");
                continue;
            }
            String displayName = section.getString(key+".display-name", "display-name");
            String loreString = section.getString(key+".lore", null);
            List<String> loreList = section.getStringList(key+".lore");
            if(loreList.size() == 0 && loreString != null) loreList.add(loreString);
            double amount = section.getDouble(key+".amount", 1.0);

            RegimeBuff regimeBuff = new RegimeBuff(type, amount, displayName, loreList, key);
            regimeBuffs.add(regimeBuff);
        }
        return regimeBuffs;
    }


    private void loadConfig() {

        // Building material GROUPS
        ConfigurationSection section = MyColony.plugin.getConfig().getConfigurationSection("building-material-groups");
        if (section != null) {
            for (String groupName : section.getKeys(false)) {
                HashSet<Material> materials = new HashSet<>();
                String groupString = section.getString(groupName);
                if (groupString == null) continue;
                for (String s : groupString.split(",")) {
                    try {
                        materials.add(Material.matchMaterial(s));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                buildingMaterialGroups.put(groupName, materials);
            }
        }

        // Region groups
        ConfigurationSection groupsSection = MyColony.plugin.getConfig().getConfigurationSection("region-groups");
        if(groupsSection != null){
            for(String key : groupsSection.getKeys(false)){
                String displayName = groupsSection.getString(key+".display-name", key);
                RegionGroup regionGroup = new RegionGroup(key, displayName);
                regionGroups.put(key, regionGroup);
            }
        }


        // Shop Groups
        List<String> defaultDescription = new ArrayList<>();
        defaultDescription.add("Default group");
        ShopGroup defaultGroup = new ShopGroup("default", Material.BEDROCK, 1, null,
                defaultDescription, 1);

        section = MyColony.plugin.getConfig().getConfigurationSection("shop-groups");
        shopGroups.put("default", defaultGroup);
        if(section != null){
            for(String key : section.getKeys(false)){
                int priority = section.getInt(key+".priority", 1);
                String iconString = section.getString(key+".icon", "BEDROCK");
                Material material = Material.matchMaterial(iconString);
                if(material == null) material = Material.REDSTONE_BLOCK;
                int iconAmount = section.getInt(key+".icon-amount", 1);
                String name = section.getString(key+".name", "No name");
                List<String> descriptionList = section.getStringList(key+".description");
                String permission = section.getString(key+".permission", null);

                ShopGroup shopGroup = new ShopGroup(name, material, iconAmount, permission, descriptionList, priority);
                shopGroups.put(key, shopGroup);
            }
        }


    }

    public File getConfigFile() {
        return configFile;
    }

    public Map<String, RegionType> getRegionTypes() {
        return regionTypes;
    }

    public Map<String, ShopGroup> getShopGroups() {
        return shopGroups;
    }

    public HashMap<String, HashSet<Material>> getBuildingMaterialGroups() {
        return buildingMaterialGroups;
    }

    public RegionType getRegionType(String regionName) {
        return regionTypes.get(regionName);
    }

    public Map<String, RegionType> getRegionTypesMap() {
        return regionTypes;
    }

    public static HashSet<Material> getMaterialGroup(String groupName) {
        if (buildingMaterialGroups == null) return null;
        return buildingMaterialGroups.get(groupName);
    }
}
