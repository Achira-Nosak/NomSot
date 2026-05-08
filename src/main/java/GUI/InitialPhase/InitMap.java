package GUI.InitialPhase;

import Logic.Core.GameMapManager;

public class InitMap {

    public static void Init() {
        GameMapManager.getInstance();
        GUI.GUIServices.TerrainMapManager.getInstance().initMap();
    }
}
