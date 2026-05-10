package Logic.Core;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import Logic.Core.GridDirtyFlag;
import Logic.Core.GameMapManager;

class GridDirtyFlagTest {

    // เทสว่า Singleton ดึงมาใช้งานได้และเป็น Instance เดียวกันเสมอ
    @Test
    void testSingletonInstance() {
        GridDirtyFlag instance1 = GridDirtyFlag.getInstance();
        GridDirtyFlag instance2 = GridDirtyFlag.getInstance();
        assertNotNull(instance1);
        assertEquals(instance1, instance2);
    }

    // เทสว่าหลังจากสั่ง Update แล้ว Map ของน้ำและไฟต้องถูกสร้างขึ้นมา (ไม่เป็น null)
    @Test
    void testUpdateGeneratesMaps() {
        GridDirtyFlag grid = GridDirtyFlag.getInstance();

        // สั่งให้ Grid สกปรก แล้วเรียกอัปเดต
        grid.makeGridDirty();
        grid.updateIfGridDirty();

        assertNotNull(grid.getPowerMap());
        assertNotNull(grid.getWaterMap());
    }

    // เทสว่า Array ของน้ำและไฟที่ถูกสร้างขึ้น มีขนาดตรงกับขนาดของ GameMapManager จริงๆ
    @Test
    void testMapDimensionsMatchGameMap() {
        GridDirtyFlag grid = GridDirtyFlag.getInstance();
        int expectedSize = GameMapManager.getInstance().getMapSize();

        grid.makeGridDirty();
        grid.updateIfGridDirty();

        // เช็คความกว้างของ Array แกน X (แถวแรก)
        assertEquals(expectedSize, grid.getPowerMap().length);
        assertEquals(expectedSize, grid.getWaterMap().length);
    }
}
