package Model;

/**
 * Interface สำหรับตึกที่มีความสามารถในการผลิตทรัพยากร (น้ำ/ไฟฟ้า/อาหาร)
 * <p>Design Purpose: บังคับใช้ Contract ให้ตึกที่ผลิตทรัพยากรต้องมีเมธอดส่งออกน้ำไฟอาหารลงบน Grid ช่วยให้ GridDirtyFlag และระบบโครงข่ายคำนวณข้อมูลได้ง่ายขึ้น</p>
 */
public interface IResourceProducer {
    void produceResources(boolean[][] powerMap, boolean[][] waterMap);
    boolean isNetworkConnected();
}
