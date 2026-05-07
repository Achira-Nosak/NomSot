package GUI.GUIServices;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

public class SoundManager {
    private static SoundManager instance;
    private MediaPlayer bgmPlayer;
    private final String SOUND_PATH = "/Assets/Sounds/";

    private SoundManager() {}

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }


    public void playBGM(String fileName) {
        try {
            URL resource = getClass().getResource(SOUND_PATH + fileName);

            if (resource == null) {
                System.err.println("❌ หาไฟล์เพลงไม่เจอ: " + SOUND_PATH + fileName);
                return;
            }

            // ถ้ามีเพลงเก่าเล่นอยู่ ให้หยุดก่อน
            if (bgmPlayer != null) {
                bgmPlayer.stop();
            }

            Media media = new Media(resource.toString());
            bgmPlayer = new MediaPlayer(media);

            // ตั้งค่าให้เล่นวนลูปไปเรื่อยๆ (INDEFINITE)
            bgmPlayer.setCycleCount(MediaPlayer.INDEFINITE);

            // ปรับระดับเสียง (0.0 ถึง 1.0)
            bgmPlayer.setVolume(0.3);

            bgmPlayer.play();
            System.out.println("🔊 Playing BGM: " + fileName);

        } catch (Exception e) {
            System.err.println("❌ เกิดข้อผิดพลาดในการโหลดไฟล์เสียง: " + e.getMessage());
        }
    }

    // เอาไว้เรียกตอนปิดเกม หรือปุ่ม Pause
    public void stopBGM() {
        if (bgmPlayer != null) {
            bgmPlayer.stop();
        }
    }
}