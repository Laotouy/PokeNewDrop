package lt.pokenewdrop;

import Br.API.GUI.Ex.UIManager;
import catserver.api.bukkit.event.ForgeEvent;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.events.DropEvent;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.drops.DroppedItem;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import lt.pokenewdrop.commands.PncCommand;
import lt.pokenewdrop.listener.ChatListener;
import lt.pokenewdrop.ui.*;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PokeNewDrop extends JavaPlugin implements Listener {

    public Map<EnumSpecies, List<DropData>> map = Maps.newHashMap();
    public static PokeNewDrop INSTANCE;
    public static String PREFIX = "§7(§c!§7)§f ";
    public static Map<UUID,EnumSpecies> editPokemonType = new HashMap<>();
    public static Map<UUID,DropData> editPokemonDropData = new HashMap<>();

    @Override
    public void onEnable() {

        INSTANCE = this;


        Bukkit.getServer().getPluginManager().registerEvents(this, this);

        getCommand("pnd").setExecutor(new PncCommand());
        load();
        StringBuilder stringBuilder = new StringBuilder();
        map.keySet().forEach(key -> stringBuilder.append(key.name).append(","));

        getLogger().info("成功载入新的掉落配置 - " + stringBuilder.toString());

        Bukkit.getScheduler().runTaskAsynchronously(this, AD::new);

        UIManager.RegisterUI(new UI_PokemonDropDataList());
        UIManager.RegisterUI(new UI_Admin());
        UIManager.RegisterUI(new UI_PokemonDropDataList_Items());
        UIManager.RegisterUI(new UI_PokemonDropDataList_Commands());
        UIManager.RegisterUI(new UI_PokemonDropDataList_World());

        getServer().getPluginManager().registerEvents(new ChatListener(), INSTANCE);
    }


    public void load() {

        map.clear();
        File file = getDataFolder();
        if (!file.exists()) {
            System.out.println("目录不存在");
            return;
        }
        load(file);

    }

    public void load(File file) {
        System.out.println("开始加载 - " + file.getPath());
        if (file.isDirectory()) {
            for (File file1 : file.listFiles()) { // 遍历所有精灵文件夹
                System.out.println("filename: " + file1.getName());
                getLogger().info("开始加载文件夹: " + file1.getName() + " 下的精灵配置");
                try {
                    EnumSpecies.valueOf(file1.getName());
                    load(EnumSpecies.valueOf(file1.getName()),file1);
                } catch (Exception ex) {
                    getLogger().warning("文件夹昵称为: " + file1.getName() + "的精灵不存在,跳过 " + file1.getName() + "的加载");
                }
            }
        }
    }

    public void load(EnumSpecies species, File file) {

        if (file.isDirectory()) {

            for (File file_Yaml : file.listFiles()) {
                if (!file_Yaml.isDirectory()) {
                    getLogger().info("开始加载 " + species.name() + " 的配置: " + file_Yaml.getName());
                    loadPokemonData(species, file_Yaml);
                }
            }

        } else {
            getLogger().warning(file.getPath() + "不是文件夹,加载异常");
        }

    }


    public void loadPokemonData(EnumSpecies species, File file) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        try {
            List<String> commands = yaml.getStringList("commands");
            List<String> items = yaml.getStringList("items");

            KillPokemonEnum type = KillPokemonEnum.valueOf(yaml.getString("type"));

            boolean clear = yaml.getBoolean("clear");
            int random = yaml.getInt("random");
            String fileName = yaml.getString("fileName");
            String world = yaml.getString("world");
            DropData dropData = new DropData(commands, items, type, clear, random,fileName,world);
            if (map.containsKey(species)) {
                map.get(species).add(dropData);
            } else {
                map.put(species, new ArrayList<>(Collections.singletonList(dropData)));
            }

        } catch (Exception ex) {
            getLogger().warning("异常 - 加载配置 plugins/PokeNewDrop/" +
                    "" + species.name() + "/" + file.getName() + " 时出现问题,请检查配置内容");
            ex.printStackTrace();
        }

    }

    /*
    \ 返回值: 文件的名字
     */
    public String save(DropData dropData, EnumSpecies enumSpecies) {

        File folder = new File(getDataFolder(), enumSpecies.name());
        if (!folder.exists()) {
            folder.mkdirs();
        }
        int i = folder.listFiles().length;

        File f = new File(folder, enumSpecies.name() + "_" + i + ".yml");
        if (dropData.getFileName().isEmpty()) {
            while (f.exists()) {
                i++;
                f = new File(folder, enumSpecies.name() + "_" + i + ".yml");
            }
        }else {
            f = new File(folder, dropData.getFileName());
        }

        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("commands", dropData.getCommands());
        yaml.set("items", dropData.getItems());
        yaml.set("type", dropData.getType().name());
        yaml.set("clear", dropData.isClear());
        yaml.set("random", dropData.getRandom());
        yaml.set("world", dropData.getWorld());
        try {
            yaml.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        yaml.set("fileName",f.getName());
        dropData.setFileName(f.getName());
        try {
            yaml.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f.getName();
    }

    @EventHandler
    public void onForge(ForgeEvent event) {

        if (event.getForgeEvent() instanceof DropEvent) {
            DropEvent e = (DropEvent) event.getForgeEvent();

            if (e.isCanceled())
                return;
            if (!e.isPokemon()) {
                return;
            }

            EntityPixelmon ep = (EntityPixelmon) e.entity;
            if (!map.containsKey(ep.getPokemonData().getSpecies())) {
                return;
            }

            List<DropData> drops = map.get(ep.getPokemonData().getSpecies());
            List<DroppedItem> droppedItems = new ArrayList<>(e.getDrops());
            for (DroppedItem di : e.getDrops()) {
                e.removeDrop(di);
            }
            for (DropData dropData : drops) {
                switch (dropData.getType()) {
                    case BOSS: {
                        if (!ep.isBossPokemon()) {
                            return;
                        }
                    }
                    case NORMAL: {
                        if (ep.isBossPokemon()) {
                            return;
                        }
                    }
                }
                int num = (int)(Math.random()*100) + 1;
                if (num > dropData.getRandom()){
                    return;
                }
                if (dropData.isClear()) {
                    droppedItems.clear();
                }
                for (String itemJSON : dropData.getItems()) {
                    try {
                        net.minecraft.nbt.NBTTagCompound tag = JsonToNBT.func_180713_a(itemJSON);
                        e.addDrop(new net.minecraft.item.ItemStack(tag));
                    } catch (NBTException nbtException) {
                        nbtException.printStackTrace();
                    }
                }

                for (String cmd : dropData.getCommands()) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", e.player.func_70005_c_()));
                }
            }
            if (droppedItems.size() != 0){
                droppedItems.forEach(droppedItem -> {
                    e.addDrop(droppedItem.itemStack);
                });
            }
        }

    }

}



