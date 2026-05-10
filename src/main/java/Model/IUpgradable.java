package Model;

/**
 * Interface สำหรับอาคารที่สามารถพัฒนาเลเวลได้ (Level Up/Down)
 * <p>Design Purpose: แยกพฤติกรรมการเติบโตออกมาเป็น Interface ทำให้เราสามารถเลือกหยิบความสามารถนี้ไปแปะให้ตึกประเภทไหนก็ได้ในอนาคต โดยไม่กระทบโครงสร้างหลัก</p>
 */
public interface IUpgradable {
    boolean canUpgrade();
    void upgradeLevel();
    int getCurrentLevel();
}
