package lt.pokenewdrop.commands;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import lt.pokenewdrop.AD;
import lt.pokenewdrop.DropData;
import lt.pokenewdrop.KillPokemonEnum;
import lt.pokenewdrop.PokeNewDrop;
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

public class PncCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {


        if (sender.isOp() && args.length == 1 && args[0].equalsIgnoreCase("reload")){

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

            if (PokeNewDrop.INSTANCE.map.containsKey(enumSpecies)) {

                sender.sendMessage("§c此精灵的配置已存在,请删除此PokeNewDrop配置文件 [new] 目录下的 " + enumSpecies.name() + ".yml 以后输入/pnd reload 重载即可删除此精灵的旧配置");
                return true;

            }

            DropData dropData = new DropData(new ArrayList<>(), new ArrayList<>());
            PokeNewDrop.INSTANCE.map.put(enumSpecies, dropData);
            PokeNewDrop.INSTANCE.save(dropData, enumSpecies);
            sender.sendMessage("§f创建完成,你可以在服务端路径内的 plugins/PokeNewDrop/" + enumSpecies.name() +".yml 找到这只精灵的配置");
            return true;
        }


        if (!PokeNewDrop.INSTANCE.map.containsKey(enumSpecies)) {
            sender.sendMessage("§c此精灵的配置不存在,请先输入/pnd create    来为你精灵烂第一只精灵创建配置");
            return true;
        }
        DropData dropData = PokeNewDrop.INSTANCE.map.get(enumSpecies);


        if (args.length == 1 && args[0].equalsIgnoreCase("item")) {


            if (player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                sender.sendMessage("§c请确保主手的物品不为空才能添加为掉落物");
                return true;
            }

            String nbtItem = CraftItemStack.asNMSCopy(player.getInventory().getItemInMainHand()).save(new NBTTagCompound()).toString();

            dropData.getItems().add(nbtItem);

            PokeNewDrop.INSTANCE.save(dropData, enumSpecies);
            sender.sendMessage("§F添加完成,已保存数据.");
            return true;
        }

        if (args.length > 2 && args[0].equalsIgnoreCase("cmd")) {

            StringBuilder cmd = new StringBuilder();

            for (int i = 1; i < args.length; i++) {
                cmd.append(args[i]).append(" ");
            }

            dropData.getCommands().add(cmd.toString());

            PokeNewDrop.INSTANCE.save(dropData, enumSpecies);
            sender.sendMessage("§F添加完成,已保存数据.");
            sender.sendMessage("§f精灵 " + enumSpecies.name() + " 被击杀以后将执行如下命令:");
            dropData.getCommands().forEach(str -> {
                sender.sendMessage("/" + str);
            });
            sender.sendMessage("§f若要删除某命令请打开配置文件 plugins/PokeNewDrop/new/" + enumSpecies.name() +".yml 中 commands 下移除要删除的命令");


            return true;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("type")) {

            try {
                KillPokemonEnum.valueOf(args[1]);
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§ctype 类型仅允许设置为: ALL NORMAL BOSS");
                sender.sendMessage("§ctype 类型依次代表: ALL(野生精灵和BOSS) NORMAL(野生精灵) BOSS(BOSS精灵)");
                return true;
            }

            dropData.setType(KillPokemonEnum.valueOf(args[1]));

            PokeNewDrop.INSTANCE.save(dropData, enumSpecies);

            sender.sendMessage("§f现在," + enumSpecies.name() + "的掉落配置判断击杀类型已被设置为: " + args[1]);


            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("clear")) {


            dropData.setClear(!dropData.isClear());

            PokeNewDrop.INSTANCE.save(dropData, enumSpecies);

            sender.sendMessage("§f现在," + enumSpecies.name() + "的掉落配置判断是否清理原本要掉落的物品被设置为: " + dropData.isClear());
            sender.sendMessage("§ftrue 代表 清理 | false 代表 不清理");


            return true;
        }


        return false;
    }
}
