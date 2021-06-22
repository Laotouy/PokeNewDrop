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
import lt.pokenewdrop.util.JsonToNBT;
import lt.pokenewdrop.util.NBTException;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Arrays;

public class UI_PokemonDropDataList_Commands extends BaseUI {
    SnapshotFactory factory = SnapshotFactory.getDefaultSnapshotFactory();


    public UI_PokemonDropDataList_Commands() {
        this.Rows = 6;
        this.AllowShift = false;
        this.DisplayName = "§e配置掉落物品列表";
        this.Name = "PokeNewDropUI_DropCommandsList";

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
                        finalI >= PokeNewDrop.editPokemonDropData.get(player.getUniqueId()).getCommands().size()) {
                    return null;
                }

                String cmd = PokeNewDrop.editPokemonDropData.get(player.getUniqueId()).getCommands().get(finalI);
                return ItemBuilder.getBuilder(Material.INK_SACK).durability(9).name(" §e ")
                        .lore(
                                "",
                                "  §f执行后台命令: " + cmd,
                                "  §f",
                                "").build();

            }).setClick(ClickType.LEFT, player -> {
                if (!PokeNewDrop.editPokemonType.containsKey(player.getUniqueId()) ||
                        !PokeNewDrop.INSTANCE.map.containsKey(PokeNewDrop.editPokemonType.get(player.getUniqueId())) ||
                        PokeNewDrop.INSTANCE.map.get(PokeNewDrop.editPokemonType.get(player.getUniqueId())).size() == 0 ||
                        finalI >= PokeNewDrop.editPokemonDropData.get(player.getUniqueId()).getCommands().size()) {
                    return;
                }
                player.closeInventory();
                DropData dropData = PokeNewDrop.editPokemonDropData.get(player.getUniqueId());
                dropData.getCommands().remove(finalI);
                EnumSpecies enumSpecies = PokeNewDrop.editPokemonType.get(player.getUniqueId());

                PokeNewDrop.INSTANCE.save(dropData, enumSpecies);

                UIManager.openUI(player,"PokeNewDropUI_DropCommandsList");
            });
        }
    }

    Item add = Item.getNewInstance(player -> {

        return ItemBuilder.getBuilder(Material.BEACON).name(" §e添加一个命令 ").lore(
                Arrays.asList("",
                        " §f点击以后 请在 十秒内在聊天栏内输入你要添加的命令",
                        " §f 无需输入斜杠 '/' 直接后台那样输入命令即可",
                        "",
                        "  §f点击上方已添加的命令可将其删除",
                        "  §f",
                        "  §f可用变量: ",
                        "   §f击杀玩家: %player%",
                        "")).build();

    }).setClick(ClickType.LEFT, player -> {
        player.closeInventory();

        ChatListener.inputRandomLimitTime.remove(player.getUniqueId());
        ChatListener.inputLimitTime.put(player.getUniqueId(),System.currentTimeMillis() + 10000); // 十秒限制 监控用户输入的字符
        player.sendMessage("§F请在十秒内在聊天栏中发送你要添加的命令,无需在命令前面加斜杠( '/'),若十秒内再次打开编辑器则中断此次输入.");



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
