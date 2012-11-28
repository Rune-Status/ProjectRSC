package org.darkquest.config;

import org.darkquest.gs.model.Bank;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public final class Constants {

    public static final class GameServer {

        /**
         * the servers name
         */
        public static String SERVER_NAME = "Project RSC";

        /**
         * whether or not this is a members world
         */
        public static boolean MEMBER_WORLD = false;

        /**
         * this worlds 'number'
         */
        public static int WORLD_NUMBER = 0;
		
		
        /**
         * the client version needed for login
         */
        public static int CLIENT_VERSION = 0;

        /**
         * the maximum allowed players to connect
         */
        public static int MAX_PLAYERS = 2000;

        /**
         * the ip address the server is hosted on
         */
        public static String SERVER_IP = "localhost";

        /**
         * the port the server is hosted on
         */
        public static int SERVER_PORT = 43595;

        /**
         * where the server is hosted (i.e. USA, Holland, etc.)
         */
        public static String SERVER_LOCATION = "";

        /**
         * the mysql database host
         */
        public static String MYSQL_HOST = "localhost";

        /**
         * the mysql database name
         */
        public static String MYSQL_DB = "projectrsc";

        /**
         * the mysql username
         */
        public static String MYSQL_USER = "root";

        /**
         * the mysql password
         */
        public static String MYSQL_PASS = "";

        /**
         *  mysql prefix
         */
        public static String MYSQL_TABLE_PREFIX = "prsc_";

        /**
         * the ip to connect to the login server
         */
        public static String LOGIN_SERVER_IP = "localhost";

        /**
         * the port to connect to the login server
         */
        public static int LOGIN_SERVER_PORT = 34528;

        /**
         * the normal experience rate
         */
        public static double EXP_RATE = 15.0;

        /**
         * experience rate for members
         */
        public static double MEMBERS_EXP_RATE = 15.0;
        /**
         * enable/disable batch events
         */
        public static boolean BATCH_EVENTS = false;

        /**
         * A message players will receive upon login
         */
        public static String MOTD = "Welcome to " + SERVER_NAME + "!";

        /**
         * where the server will look for other configuration files
         */
        public static String CONFIG_DIR = "../" + File.separator + "conf" + File.separator + "server";
        //public static String CONFIG_DIR = "conf" + File.separator + "server"; // GORF ONLY
        
        /**
         * Ban location
         */
        public static String BAN_LOCATION = "../" + File.separator + "ban.sh";
        
        /**
         * Script location
         */
        public static String SCRIPTS_DIR = "../" + File.separator + "scripts";
        
        /**
         * Connection threshold before blacklisting
         */
        
        public static final int MAX_THRESHOLD = 300;
        
        /**
         * when the server was started
         */
        public static long START_TIME = 0L;

        /**
         * Strikes, Bolts & Blast Spells.
         * <p/>
         * Remember, 30+ Magic damage gives you +1 damage, so these damages are
         * -1 the absolute max. Level Requirement, Max Damage
         */
        public static final int[][] SPELLS = {{1, 1}, {4, 2}, {9, 2}, {13, 3}, {17, 3}, {23, 4}, {29, 4}, {35, 5}, {41, 5}, {47, 6}, {53, 6}, {59, 7}, {62, 8}, {65, 9}, {70, 10}, {75, 11}};

        /**
         * ID's of all Undead-type of NPC's. (Used for crumble undead & sounds)
         */
        public static final int[] UNDEAD_NPCS = {15, 53, 80, 178, 664, 41, 52, 68, 180, 214, 319, 40, 45, 46, 50, 179, 195};

        /**
         * ID's of all ARMOR type NPC's. (Used for armor hitting sounds)
         */
        public static final int[] ARMOR_NPCS = {66, 102, 189, 277, 322, 401324, 323, 632, 633};

        /**
         * Maximum hit for Crumble Undead (Magic) spell. (Against undead)
         */
        public static final int CRUMBLE_UNDEAD_MAX = 12;

        /**
         * These NPCs are NPCs that are attackable, but do not run on low health
         * such as Guards etc.
         */
        public static final int[] NPCS_THAT_DONT_RETREAT = {65, 102, 100, 127, 258, 524, 526, 100, 321};

        public static void initConfig(String file) throws IOException {
            Properties props = new Properties();
            props.loadFromXML(new FileInputStream(file));

            SERVER_IP = props.getProperty("server_ip");
            SERVER_PORT = Integer.parseInt(props.getProperty("server_port"));
            SERVER_LOCATION = props.getProperty("server_location");
            MYSQL_USER = props.getProperty("mysql_user");
            MYSQL_PASS = props.getProperty("mysql_pass");
            MYSQL_HOST = props.getProperty("mysql_host");
            MYSQL_DB = props.getProperty("mysql_db");
            MYSQL_TABLE_PREFIX = props.getProperty("mysql_table_prefix");

            LOGIN_SERVER_IP = props.getProperty("ls_ip");
            LOGIN_SERVER_PORT = Integer.parseInt(props.getProperty("ls_port"));

            SERVER_NAME = props.getProperty("server_name");
            MEMBER_WORLD = Boolean.parseBoolean(props.getProperty("members"));
            WORLD_NUMBER = Integer.parseInt(props.getProperty("world_number"));
            CLIENT_VERSION = Integer.parseInt(props.getProperty("client_version"));
            MAX_PLAYERS = Integer.parseInt(props.getProperty("maxplayers"));

            EXP_RATE = Double.parseDouble(props.getProperty("exp_rate"));
            MEMBERS_EXP_RATE = Double.parseDouble(props.getProperty("members_exp_rate"));

            Bank.MAX_SIZE = Integer.parseInt(props.getProperty("max_" + (MEMBER_WORLD ? "members_" : "") + "bank_size"));
            
            BAN_LOCATION = props.getProperty("ban_location"); 
            SCRIPTS_DIR = props.getProperty("scripts_dir");
            
            START_TIME = System.currentTimeMillis();
            props.clear();
        }
    }
    
	public static final class Quests {
		public static final int BLACK_KNIGHTS_FORTRESS = 0;
		public static final int COOKS_ASSISTANT = 1;
		public static final int DEMON_SLAYER = 2;
		public static final int DORICS_QUEST = 3;
		public static final int THE_RESTLESS_GHOST = 4;
		public static final int GOBLIN_DIPLOMACY = 5;
		public static final int ERNEST_THE_CHICKEN = 6;
		public static final int IMP_CATCHER = 7;
		public static final int PIRATES_TREASURE = 8;
		public static final int PRINCE_ALI_RESCUE = 9;
		public static final int ROMEO_N_JULIET = 10;
		public static final int SHEEP_SHEARER = 11;
		public static final int SHIELD_OF_ARRAV = 12;
		public static final int THE_KNIGHTS_SWORD = 13;
		public static final int VAMPIRE_SLAYER = 14;
		public static final int WITCHS_POTION = 15;
		public static final int DRAGON_SLAYER = 16;
		public static final int WITCHS_HOUSE = 17;
		public static final int LOST_CITY = 18;
		public static final int HEROS_QUEST = 19;
		public static final int DRUIDIC_RITUAL = 20;
		public static final int MERLINS_CRYSTAL = 21;
		public static final int SCORPION_CATCHER = 22;
		public static final int FAMILY_CREST = 23;
		public static final int TRIBAL_TOTEM = 24;
		public static final int FISHING_CONTEST = 25;
		public static final int MONKS_FRIEND = 26;
		public static final int TEMPLE_OF_IKOV = 27;
		public static final int CLOCK_TOWER = 28;
		public static final int THE_HOLY_GRAIL = 29;
		public static final int FIGHT_ARENA = 30;
		public static final int TREE_GNOME_VILLAGE = 31;
		public static final int THE_HAZEEL_CULT = 32;
		public static final int SHEEP_HERDER = 33;
		public static final int PLAGUE_CITY = 34;
		public static final int SEA_SLUG = 35;
		public static final int WATERFALL_QUEST = 36;
		public static final int BIOHAZARD = 37;
		public static final int JUNGLE_POTION = 38;
		public static final int GRAND_TREE = 39;
		public static final int SHILO_VILLAGE = 40;
		public static final int UNDERGROUND_PASS = 41;
		public static final int OBSERVATORY_QUEST = 42;
		public static final int TOURIST_TRAP = 43;
		public static final int WATCHTOWER = 44;
		public static final int DWARF_CANNON = 45;
		public static final int MURDER_MYSTERY = 46;
		public static final int DIGSITE = 47;
		public static final int GERTRUDES_CAT = 48;
		public static final int LEGENDS_QUEST = 49;
	}
}
