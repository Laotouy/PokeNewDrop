package lt.pokenewdrop.ui;

import Br.API.GUI.Ex.BaseUI;
import Br.API.GUI.Ex.Item;
import Br.API.GUI.Ex.SnapshotFactory;
import Br.API.GUI.Ex.UIManager;
import Br.API.ItemBuilder;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import lt.pokenewdrop.DropData;
import lt.pokenewdrop.PokeNewDrop;
import lt.pokenewdrop.listener.ChatListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Arrays;

public class UI_PokemonDropDataList_World extends BaseUI {
    SnapshotFactory factory = SnapshotFactory.getDefaultSnapshotFactory();


    public UI_PokemonDropDataList_World() {
        this.Rows = 6;
        this.AllowShift = false;
        this.DisplayName = "§e配置掉落物品列表";
        this.Name = "PokeNewDropUI_DropWorldList";

        load();
    }

    private final Item[] drops = new Item[36];
    Item is = Item.getNewInstance(ItemBuilder.getBuilder(Material.STAINED_GLASS_PANE).ammount(1).durability((short) 7).name("§f - ").build());

    public void load() {
        for (int i = 0; i < drops.length; i++) {
            int finalI = i;
            drops[i] = Item.getNewInstance(player -> {
                if (!PokeNewDrop.editPokemonType.containsKey(player.getUniqueId()) ||
                        !PokeNewDrop.INSTANCE.map.containsKey(PokeNewDrop.editPokemonType.get(player.getUniqueId())) ||
                        PokeNewDrop.INSTANCE.map.get(PokeNewDrop.editPokemonType.get(player.getUniqueId())).size() == 0 ||
                        finalI >= Bukkit.getServer().getWorlds().size()) {
                    return null;
                }


                World world = Bukkit.getServer().getWorlds().get(finalI);
                return ItemBuilder.getBuilder(Material.INK_SACK).durability(9).name(" §e ")
                        .lore(
                                "",
                                "  §f世界标识: " + world.getName(),
                                "  §f",
                                "").build();

            }).setClick(ClickType.LEFT, player -> {
                if (!PokeNewDrop.editPokemonType.containsKey(player.getUniqueId()) ||
                        !PokeNewDrop.INSTANCE.map.containsKey(PokeNewDrop.editPokemonType.get(player.getUniqueId())) ||
                        PokeNewDrop.INSTANCE.map.get(PokeNewDrop.editPokemonType.get(player.getUniqueId())).size() == 0 ||
                        finalI >= Bukkit.getServer().getWorlds().size()) {
                    return;
                }
                player.closeInventory();
                World world = Bukkit.getServer().getWorlds().get(finalI);

                DropData dropData = PokeNewDrop.editPokemonDropData.get(player.getUniqueId());
                dropData.setWorld(world.getName());
                EnumSpecies enumSpecies = PokeNewDrop.editPokemonType.get(player.getUniqueId());

                PokeNewDrop.INSTANCE.save(dropData, enumSpecies);

                UIManager.openUI(player,"PokeNewDropUI_Admin");
            });
        }
    }

    Item add = Item.getNewInstance(player -> {

        return ItemBuilder.getBuilder(Material.BEACON).name(" §e选择应用世界 ").lore(
                Arrays.asList("",
                        " §f在上面可选择特定世界",
                        "",
                        "  §f若要应用到全部世界请直接点击我",
                        "  §f",
                        "")).build();

    }).setClick(ClickType.LEFT,player -> {
        if (!PokeNewDrop.editPokemonType.containsKey(player.getUniqueId()) ||
                !PokeNewDrop.INSTANCE.map.containsKey(PokeNewDrop.editPokemonType.get(player.getUniqueId())) ||
                PokeNewDrop.INSTANCE.map.get(PokeNewDrop.editPokemonType.get(player.getUniqueId())).size() == 0
                ) {
            return;
        }
        player.closeInventory();

        DropData dropData = PokeNewDrop.editPokemonDropData.get(player.getUniqueId());
        dropData.setWorld("*");
        EnumSpecies enumSpecies = PokeNewDrop.editPokemonType.get(player.getUniqueId());

        PokeNewDrop.INSTANCE.save(dropData, enumSpecies);

        UIManager.openUI(player,"PokeNewDropUI_Admin");
    });

    @Override
    public Item getItem(Player player, int i) {
        if (i < drops.length) {
            return drops[i];
        }
        if (i >= 36 && i <= 44)
            return is;
        if (i == 49)
            return add;

        return null;
    }

    @Override
    public SnapshotFactory getSnapshotFactory() {
        return factory;
    }
}
