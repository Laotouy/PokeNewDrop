package lt.pokenewdrop;

import catserver.api.bukkit.event.ForgeEvent;
import com.google.common.collect.Maps;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.events.DropEvent;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.drops.DroppedItem;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import lt.pokenewdrop.commands.PncCommand;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PokeNewDrop extends JavaPlugin implements Listener {

    public Map<EnumSpecies, DropData> map = Maps.newHashMap();
    public static PokeNewDrop INSTANCE;

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

    }



    public void load() {

        map.clear();
        File file = new File(getDataFolder(), "new");
        if (!file.exists()) {
            return;
        }
        load(file);

    }

    public void load(File file) {
        if (file.isDirectory()) {
            for (File file1 : file.listFiles()) {
                load(file1);
            }
            return;
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        try {
            List<String> commands = yaml.getStringList("commands");
            List<String> items = yaml.getStringList("items");

            KillPokemonEnum type = KillPokemonEnum.valueOf(yaml.getString("type"));

            boolean clear = yaml.getBoolean("clear");
            DropData dropData = new DropData(commands, items, type, clear);
            EnumSpecies enumSpecies = EnumSpecies.valueOf(file.getName().replace(".yml", ""));
            map.put(enumSpecies, dropData);
        } catch (Exception ex) {
            getLogger().warning("异常 - 加载配置 /new/" + file.getName() + " 时出现问题,请检查配置内容");
        }
    }

    public void save(DropData dropData, EnumSpecies enumSpecies) {

        File folder = new File(getDataFolder(), "new");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File f = new File(folder, enumSpecies.name() + ".yml");

        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("commands", dropData.getCommands());
        yaml.set("items", dropData.getItems());
        yaml.set("type", dropData.getType().name());
        yaml.set("clear", dropData.isClear());
        try {
            yaml.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }

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

            DropData dropData = map.get(ep.getPokemonData().getSpecies());

            switch (dropData.getType()){
                case BOSS:{
                    if (!ep.isBossPokemon()){
                        return;
                    }
                }
                case NORMAL:{
                    if (ep.isBossPokemon()){
                        return;
                    }
                }
            }
            if (dropData.isClear()) {
                for (DroppedItem di : e.getDrops()) {
                    e.removeDrop(di);
                }
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
    }

}



