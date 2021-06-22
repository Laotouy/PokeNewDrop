package lt.pokenewdrop.listener;

import Br.API.GUI.Ex.UIManager;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import lt.pokenewdrop.DropData;
import lt.pokenewdrop.PokeNewDrop;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class ChatListener implements Listener {

    public static Map<UUID, Long> inputLimitTime = new HashMap<>();
    public static Map<UUID, Long> inputRandomLimitTime = new HashMap<>();

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {

        Player player = e.getPlayer();
        if (inputLimitTime.containsKey(player.getUniqueId())) {

            if (inputLimitTime.get(player.getUniqueId()) > System.currentTimeMillis()) {
                // 未超时
                inputLimitTime.remove(player.getUniqueId());
                e.setCancelled(true);
                EnumSpecies enumSpecies = PokeNewDrop.editPokemonType.get(player.getUniqueId());
                DropData dropData = PokeNewDrop.editPokemonDropData.get(player.getUniqueId());
                dropData.getCommands().add(e.getMessage());
                PokeNewDrop.INSTANCE.save(dropData, enumSpecies);
                UIManager.openUI(player, "PokeNewDropUI_DropCommandsList");
                player.sendMessage("§f添加完成: " + e.getMessage());


            } else {
                // 已超时
                inputLimitTime.remove(e.getPlayer().getUniqueId());
                player.sendMessage("§f上次激活了给精灵掉落配置中添加命令,但是已经超时了,若要添加命令请重新激活一次命令添加.");

            }

        }

        if (inputRandomLimitTime.containsKey(player.getUniqueId())){

            if (inputRandomLimitTime.get(player.getUniqueId()) > System.currentTimeMillis()) {
                // 未超时
                inputRandomLimitTime.remove(player.getUniqueId());
                e.setCancelled(true);

                if (!isInteger(e.getMessage()) || Integer.parseInt(e.getMessage()) <= 0 || Integer.parseInt(e.getMessage()) > 100){
                    player.sendMessage("§f输入错误 " + e.getMessage() +" 不是 1 - 100 之间的整数,请重新激活设置再次输入.");
                    return;
                }



                EnumSpecies enumSpecies = PokeNewDrop.editPokemonType.get(player.getUniqueId());
                DropData dropData = PokeNewDrop.editPokemonDropData.get(player.getUniqueId());
                dropData.setRandom(Integer.parseInt(e.getMessage()));
                PokeNewDrop.INSTANCE.save(dropData, enumSpecies);
                UIManager.openUI(player, "PokeNewDropUI_Admin");
                player.sendMessage("§e触发概率设置为: " + e.getMessage());


            } else {
                // 已超时
                inputRandomLimitTime.remove(e.getPlayer().getUniqueId());
                player.sendMessage("§f上次激活了给精灵掉落配置中添加命令,但是已经超时了,若要添加命令请重新激活一次命令添加.");

            }

        }

    }
    /*
     * 判断是否为整数
     * @param str 传入的字符串
     * @return 是整数返回true,否则返回false
     */


    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

}
