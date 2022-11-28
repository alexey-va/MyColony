package ru.mcfine.mycolony.mycolony.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.mcfine.mycolony.mycolony.MyColony;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class MyConfig {

    private File customConfigFile;
    private HashMap<String, FileConfiguration> customConfigMap;
    private String fileSeparator = FileSystems.getDefault().getSeparator();

    public MyConfig(){
        try(Stream<Path> stream = Files.walk(Paths.get(MyColony.plugin.getDataFolder()+fileSeparator+"regions"+fileSeparator))){
             stream.forEach(file -> {
                 createCustomConfig(file.getParent().toAbsolutePath(), file.getFileName());
             });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveDefaultCustomConfig(){
        File regions = new File(MyColony.plugin.getDataFolder().toString() + fileSeparator + "regions");
        if(!regions.exists()) regions.mkdir();
        try{
            List<String> files = ResourceUti;
        }
    }

    private static List<String> listFiles(Class<?> clas, String path) throws IOException{
        List<String> files= new ArrayList<>();
        URL url = clas.getResource(path);
        if(url == null) throw new IllegalArgumentException("list files failed");
        if()
    }

    private void createCustomConfig(Path resourcePath, Path filename){
        System.out.println(resourcePath.toString()+" | "+filename.toString()+" | "+filename.toString().substring(0,filename.toString().lastIndexOf(".")));
        customConfigFile = new File(resourcePath.toString(), filename.toString());

        if(!customConfigFile.exists()){
            customConfigFile.getParentFile().mkdirs();
            MyColony.plugin.saveResource(resourcePath.toString(), false);
        }

        FileConfiguration customConfig = new YamlConfiguration();
        try{
            customConfig.load(customConfigFile);
        }catch (IOException | InvalidConfigurationException e){
            e.printStackTrace();
        }

        customConfigMap.put(filename.toString().substring(0,filename.toString().lastIndexOf(".")), customConfig);

    }

}
