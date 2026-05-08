package Model;

import Config.Enums.ZoneType;

public class ServiceBuilding extends BaseBuilding implements IAuraProvider, IUpgradable {

    private double currentAuraRadius;

    public ServiceBuilding(String buildingId, int gridX, int gridY) {
        super(buildingId, gridX, gridY, ZoneType.OTHER);
        if (getStats() != null) {
            this.currentAuraRadius = getStats().getServiceRadius();
        }
    }

    @Override
    public void onTick(long currentTick) {
        applyAuraToSurroundings();
    }

    @Override
    public void applyAuraToSurroundings() {
        double radius = getAuraRadius();
    }

    @Override
    public double getAuraRadius() {
        return currentAuraRadius;
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

    public double getCurrentAuraRadius() { return currentAuraRadius; }
    public void setCurrentAuraRadius(double currentAuraRadius) { this.currentAuraRadius = currentAuraRadius; }
}