package AIO;

import org.bukkit.Location;

import java.util.Date;
import java.util.UUID;

public class PrisonCell {

    private String owner;
    private UUID uuid;
    private Date ownedUntil;
    private Location coordinate1;
    private Location coordinate2;
    private int orientation;
    private int size;
    private String name;

    PrisonCell(String owner, UUID uuid, Date ownedUntil, Location coordinate1, Location coordinate2, int orientation, int size, String name) {
        this.owner = owner;
        this.uuid = uuid;
        this.ownedUntil = ownedUntil;
        this.coordinate1 = coordinate1;
        this.coordinate2 = coordinate2;
        this.orientation = orientation;
        this.size = size;
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Date getOwnedUntil() {
        return ownedUntil;
    }

    public Location getCoordinate1() {
        return coordinate1.clone();
    }

    public Location getCoordinate2() {
        return coordinate2.clone();
    }

    public int getOrientation() {
        return orientation;
    }

    public int getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public Location getEntranceCorner() {
        switch (getOrientation()) {
            case 0: return new Location(getCoordinate1().getWorld(), getCoordinate1().getX(), getCoordinate2().getY(), getCoordinate2().getZ());
            case 1: return new Location(getCoordinate1().getWorld(), getCoordinate1().getX(), getCoordinate2().getY(), getCoordinate1().getZ());
            case 2: return new Location(getCoordinate1().getWorld(), getCoordinate2().getX(), getCoordinate2().getY(), getCoordinate1().getZ());
            case 3: return new Location(getCoordinate1().getWorld(), getCoordinate2().getX(), getCoordinate2().getY(), getCoordinate2().getZ());
            case 4: return new Location(getCoordinate1().getWorld(), getCoordinate1().getX(), getCoordinate2().getY(), getCoordinate1().getZ());
            case 5: return new Location(getCoordinate1().getWorld(), getCoordinate2().getX(), getCoordinate2().getY(), getCoordinate1().getZ());
            case 6: return new Location(getCoordinate1().getWorld(), getCoordinate2().getX(), getCoordinate2().getY(), getCoordinate2().getZ());
            case 7: return new Location(getCoordinate1().getWorld(), getCoordinate1().getX(), getCoordinate2().getY(), getCoordinate2().getZ());
        }
        return null;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setOwnedUntil(Date ownedUntil) {
        this.ownedUntil = ownedUntil;
    }

    public void setCoordinate1(Location coordinate1) {
        this.coordinate1 = coordinate1;
    }

    public void setCoordinate2(Location coordinate2) {
        this.coordinate2 = coordinate2;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setName(String name) {
        this.name = name;
    }
}
