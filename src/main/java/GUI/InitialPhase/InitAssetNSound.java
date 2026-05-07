package GUI.InitialPhase;

import GUI.GUIServices.AssetManager;
import GUI.GUIServices.SoundManager;

public class InitAssetNSound {

    public static void Init() {
        AssetManager.getInstance().loadAllAssets();
        SoundManager.getInstance().playBGM("Background 1.mp3");
    }
}
