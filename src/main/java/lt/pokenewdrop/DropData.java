package lt.pokenewdrop;

import java.util.List;

public class DropData {

    private List<String> commands;

    private List<String> items;

    private KillPokemonEnum type = KillPokemonEnum.ALL;

    private boolean clear = false;


    public DropData(List<String> commands, List<String> items) {
        this.commands = commands;
        this.items = items;
    }

    public DropData(List<String> commands, List<String> items, KillPokemonEnum type, boolean clear) {
        this.commands = commands;
        this.items = items;
        this.type = type;
        this.clear = clear;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
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

    public void setItems(List<String> items) {
        this.items = items;
    }


    public boolean isClear() {
        return clear;
    }

    public void setClear(boolean clear) {
        this.clear = clear;
    }

    public class Drop{

        int i;
        String str;

        public Drop(int i, String str) {
            this.i = i;
            this.str = str;
        }
    }

}
