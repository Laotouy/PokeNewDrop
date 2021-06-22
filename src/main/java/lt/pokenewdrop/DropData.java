package lt.pokenewdrop;

import java.util.List;

public class DropData {

    private List<String> commands;

    private List<String> items;

    private KillPokemonEnum type = KillPokemonEnum.ALL;

    private boolean clear = true;

    private int random = 100;

    private String fileName = "";

    private String world = "world";

    public DropData(List<String> commands, List<String> items) {
        this.commands = commands;
        this.items = items;
    }

    public DropData(List<String> commands, List<String> items, KillPokemonEnum type, boolean clear,int random,String fileName,String world) {
        this.commands = commands;
        this.items = items;
        this.type = type;
        this.clear = clear;
        this.random = random;
        this.fileName = fileName;
        this.world = world;
    }

    public int getRandom() {
        return random;
    }

    public void setRandom(int random) {
        this.random = random;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public List<String> getCommands() {
        return commands;
    }


    public List<String> getItems() {
        return items;
    }

    public KillPokemonEnum getType() {
        return type;
    }

    public void setType(KillPokemonEnum type) {
        this.type = type;
    }



    public boolean isClear() {
        return clear;
    }

    public void setClear(boolean clear) {
        this.clear = clear;
    }



}
