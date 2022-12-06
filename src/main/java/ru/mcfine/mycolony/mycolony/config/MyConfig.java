package ru.mcfine.mycolony.mycolony.config;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import ru.mcfine.mycolony.mycolony.MyColony;
import ru.mcfine.mycolony.mycolony.production.ProductionEntry;
import ru.mcfine.mycolony.mycolony.regions.BuildingMaterial;
import ru.mcfine.mycolony.mycolony.regions.ProductionItem;
import ru.mcfine.mycolony.mycolony.regions.RegionType;
import ru.mcfine.mycolony.mycolony.requirements.Requirement;

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
    private HashMap<String, RegionType> regionTypes = new HashMap<>();
    public static HashMap<String, HashSet<Material>> buildingMaterialGroups = new HashMap<>();

    public MyConfig(){
        MyColony.plugin.saveDefaultConfig();
        loadConfig();

        String fileSeparator = FileSystems.getDefault().getSeparator();
        File regions = new File(MyColony.plugin.getDataFolder().toString() + fileSeparator + "regions");
        if(!regions.exists()) regions.mkdirs();

        try (Stream<Path> stream = Files.walk(Paths.get(regions.getPath()))) {
            stream.forEach(path -> readRegionFile(path.toFile()));
        } catch (Exception ex){
            ex.printStackTrace();
        }

        Reflections reflections = new Reflections("regions", Scanners.Resources);
        Set<String> set = reflections.getResources(".*").stream().map(s -> '/' + s).collect(Collectors.toSet());
        String path = MyColony.plugin.getDataFolder().toString()+fileSeparator;
        for(String s : set){
            copy(MyColony.class.getResourceAsStream(s), (path+s));
        }
    }

    public void copy(InputStream source, String destination){
        try{
            File file = new File(destination);
            if(!file.exists()){
                file.getParentFile().mkdirs();
                Files.copy(source, Paths.get(destination));
                readRegionFile(file);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void readRegionFile(File file){

        FileConfiguration data = YamlConfiguration.loadConfiguration(file);
        String name = data.getString("region-id", "default-id");

        // Building Materials
        ArrayList<BuildingMaterial> matList = new ArrayList<>();
        ConfigurationSection buildingSection =data.getConfigurationSection("building-materials");
        if(buildingSection != null) {
            Map<String, Object> buildingMaterialMap = buildingSection.getValues(false);
            for (Map.Entry<String, Object> entry : buildingMaterialMap.entrySet()) {
                try {
                    BuildingMaterial bMat = null;
                    if (this.buildingMaterialGroups.containsKey(entry.getKey())) {
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
        int xLeft =  data.getInt("left-offset",3 );
        int zForward =  data.getInt("forward-offset",3 );
        int zBackward =  data.getInt("backward-offset",3 );
        int yUp = data.getInt("up-offset",3 );
        int yDown = data.getInt("down-offset",3 );
        boolean isCity = data.getBoolean("is-city", false);
        Material dynmapMarker = Material.matchMaterial(data.getString("dynmap-marker", "CAKE"));
        Material shopIcon = Material.matchMaterial(data.getString("shop-icon", "BEDROCK"));
        double price = data.getDouble("price", 0.0);
        String displayName = data.getString("display-name", "display-name");
        boolean enabled = data.getBoolean("enabled", true);
        List<?> effectList = data.getList("effects");
        double time = data.getDouble("period", 100);


        if(effectList != null){
            try {
                effectList = (List<String>) effectList;
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }

        List<ProductionEntry> productionEntries = parseProductionEntries(data.getConfigurationSection("production"));
        List<Requirement> requirementEntries = parseReqs(data.getConfigurationSection(""));

        RegionType regionType = new RegionType(productionEntries,
                level, 1, 0, null, matList,
                xRight, xLeft, zBackward, zForward, yDown, yUp, isCity, dynmapMarker,
                shopIcon, price, displayName, enabled, requirementEntries);

        for(var req : regionType.getReqs()){
            System.out.println(req.getCityLevels()+" \n "+req.getReqRegions()+" \n "+req.getReq()+" | "+req.getPermission());
        }

        regionTypes.put(name, regionType);
    }

    private List<Requirement> parseReqs(ConfigurationSection section){
        System.out.println(section);
        if(section == null) return null;
        List<Requirement> result = new ArrayList<>();
        List<String> strings = section.getStringList("requirements");
        for(String s : strings){
            System.out.println("String: "+s);
            if(!s.contains("="))continue;
            String[] entries = s.split("=", 2);
            if(entries[0].equalsIgnoreCase("city_level")){
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
                for(String s2 : regionTypes){
                    Integer amount = null;
                    String[] regionSplit = s2.split(":");
                    if(regionSplit.length == 2){
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

    private List<ProductionEntry> parseProductionEntries(ConfigurationSection section){
        if(section == null) return null;
        List<ProductionEntry> result = new ArrayList<>();
        for(String prodKey : section.getKeys(false)){
            String name = section.getString(prodKey+".name", "no_name");
            int priority = section.getInt(prodKey+".priority", 1);
            double period = section.getDouble(prodKey+".period", 100);
            double chance = section.getDouble(prodKey+".chance", 1.0);
            List<String> inputStrings = section.getStringList(prodKey+".input");
            List<String> outputStrings = section.getStringList(prodKey+".output");

            List<ProductionItem> input = new ArrayList<>();
            for(String s : inputStrings){
                String[] matArray = s.split(":");
                boolean isTool = false;
                boolean isMoney = false;
                if(matArray.length > 1) isTool = matArray[1].equalsIgnoreCase("tool");
                if(matArray.length > 1) isMoney = matArray[1].equalsIgnoreCase("money");

                if(!isTool && !isMoney) {
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
                } else if(isTool){
                    int durability;
                    if (matArray.length > 2) durability = Integer.parseInt(matArray[2]);
                    else durability = 1;

                    HashSet<Material> group = getMaterialGroup(matArray[0]);
                    ProductionItem entry;
                    if (group != null) entry = new ProductionItem(matArray[0], durability, ProductionItem.Type.TOOL_GROUP);
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
            for(String s : outputStrings){
                String[] matArray = s.split(":");
                boolean isMoney = false;
                if(matArray.length > 1) isMoney = matArray[1].equalsIgnoreCase("money");

                if(!isMoney) {
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
                } else{
                    double amount = Double.parseDouble(matArray[0]);
                    output.add(new ProductionItem(amount));
                }
            }
            result.add(new ProductionEntry(input, output, chance, period, name, new ArrayList<>(), priority));
        }
        return result;
    }


    private void loadConfig(){
        ConfigurationSection section = MyColony.plugin.getConfig().getConfigurationSection("building-material-groups");
        if (section != null) {
            for (String groupName : section.getKeys(false)) {
                HashSet<Material> materials = new HashSet<>();
                String groupString = section.getString(groupName);
                if(groupString == null) continue;
                for (String s : groupString.split(",")) {
                    try {
                        materials.add(Material.matchMaterial(s));
                        //System.out.println(groupName+" | "+Material.matchMaterial(s));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                buildingMaterialGroups.put(groupName, materials);
            }
        }


    }

    public HashMap<String, HashSet<Material>> getBuildingMaterialGroups() {
        return buildingMaterialGroups;
    }

    public RegionType getRegionType(String regionName){
        return regionTypes.get(regionName);
    }

    public static HashSet<Material> getMaterialGroup(String groupName){
        if(buildingMaterialGroups == null) return null;
        return buildingMaterialGroups.get(groupName);
    }
}
