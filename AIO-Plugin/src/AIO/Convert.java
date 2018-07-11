package AIO;

import com.sk89q.worldedit.BlockVector;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Convert {

    public static BlockVector LocationToBlockVector(Location location) {
        return new BlockVector(location.getX(), location.getY(), location.getZ());
    }

    public static Location BlockVectorToLocation(World world, BlockVector vector) {
        return new Location(world, vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }

    public static String LocationToStringNoPY(Location location) {
        return location.getWorld().getName() + ";" + location.getX() + ";" + location.getY() + ";" + location.getZ();
    }

    public static Location StringToLocationNoPY(String string) {
        return new Location(Bukkit.getServer().getWorld(string.split(";")[0]), Double.parseDouble(string.split(";")[1]), Double.parseDouble(string.split(";")[2]), Double.parseDouble(string.split(";")[3]));
    }

    public static String LocationToString(Location location) {
        return location.getWorld().getName() + ";" + location.getX() + ";" + location.getY() + ";" + location.getZ() + ";" + location.getYaw() + ";" + location.getPitch();
    }

    public static Location StringToLocation(String string) {
        return new Location(Bukkit.getServer().getWorld(string.split(";")[0]), Double.parseDouble(string.split(";")[1]), Double.parseDouble(string.split(";")[2]), Double.parseDouble(string.split(";")[3]), Float.parseFloat(string.split(";")[4]), Float.parseFloat(string.split(";")[5]));
    }

    public static GameMode IntToGamemode(int gamemode) {
        switch (gamemode) {
            case 0: return GameMode.SURVIVAL;
            case 1: return GameMode.CREATIVE;
            case 2: return GameMode.ADVENTURE;
            case 3: return GameMode.SPECTATOR;
        }
        return GameMode.SURVIVAL;
    }

    public static int GamemodeToInt(GameMode gamemode) {
        switch (gamemode) {
            case SURVIVAL: return 0;
            case CREATIVE: return 1;
            case ADVENTURE: return 2;
            case SPECTATOR: return 3;
        }
        return 0;
    }

    public static BarColor StringToColor(String string) {
        switch (string) {
            case "red": return BarColor.RED;
            case "pink": return BarColor.PINK;
            case "yellow": return BarColor.YELLOW;
            case "green": return BarColor.GREEN;
            case "blue": return BarColor.BLUE;
            case "purple": return BarColor.PURPLE;
            case "white": return BarColor.WHITE;
        }
        return BarColor.WHITE;
    }

    public static String ColorToString(BarColor color) {
        switch (color) {
            case WHITE: return "white";
            case YELLOW: return "yellow";
            case BLUE: return "blue";
            case GREEN: return "green";
            case RED: return "red";
            case PURPLE: return "purple";
            case PINK: return "pink";
        }
        return "white";
    }

    public static BarStyle StringToStyle(String string) {
        switch (string) {
            case "solid": return BarStyle.SOLID;
            case "segmented6": return BarStyle.SEGMENTED_6;
            case "segmented10": return BarStyle.SEGMENTED_10;
            case "segmented12": return BarStyle.SEGMENTED_12;
            case "segmented20": return BarStyle.SEGMENTED_20;
        }
        return BarStyle.SEGMENTED_20;
    }

    public static String StyleToString(BarStyle style) {
        switch (style) {
            case SOLID: return "solid";
            case SEGMENTED_6: return "segmented6";
            case SEGMENTED_10: return "segmented10";
            case SEGMENTED_12: return "segmented12";
            case SEGMENTED_20: return "segmented20";
        }
        return "segmented20";
    }

    public static String DateToString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }

    public static int BooleanToInt(boolean value) {
        if (value) {
            return 1;
        } else {
            return 0;
        }
    }

    public static DyeColor ByteToDye(byte color) {
        switch (color) {
            case 0: return DyeColor.BLACK;
            case 1: return DyeColor.RED;
            case 2: return DyeColor.GREEN;
            case 3: return DyeColor.BROWN;
            case 4: return DyeColor.BLUE;
            case 5: return DyeColor.PURPLE;
            case 6: return DyeColor.CYAN;
            case 7: return DyeColor.SILVER;
            case 8: return DyeColor.GRAY;
            case 9: return DyeColor.PINK;
            case 10: return DyeColor.LIME;
            case 11: return DyeColor.YELLOW;
            case 12: return DyeColor.LIGHT_BLUE;
            case 13: return DyeColor.MAGENTA;
            case 14: return DyeColor.ORANGE;
            case 15: return DyeColor.WHITE;
        }
        return DyeColor.BLACK;
    }

    public static DyeColor ByteToWool(byte color) {
        return ByteToDye((byte)(15 - color));
    }

    public static byte DyeToByte(DyeColor color) {
        switch (color) {
            case BLACK: return 0;
            case RED: return 1;
            case GREEN: return 2;
            case BROWN: return 3;
            case BLUE: return 4;
            case PURPLE: return 5;
            case CYAN: return 6;
            case SILVER: return 7;
            case GRAY: return 8;
            case PINK: return 9;
            case LIME: return 10;
            case YELLOW: return 11;
            case LIGHT_BLUE: return 12;
            case MAGENTA: return 13;
            case ORANGE: return 14;
            case WHITE: return 15;
        }
        return 0;
    }

    public static byte DyeToWool(byte dye) {
        return (byte)(15 - dye);
    }

    public static byte WoolToDye(byte wool) {
        return (byte)(15 - wool);
    }

    public static DyeColor StringtoDye(String string) {
        switch (string.replace("-", "_").toLowerCase()) {
            case "black":
                return DyeColor.BLACK;
            case "red":
                return DyeColor.RED;
            case "green":
                return DyeColor.GREEN;
            case "brown":
                return DyeColor.BROWN;
            case "blue":
                return DyeColor.BLUE;
            case "purple":
                return DyeColor.PURPLE;
            case "cyan":
                return DyeColor.CYAN;
            case "silver":
            case "light_gray":
            case "light_grey":
                return DyeColor.SILVER;
            case "gray":
            case "grey":
                return DyeColor.GRAY;
            case "pink":
                return DyeColor.PINK;
            case "lime":
            case "light_green":
                return DyeColor.LIME;
            case "yellow":
                return DyeColor.YELLOW;
            case "light_blue":
                return DyeColor.LIGHT_BLUE;
            case "magenta":
                return DyeColor.MAGENTA;
            case "orange":
                return DyeColor.ORANGE;
            case "white":
                return DyeColor.WHITE;
        }
        return DyeColor.BLACK;
    }
}
