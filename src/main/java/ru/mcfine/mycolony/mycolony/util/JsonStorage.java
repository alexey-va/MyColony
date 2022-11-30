package ru.mcfine.mycolony.mycolony.util;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import ru.mcfine.mycolony.mycolony.MyColony;
import ru.mcfine.mycolony.mycolony.regions.RegionMock;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class JsonStorage {

    public void loadData(){
        Gson gson = new Gson();
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

}
