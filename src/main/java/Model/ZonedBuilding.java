package Model;

import Config.Enums.ZoneType;

public class ZonedBuilding extends BaseBuilding implements IUpgradable {
    private int currentResidents = 0;
    private int currentWorkers = 0;

    public ZonedBuilding(String buildingId, int gridX, int gridY, ZoneType zoneType) {
        super(buildingId, gridX, gridY, zoneType);
    }

    @Override
    public void onTick(long currentTick) {
        if (canUpgrade()) {
            upgradeLevel();
        }
    }

    @Override
    public boolean canUpgrade() {
        return false;
    }

    @Override
    public void upgradeLevel() {
        level++;
    }

    @Override
    public int getCurrentLevel() { return level; }

    public int getCurrentResidents() { return currentResidents; }
    public void setCurrentResidents(int currentResidents) { this.currentResidents = currentResidents; }

    public int getCurrentWorkers() { return currentWorkers; }
    public void setCurrentWorkers(int currentWorkers) { this.currentWorkers = currentWorkers; }
}