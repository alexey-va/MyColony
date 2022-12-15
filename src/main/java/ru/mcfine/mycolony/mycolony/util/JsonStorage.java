package ru.mcfine.mycolony.mycolony.util;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.mcfine.mycolony.mycolony.MyColony;
import ru.mcfine.mycolony.mycolony.players.PlayerMock;
import ru.mcfine.mycolony.mycolony.regions.RegionMock;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonStorage {

    public void loadData(){
        Gson gson = new Gson();
        loadRegions(gson);
        loadPlayers(gson);
    }

    private void loadRegions(Gson gson){
        File file = new File(MyColony.plugin.getDataFolder().getAbsolutePath() + File.separator +"storage"+File.separator+"regions.json");
        try{
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (Exception ex){
            ex.printStackTrace();
        }

        Reader reader = null;
        try{
            reader = new FileReader(file);
        } catch (Exception ex){
            ex.printStackTrace();
        }

        try{
            Type listType = new TypeToken<ArrayList<RegionMock>>(){}.getType();
            MyColony.getRegionManager().uploadRegions(gson.fromJson(reader, listType));
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void loadPlayers(Gson gson){
        File file = new File(MyColony.plugin.getDataFolder().getAbsolutePath() + File.separator +"storage"+File.separator+"players.json");
        try{
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (Exception ex){
            ex.printStackTrace();
        }

        Reader reader = null;
        try{
            reader = new FileReader(file);
        } catch (Exception ex){
            ex.printStackTrace();
        }

        try{
            Type listType = new TypeToken<List<PlayerMock>>(){}.getType();
            MyColony.getRegionManager().uploadPlayers(gson.fromJson(reader, listType));
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void savePlayers(){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(MyColony.plugin.getDataFolder().getAbsolutePath() + File.separator + "storage" + File.separator + "players.json");
        //noinspection ResultOfMethodCallIgnored
        file.getParentFile().mkdirs();
        try {
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Writer writer;
        try {
            writer = new FileWriter(file, false);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        gson.toJson(MyColony.regionManager.getPlayerMock(), writer);
        try {
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveRegions() {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(MyColony.plugin.getDataFolder().getAbsolutePath() + File.separator + "storage" + File.separator + "regions.json");
        //noinspection ResultOfMethodCallIgnored
        file.getParentFile().mkdirs();
        try {
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Writer writer;
        try {
            writer = new FileWriter(file, false);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        gson.toJson(MyColony.regionManager.getRegionsMock(), writer);
        try {
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
