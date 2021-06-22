package lt.pokenewdrop.ui;

import Br.API.GUI.Ex.BaseUI;
import Br.API.GUI.Ex.Item;
import Br.API.GUI.Ex.SnapshotFactory;
import Br.API.GUI.Ex.UIManager;
import Br.API.ItemBuilder;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import lt.pokenewdrop.DropData;
import lt.pokenewdrop.PokeNewDrop;
import lt.pokenewdrop.util.JsonToNBT;
import lt.pokenewdrop.util.NBTException;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Arrays;

public class UI_PokemonDropDataList_Items extends BaseUI {
    SnapshotFactory factory = SnapshotFactory.getDefaultSnapshotFactory();


    public UI_PokemonDropDataList_Items() {
        this.Rows = 6;
        this.AllowShift = false;
        this.DisplayName = "§e配置掉落物品列表";
        this.Name = "PokeNewDropUI_DropItemList";

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
                        finalI >= PokeNewDrop.editPokemonDropData.get(player.getUniqueId()).getItems().size()) {
                    return null;
                }

                String itemJson = PokeNewDrop.editPokemonDropData.get(player.getUniqueId()).getItems().get(finalI);
                try {
                    NBTTagCompound tag = JsonToNBT.getTagFromJson(itemJson);

                    return CraftItemStack.asBukkitCopy(new net.minecraft.server.v1_12_R1.ItemStack(tag));

                } catch (NBTException nbtException) {
                    nbtException.printStackTrace();
                }

                return null;

            }).setClick(ClickType.LEFT, player -> {
                if (!PokeNewDrop.editPokemonType.containsKey(player.getUniqueId()) ||
                        !PokeNewDrop.INSTANCE.map.containsKey(PokeNewDrop.editPokemonType.get(player.getUniqueId())) ||
                        PokeNewDrop.INSTANCE.map.get(PokeNewDrop.editPokemonType.get(player.getUniqueId())).size() == 0 ||
                        finalI >= PokeNewDrop.editPokemonDropData.get(player.getUniqueId()).getItems().size()) {
                    return;
                }
                player.closeInventory();
                DropData dropData = PokeNewDrop.editPokemonDropData.get(player.getUniqueId());
                dropData.getItems().remove(finalI);
                EnumSpecies enumSpecies = PokeNewDrop.editPokemonType.get(player.getUniqueId());

                PokeNewDrop.INSTANCE.save(dropData, enumSpecies);

                UIManager.openUI(player,"PokeNewDropUI_DropItemList");
            });
        }
    }

    Item add = Item.getNewInstance(player -> {

        return ItemBuilder.getBuilder(Material.BEACON).name(" §e添加一个掉落物 ").lore(
                Arrays.asList("",
                        " §f将手持的物品加入此掉落配置中",
                        "",
                        "  §f点击上方已添加的物品可将其删除",
                        "")).build();

    }).setClick(ClickType.LEFT, player -> {
        player.closeInventory();
        DropData dropData = PokeNewDrop.editPokemonDropData.get(player.getUniqueId());
        if (player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getType() == Material.AIR) {
            player.sendMessage("§c请确保主手的物品不为空才能添加为掉落物");
            return;
        }

        String nbtItem = CraftItemStack.asNMSCopy(player.getInventory().getItemInMainHand()).save(new NBTTagCompound()).toString();
        dropData.getItems().add(nbtItem);
        EnumSpecies enumSpecies = PokeNewDrop.editPokemonType.get(player.getUniqueId());

        PokeNewDrop.INSTANCE.save(dropData, enumSpecies);
        player.sendMessage("§F添加完成,已保存数据.");
        UIManager.openUI(player,"PokeNewDropUI_DropItemList");

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
