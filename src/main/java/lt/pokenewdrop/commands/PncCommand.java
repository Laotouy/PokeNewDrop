package lt.pokenewdrop.commands;

import Br.API.GUI.Ex.UIManager;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import lt.pokenewdrop.AD;
import lt.pokenewdrop.DropData;
import lt.pokenewdrop.KillPokemonEnum;
import lt.pokenewdrop.PokeNewDrop;
import lt.pokenewdrop.listener.ChatListener;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PncCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {


        if (sender.isOp() && args.length == 1 && args[0].equalsIgnoreCase("reload")) {

            PokeNewDrop.INSTANCE.load();
            sender.sendMessage("§c重置完成,详细内容查看控制台输出");

            Bukkit.getScheduler().runTaskAsynchronously(PokeNewDrop.INSTANCE, AD::new);

            return true;
        }

        if (!sender.isOp() || !(sender instanceof Player)) {
            sender.sendMessage("§c此命令仅允许管理员玩家执行");
            return true;
        }
        Player player = (Player) sender;

        PlayerPartyStorage pps = Pixelmon.storageManager.getParty(player.getUniqueId());
        if (pps.getTeam().size() == 0 || pps.get(0) == null) {
            sender.sendMessage("§c请确保精灵栏第一个位置的精灵不为空");
            return true;
        }

        EnumSpecies enumSpecies = pps.get(0).getSpecies();

        if (args.length == 1 && args[0].equalsIgnoreCase("create")) {

            DropData dropData = new DropData(new ArrayList<>(), new ArrayList<>());
            if (!PokeNewDrop.INSTANCE.map.containsKey(enumSpecies)) {
                List<DropData> dropDatas = new ArrayList<>();
                dropDatas.add(dropData);
                PokeNewDrop.INSTANCE.map.put(enumSpecies, dropDatas);
            } else {
                PokeNewDrop.INSTANCE.map.get(enumSpecies).add(dropData);
            }

            String fileName = PokeNewDrop.INSTANCE.save(dropData, enumSpecies);
            sender.sendMessage(PokeNewDrop.PREFIX + "创建完成,你可以在服务端路径内的 plugins/PokeNewDrop/" + enumSpecies.name() + "/" + fileName + " 找到这只精灵你所创建的配置文件");
            return true;
        }


        if (player.isOp() && args.length == 1 && args[0].equalsIgnoreCase("edit")) {
            if ( ( ChatListener.inputLimitTime.containsKey(player.getUniqueId()) && System.currentTimeMillis() < ChatListener.inputLimitTime.get(player.getUniqueId()) )
            || ( ChatListener.inputRandomLimitTime.containsKey(player.getUniqueId()) && System.currentTimeMillis() < ChatListener.inputRandomLimitTime.get(player.getUniqueId()) )){
                player.sendMessage("设置操作结束,请再次输入打开编辑器.");
                ChatListener.inputLimitTime.remove(player.getUniqueId());
                ChatListener.inputRandomLimitTime.remove(player.getUniqueId());
                return true;
            }
            PokeNewDrop.editPokemonType.put(player.getUniqueId(),enumSpecies);
            UIManager.openUI(player, "PokeNewDropUI_DropList");
            return true;
        }

//        if (!PokeNewDrop.INSTANCE.map.containsKey(enumSpecies)) {
//            sender.sendMessage("§c此精灵的配置不存在,请先输入/pnd create    来为你精灵烂第一只精灵创建配置");
//            return true;
//        }
//        DropData dropData = PokeNewDrop.INSTANCE.map.get(enumSpecies);
//
//
//        if (args.length == 1 && args[0].equalsIgnoreCase("item")) {
//
//
//            if (player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getType() == Material.AIR) {
//                sender.sendMessage("§c请确保主手的物品不为空才能添加为掉落物");
//                return true;
//            }
//
//            String nbtItem = CraftItemStack.asNMSCopy(player.getInventory().getItemInMainHand()).save(new NBTTagCompound()).toString();
//
//            NBTTagCompound nbtTagCompound = new NBTTagCompound();
//
//
//            CraftItemStack.asBukkitCopy(new net.minecraft.server.v1_12_R1.ItemStack(nbtTagCompound));
//
//
//
//            PokeNewDrop.INSTANCE.save(dropData, enumSpecies);
//            sender.sendMessage("§F添加完成,已保存数据.");
//            return true;
//        }
//
//
//        if (args.length == 2 && args[0].equalsIgnoreCase("type")) {
//
//            try {
//                KillPokemonEnum.valueOf(args[1]);
//            } catch (IllegalArgumentException e) {
//                sender.sendMessage("§ctype 类型仅允许设置为: ALL NORMAL BOSS");
//                sender.sendMessage("§ctype 类型依次代表: ALL(野生精灵和BOSS) NORMAL(野生精灵) BOSS(BOSS精灵)");
//                return true;
//            }
//
//            dropData.setType(KillPokemonEnum.valueOf(args[1]));
//
//            PokeNewDrop.INSTANCE.save(dropData, enumSpecies);
//
//            sender.sendMessage("§f现在," + enumSpecies.name() + "的掉落配置判断击杀类型已被设置为: " + args[1]);
//
//
//            return true;
//        }
//
//        if (args.length == 1 && args[0].equalsIgnoreCase("clear")) {
//
//
//            dropData.setClear(!dropData.isClear());
//
//            PokeNewDrop.INSTANCE.save(dropData, enumSpecies);
//
//            sender.sendMessage("§f现在," + enumSpecies.name() + "的掉落配置判断是否清理原本要掉落的物品被设置为: " + dropData.isClear());
//            sender.sendMessage("§ftrue 代表 清理 | false 代表 不清理");
//
//
//            return true;
//        }


        return false;
    }
}
