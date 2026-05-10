package Model;

import Logic.Core.AuraMapManager;

/**
 * Interface สำหรับตึกที่สามารถแผ่อิทธิพล (Aura) ลงบนแผนที่ได้
 * <p>Design Purpose: รองรับหลักการ Polymorphism เพื่อให้ระบบ AuraMapManager สามารถสั่งตึกให้แผ่ออร่าได้ทันที โดยไม่ต้องสนใจว่าตึกนั้นจะเป็นคลาสอะไร (ตำรวจ, สวนสาธารณะ หรือโรงไฟฟ้า)</p>
 */
public interface IAuraProvider {
    void applyAuraToSurroundings(AuraMapManager manager);
    double getAuraRadius();
}
