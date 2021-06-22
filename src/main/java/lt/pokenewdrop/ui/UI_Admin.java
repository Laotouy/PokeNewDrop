package lt.pokenewdrop.ui;

import Br.API.GUI.Ex.BaseUI;
import Br.API.GUI.Ex.Item;
import Br.API.GUI.Ex.SnapshotFactory;
import Br.API.GUI.Ex.UIManager;
import Br.API.ItemBuilder;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import lt.pokenewdrop.DropData;
import lt.pokenewdrop.KillPokemonEnum;
import lt.pokenewdrop.PokeNewDrop;
import lt.pokenewdrop.listener.ChatListener;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.io.File;
import java.util.Arrays;

public class UI_Admin extends BaseUI {
    SnapshotFactory factory = SnapshotFactory.getDefaultSnapshotFactory();

    public UI_Admin() {
        this.Rows = 1;
        this.AllowShift = false;
        this.DisplayName = "§e精灵掉落物编辑器";
        this.Name = "PokeNewDropUI_Admin";
    }

    @Override
    public Item getItem(Player player, int i) {
        if (i == 0)
            return edit_items;
        if (i == 1)
            return edit_commands;
        if (i == 2)
            return edit_type;
        if (i == 3)
            return clear;
        if (i == 4)
            return random;
        if (i == 5)
            return world;
        if (i == 8)
            return delete;

        return null;
    }

    Item edit_items = Item.getNewInstance(player -> ItemBuilder.getBuilder(Material.BONE).name(" §e管理掉落物品 ").build()).setClick(ClickType.LEFT, player -> {
        UIManager.openUI(player, "PokeNewDropUI_DropItemList");
    });

    Item edit_commands = Item.getNewInstance(player -> ItemBuilder.getBuilder(Material.COMMAND).name(" §e管理命令配置 ").build()).setClick(ClickType.LEFT, player -> {
        UIManager.openUI(player, "PokeNewDropUI_DropCommandsList");

    });
    Item world = Item.getNewInstance(player -> ItemBuilder.getBuilder(Material.CLAY_BALL).name(" §e设置应用世界 ").lore(
            "",
            "  §f当前设置: " + (PokeNewDrop.editPokemonDropData.get(player.getUniqueId()).getWorld().equals("*") ? "全部" : PokeNewDrop.editPokemonDropData.get(player.getUniqueId()).getWorld()),
            "",
            "  §f点击选择世界"

    ).build()).setClick(ClickType.LEFT, player -> {
        UIManager.openUI(player, "PokeNewDropUI_DropWorldList");

    });
    Item random = Item.getNewInstance(player -> {

        return ItemBuilder.getBuilder(Material.DIAMOND).name(" §e触发概率 ").lore(
                Arrays.asList("",
                        " §f点击以后 - 请在 十秒内在聊天栏内输入1 - 100 以内的数字",
                        "",
                        "  §f当前概率: " + PokeNewDrop.editPokemonDropData.get(player.getUniqueId()).getRandom() + "%",
                        ""
                )).build();

    }).setClick(ClickType.LEFT, player -> {
        player.closeInventory();
        ChatListener.inputLimitTime.remove(player.getUniqueId());
        ChatListener.inputRandomLimitTime.put(player.getUniqueId(), System.currentTimeMillis() + 10000); // 十秒限制 监控用户输入的字符
        player.sendMessage("§F请在十秒内在聊天栏中发送 1 到 100 以内的纯数字 ,若十秒内再次打开编辑器则中断此次输入.");


    });

    Item edit_type = Item.getNewInstance(player -> ItemBuilder.getBuilder(Material.WATCH).name(" §e管理掉落类型 ")
            .lore(
                    "",
                    "  §f当前类型: " + PokeNewDrop.editPokemonDropData.get(player.getUniqueId()).getType(),
                    "",
                    " §e类型介绍: ",
                    "  §fALL: 全部阵营的被击杀都会触发此配置",
                    "  §fBOSS: BOSS精灵被击杀才会触发此配置",
                    "  §fNORMAL: 野生普通精灵被击杀才会触发此配置",
                    "",
                    "  §c点击切换配置类型",
                    ""
            ).
                    build()).setClick(ClickType.LEFT, player -> {
        if (PokeNewDrop.editPokemonDropData.containsKey(player.getUniqueId()) && PokeNewDrop.editPokemonType.containsKey(player.getUniqueId())) {
            player.closeInventory();
            DropData dropData = PokeNewDrop.editPokemonDropData.get(player.getUniqueId());
            EnumSpecies enumSpecies = PokeNewDrop.editPokemonType.get(player.getUniqueId());


            switch (dropData.getType()) {
                case NORMAL:
                    dropData.setType(KillPokemonEnum.BOSS);
                    break;
                case BOSS:
                    dropData.setType(KillPokemonEnum.ALL);
                    break;
                case ALL:
                    dropData.setType(KillPokemonEnum.NORMAL);
                    break;
            }

            PokeNewDrop.INSTANCE.save(dropData, enumSpecies);
            player.sendMessage("§f类型被设置为: " + dropData.getType());
            UIManager.openUI(player, "PokeNewDropUI_Admin");


        }

    });

    Item clear = Item.getNewInstance(player -> ItemBuilder.getBuilder(Material.FLINT_AND_STEEL).name(" §e清理 ")
            .lore(
                    "",
                    "  §f当前类型: " + (PokeNewDrop.editPokemonDropData.get(player.getUniqueId()).isClear() ? "清理" : "不清理"),
                    "",
                    " §e类型介绍: ",
                    "  §f清理: 清理掉精灵原来的掉落物",
                    "  §f不清理: 在原有掉落物的基础上增加自己配置的掉落物",
                    "",
                    "  §c点击切换配置类型",
                    ""
            ).
                    build()).setClick(ClickType.LEFT, player -> {
        if (PokeNewDrop.editPokemonDropData.containsKey(player.getUniqueId()) && PokeNewDrop.editPokemonType.containsKey(player.getUniqueId())) {
            player.closeInventory();
            DropData dropData = PokeNewDrop.editPokemonDropData.get(player.getUniqueId());
            EnumSpecies enumSpecies = PokeNewDrop.editPokemonType.get(player.getUniqueId());


            dropData.setClear(!dropData.isClear());

            PokeNewDrop.INSTANCE.save(dropData, enumSpecies);
            player.sendMessage("§f类型被设置为: " + (dropData.isClear() ? "清理" : "不清理"));
            UIManager.openUI(player, "PokeNewDropUI_Admin");


        }

    });

    Item delete = Item.getNewInstance(player -> ItemBuilder.getBuilder(Material.BARRIER).name(" §e删除此配置 ").build()).setClick(ClickType.LEFT, player -> {
        if (PokeNewDrop.editPokemonDropData.containsKey(player.getUniqueId()) && PokeNewDrop.editPokemonType.containsKey(player.getUniqueId())) {
            DropData dropData = PokeNewDrop.editPokemonDropData.remove(player.getUniqueId());
            EnumSpecies enumSpecies = PokeNewDrop.editPokemonType.get(player.getUniqueId());
            PokeNewDrop.INSTANCE.map.get(enumSpecies).remove(dropData);
            player.closeInventory();

            File f = new File(PokeNewDrop.INSTANCE.getDataFolder(), enumSpecies.name() + "/" + dropData.getFileName());
            if (f.exists()) {
                f.delete();
                player.sendMessage("删除成功: " + f.getPath());

            } else {
                player.sendMessage("文件不存在: " + f.getPath());
            }

        }
    });


    @Override
    public SnapshotFactory getSnapshotFactory() {
        return factory;
    }
}
