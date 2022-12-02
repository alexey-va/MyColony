package ru.mcfine.mycolony.mycolony.config;

import it.unimi.dsi.fastutil.Hash;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.yaml.snakeyaml.Yaml;
import ru.mcfine.mycolony.mycolony.MyColony;
import ru.mcfine.mycolony.mycolony.regions.BuildingMaterial;
import ru.mcfine.mycolony.mycolony.regions.ConsumableType;
import ru.mcfine.mycolony.mycolony.regions.RegionType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
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
    private HashMap<String, HashSet<Material>> buildingMaterialGroups = new HashMap<>();
    static Yaml yaml = new Yaml();

    public MyConfig(){
        MyColony.plugin.saveDefaultConfig();
        loadConfig();



        String fileSeparator = FileSystems.getDefault().getSeparator();
        File regions = new File(MyColony.plugin.getDataFolder().toString() + fileSeparator + "regions");
        if(!regions.exists()) regions.mkdirs();
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
            }

            InputStream inputStream = new FileInputStream(file);
            HashMap<String, Object> data = yaml.load(inputStream);
            //System.out.println(data);

            //Double income = (Double) data.get("income");
            //Integer level = (Integer) data.get("level");
            //Integer cityLevel = (Integer) data.get("city-level-for-upgrade");
            String name = (String) data.get("name");

            HashMap<String, Integer> buildingMaterialMap = (HashMap<String, Integer>) data.get("building-materials");
            ArrayList<BuildingMaterial> matList = new ArrayList<>();
            for(Map.Entry<String, Integer> entry : buildingMaterialMap.entrySet()){
                try {
                    BuildingMaterial bMat = null;
                    if (this.buildingMaterialGroups.containsKey(entry.getKey())) {
                        bMat = new BuildingMaterial(entry.getKey(), entry.getValue());
                    } else {
                        bMat = new BuildingMaterial(Material.matchMaterial(entry.getKey()), entry.getValue());
                    }
                    matList.add(bMat);
                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }

            ArrayList<ConsumableType> consumableTypes = null;
            Object object = data.get("consumables");
            if(object != null) {
                try {
                    HashMap<String, Integer> consumablesMap = (HashMap<String, Integer>) object;
                    consumableTypes = new ArrayList<>();
                    for (Map.Entry<String, Integer> entry : consumablesMap.entrySet()) {
                        try {
                            ConsumableType cMat = null;
                            Material material = Material.matchMaterial(entry.getKey());
                            if(material == null){
                                MyColony.plugin.getLogger().severe("Unknow material: "+entry.getKey());
                                continue;
                            }
                            cMat = new ConsumableType(material, entry.getValue());
                            consumableTypes.add(cMat);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }

            ArrayList<ConsumableType> productionList = null;
            Object object2 = data.get("production");
            if(object != null) {
                try {
                    HashMap<String, Integer> productionMap = (HashMap<String, Integer>) object2;
                    productionList = new ArrayList<>();
                    for (Map.Entry<String, Integer> entry : productionMap.entrySet()) {
                        try {
                            ConsumableType cMat = null;
                            Material material = Material.matchMaterial(entry.getKey());
                            if(material == null){
                                MyColony.plugin.getLogger().severe("Unknow material: "+entry.getKey());
                                continue;
                            }
                            cMat = new ConsumableType(Material.matchMaterial(entry.getKey()), entry.getValue());
                            productionList.add(cMat);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }

            Integer level = (Integer) data.get("level");

            Integer xRight = (Integer) data.get("xRight");
            Integer xLeft = (Integer) data.get("xLeft");

            Integer zForward = (Integer) data.get("zForward");
            Integer zBackward = (Integer) data.get("zBackward");

            Integer yUp = (Integer) data.get("yUp");
            Integer yDown = (Integer) data.get("yDown");

            RegionType regionType = new RegionType(consumableTypes, productionList,
                    level, 1, 0, null, matList,
                    xRight, xLeft, zBackward, zForward, yDown, yUp);

            System.out.println(name+" | "+regionType);
            regionTypes.put(name, regionType);
            

        } catch (Exception ex){
            ex.printStackTrace();
        }
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

    public HashSet<Material> getMaterialGroup(String groupName){
        if(buildingMaterialGroups == null) return null;
        return buildingMaterialGroups.get(groupName);
    }
}
