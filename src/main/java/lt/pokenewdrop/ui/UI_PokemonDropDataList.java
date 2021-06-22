package lt.pokenewdrop.ui;

import Br.API.GUI.Ex.BaseUI;
import Br.API.GUI.Ex.Item;
import Br.API.GUI.Ex.SnapshotFactory;
import Br.API.GUI.Ex.UIManager;
import Br.API.ItemBuilder;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import lt.pokenewdrop.DropData;
import lt.pokenewdrop.PokeNewDrop;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Arrays;

public class UI_PokemonDropDataList extends BaseUI {
    SnapshotFactory factory = SnapshotFactory.getDefaultSnapshotFactory();

    Item is = Item.getNewInstance(ItemBuilder.getBuilder(Material.STAINED_GLASS_PANE).ammount(1).durability((short) 7).name("§f - ").build());

    private final Item[] drops = new Item[36];

    public UI_PokemonDropDataList() {
        this.Rows = 6;
        this.AllowShift = false;
        this.DisplayName = "§e精灵掉落配置列表";
        this.Name = "PokeNewDropUI_DropList";

        load();
    }

    private void load() {

        for (int i = 0; i < drops.length; i++) {
            int finalI = i;
            drops[i] = Item.getNewInstance(player -> {
                if (!PokeNewDrop.editPokemonType.containsKey(player.getUniqueId()) ||
                        !PokeNewDrop.INSTANCE.map.containsKey(PokeNewDrop.editPokemonType.get(player.getUniqueId())) ||
                        PokeNewDrop.INSTANCE.map.get(PokeNewDrop.editPokemonType.get(player.getUniqueId())).size() == 0 ||
                        finalI >= PokeNewDrop.INSTANCE.map.get(PokeNewDrop.editPokemonType.get(player.getUniqueId())).size()) {
                    return null;
                }

                EnumSpecies enumSpecies = PokeNewDrop.editPokemonType.get(player.getUniqueId());
                DropData dropData = PokeNewDrop.INSTANCE.map.get(PokeNewDrop.editPokemonType.get(player.getUniqueId())).get(finalI);
                return ItemBuilder.getBuilder(Material.INK_SACK).durability(9).name(" §e配置: " + dropData.getFileName())
                        .lore(
                                "",
                                "  §f点击编辑此配置",
                                "",
                                "  §f配置位置: PokeNewDrop/" + enumSpecies.name() + "/" + dropData.getFileName(),
                                "  §f",
                                "").build();

            }).setClick(ClickType.LEFT, player -> {
                if (!PokeNewDrop.editPokemonType.containsKey(player.getUniqueId()) ||
                        !PokeNewDrop.INSTANCE.map.containsKey(PokeNewDrop.editPokemonType.get(player.getUniqueId())) ||
                        PokeNewDrop.INSTANCE.map.get(PokeNewDrop.editPokemonType.get(player.getUniqueId())).size() == 0 ||
                        finalI >= PokeNewDrop.INSTANCE.map.get(PokeNewDrop.editPokemonType.get(player.getUniqueId())).size()) {
                    return;
                }

                //   EnumSpecies enumSpecies = PokeNewDrop.editPokemonType.get(player.getUniqueId());
                DropData dropData = PokeNewDrop.INSTANCE.map.get(PokeNewDrop.editPokemonType.get(player.getUniqueId())).get(finalI);
                PokeNewDrop.editPokemonDropData.put(player.getUniqueId(), dropData);
                UIManager.openUI(player, "PokeNewDropUI_Admin");
            });
        }

    }

    @Override
    public Item getItem(Player player, int i) {
        if (i < drops.length) {
            return drops[i];
        }
        if (i >= 36 && i <= 44)
            return is;

        if (i == 49)
            return create;

        return null;
    }

    Item create = Item.getNewInstance(player -> {

        return ItemBuilder.getBuilder(Material.BEACON).name(" §e创建一个新的配置 ").lore(
                Arrays.asList("",
                        " §f为精灵栏第一只精灵创建一个掉落配置",
                        " §f配置可以创建多个",
                        " §f在触发掉落的时候会挨个配置去运行",
                        " §f如果在触发概率内则执行掉落配置中的设置",
                        "")).build();

    }).setClick(ClickType.LEFT, player -> {
        player.closeInventory();
        player.performCommand("pnd create");
    });


    @Override
    public SnapshotFactory getSnapshotFactory() {
        return factory;
    }
}
