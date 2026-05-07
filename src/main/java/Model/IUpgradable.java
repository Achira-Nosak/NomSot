package Model;

public interface IUpgradable {
    boolean canUpgrade();
    void upgradeLevel();
    int getCurrentLevel();
}
