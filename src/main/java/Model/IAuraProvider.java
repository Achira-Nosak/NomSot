package Model;

import Logic.Core.AuraMapManager;

public interface IAuraProvider {
    void applyAuraToSurroundings(AuraMapManager manager);
    double getAuraRadius();
}
