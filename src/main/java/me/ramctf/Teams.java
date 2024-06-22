package me.ramctf;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;



public class Teams implements Listener{

    //variables for teams and scoreboard
    static Team red;
    static Team blue;
    static Scoreboard board;
    static Objective obj;
    static Team BlueInfo;
    static Team RedInfo;
    static Team GameInfo;
    static Team RedFlagInfo;
    static Team BlueFlagInfo;

    public static void initializeTeams(){

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        board = manager.getNewScoreboard();
        obj = board.registerNewObjective("MainScoreboard","dummy", ChatColor.BOLD + "------RAM-CTF------");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        GameInfo = board.registerNewTeam("GameInfo");
        GameInfo.addEntry(ChatColor.AQUA.toString());
        GameInfo.setPrefix(ChatColor.GREEN + "");
        GameInfo.setSuffix("");
        obj.getScore(ChatColor.AQUA.toString()).setScore(2);

        BlueInfo = board.registerNewTeam("BlueInfo");
        BlueInfo.addEntry(ChatColor.WHITE.toString());
        BlueInfo.setPrefix(ChatColor.BLUE + "Blue Score: ");
        BlueInfo.setSuffix("");
        obj.getScore(ChatColor.WHITE.toString()).setScore(1);

        RedInfo = board.registerNewTeam("RedInfo");
        RedInfo.addEntry(ChatColor.BLACK.toString());
        RedInfo.setPrefix(ChatColor.RED + "Red Score: ");
        RedInfo.setSuffix("");
        obj.getScore(ChatColor.BLACK.toString()).setScore(0);

        blue = board.registerNewTeam("Blue");
        blue.setPrefix(ChatColor.BLUE + "[BLUE] " + ChatColor.WHITE);
        blue.setAllowFriendlyFire(false);
        blue.setCanSeeFriendlyInvisibles(true);

        red = board.registerNewTeam("Red");
        red.setPrefix(ChatColor.RED + "[RED] " + ChatColor.WHITE);
        red.setAllowFriendlyFire(false);
        red.setCanSeeFriendlyInvisibles(true);

    }   

    public static void broadcastMessage(String message, String team){
        for(Player p : Bukkit.getOnlinePlayers()){
            if(team.equalsIgnoreCase("Red")){
                if(red.hasEntry(p.getName())){
                    p.sendTitle(message, "", 10, 70, 20);          
                }
            } else if(team.equalsIgnoreCase("Blue")){
                if(blue.hasEntry(p.getName())){
                    p.sendTitle(message, "", 10, 70, 20);
                }
            }
        }
    }

    public static void broadcastMessage(String message, String team, Player ignorePlayer){
        for(Player p : Bukkit.getOnlinePlayers()){
            if(!p.equals(ignorePlayer)){
                if(team.equalsIgnoreCase("Red")){
                    if(red.hasEntry(p.getName())){
                        p.sendTitle(message, "", 10, 70, 20);          
                    }
                } else if(team.equalsIgnoreCase("Blue")){
                    if(blue.hasEntry(p.getName())){
                        p.sendTitle(message, "", 10, 70, 20);
                    }
                }
            }
        }
    }

    public static void playVictory(String team){
        for(Player p : Bukkit.getOnlinePlayers()){
            if(team.equalsIgnoreCase("Red")){
                if(red.hasEntry(p.getName())){
                    new BukkitRunnable() {
                        int count = 0;

                        @Override
                        public void run() {
                            if (count < 3) {
                                p.playSound(p.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1, 1);
                                count++;
                            } else {
                                this.cancel();
                            }
                        }
                    }.runTaskTimer(JavaPlugin.getPlugin(RamCTF.class), 0, 5);
                }
            } else if(team.equalsIgnoreCase("Blue")){
                if(blue.hasEntry(p.getName())){
                    new BukkitRunnable() {
                        int count = 0;

                        @Override
                        public void run() {
                            if (count < 3) {
                                p.playSound(p.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1, 1);
                                count++;
                            } else {
                                this.cancel();
                            }
                        }
                    }.runTaskTimer(JavaPlugin.getPlugin(RamCTF.class), 0, 5);
                }
            }
        }
    }
    
    public static void playAlarm(String team){
        for(Player p : Bukkit.getOnlinePlayers()){
            if(Teams.getTeam(p).equalsIgnoreCase(team)){

                new BukkitRunnable() {
                    int count = 3;

                    @Override
                    public void run() {
                        if (count > 0) {
                            p.playSound(p.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_SNARE, 1, count);
                            count--;
                        } else {
                            this.cancel();
                        }
                    }
                }.runTaskTimer(JavaPlugin.getPlugin(RamCTF.class), 0, 5);           
            } 
        }
    }

    public static void updateScoreboard(){
        for(Player p : Bukkit.getOnlinePlayers()){
            RedInfo.setSuffix(ChatColor.GOLD + Integer.toString(GameProperties.redTeamScore()));
            BlueInfo.setSuffix(ChatColor.GOLD + Integer.toString(GameProperties.blueTeamScore()));
            if(GameProperties.pregameStarted()){
                GameInfo.setSuffix(ChatColor.GREEN + "Game Starting in " + ChatColor.GOLD + GameProperties.minutesUntilGameStart() + " minutes");
            } else if(GameProperties.gameStarted()){
                GameInfo.setSuffix(ChatColor.GREEN + "Game Ending in " + ChatColor.GOLD + GameProperties.minutesUntilGameEnd() + " minutes");

            } else {
                GameInfo.setSuffix(ChatColor.GREEN + "Game Setup In Progress");
            }
            p.setScoreboard(board);
        }
    }

    public static boolean checkAllPlayerTeams(){
        for(Player online : Bukkit.getOnlinePlayers()){
            if(!red.hasEntry(online.getName()) && !blue.hasEntry(online.getName())){
                return false;
            }
        }
        return true;
    }

    public static void addPlayerBlueTeam(Player p){
        blue.addEntry(p.getName());

        ItemStack blueLeatherChestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta blueLeatherChestplateMeta = (LeatherArmorMeta) blueLeatherChestplate.getItemMeta();
        blueLeatherChestplateMeta.setDisplayName(ChatColor.BLUE + "Blue Team");
        blueLeatherChestplateMeta.setColor(Color.BLUE);
        blueLeatherChestplate.setItemMeta(blueLeatherChestplateMeta);

        PlayerInventory inventory = p.getInventory();
        ItemStack[] armor = inventory.getArmorContents();
        armor[2] = blueLeatherChestplate;
        p.getInventory().setArmorContents(armor);


        if(red.hasEntry(p.getName())){
            red.removeEntry(p.getName());
        }

        if(checkAllPlayerTeams()){
            GameProperties.setTeamSetupCompleted(true);
        }
        
    }

    public static void addPlayerRedTeam(Player p){
        red.addEntry(p.getName());

        ItemStack redLeatherChestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta redLeatherChestplateMeta = (LeatherArmorMeta) redLeatherChestplate.getItemMeta();
        redLeatherChestplateMeta.setDisplayName(ChatColor.RED + "Red Team");
        redLeatherChestplateMeta.setColor(Color.RED);
        redLeatherChestplate.setItemMeta(redLeatherChestplateMeta);

        PlayerInventory inventory = p.getInventory();
        ItemStack[] armor = inventory.getArmorContents();
        armor[2] = redLeatherChestplate;
        p.getInventory().setArmorContents(armor);

        if(blue.hasEntry(p.getName())){
            blue.removeEntry(p.getName());
        }

        if(checkAllPlayerTeams()){
            GameProperties.setTeamSetupCompleted(true);
        }
        
    }

    public static String getTeam(Player p){
        if(red.hasEntry(p.getName())){
            return "Red";
        } else if(blue.hasEntry(p.getName())){
            return "Blue";
        } else {
            return "None";
        }
    }
}


