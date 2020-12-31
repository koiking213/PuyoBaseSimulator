package com.example.puyo_base_simulator.ui.home;

import org.junit.Before;
import org.junit.Test;
import static com.google.common.truth.Truth.assertThat;


public class TsumoControllerTest {
    private TsumoController mTC = new TsumoController("rgrbyyrrgbrb", 0);

    @Before
    public void setUp() {

    }

    @Test
    public void pushPlacementOrder() {
    }

    @Test
    public void popPlacementOrder() {
    }

    @Test
    public void restorePlacement() {
    }

    @Test
    public void placementOrderToString() {
    }

    @Test
    public void stringToPlacementOrder() {
    }

    @Test
    public void incrementTsumo() {
    }

    @Test
    public void decrementTsumo() {
    }

    @Test
    public void makeTsumoInfo() {
    }

    @Test
    public void getPuyoColor() {
    }

    @Test
    public void getMainColor() {
        assertThat(mTC.getMainColor()).isEqualTo(PuyoColor.GREEN);
    }

    @Test
    public void getSubColor() {
        assertThat(mTC.getSubColor()).isEqualTo(PuyoColor.RED);
    }

    @Test
    public void moveCurrentLeft() {
        TsumoController tc = new TsumoController("rgrbyyrrgbrb", 0);
        tc.moveCurrentLeft();
        TsumoInfo info = tc.makeTsumoInfo();
        assertThat(info.currentColor[1]).isEqualTo(PuyoColor.RED);
        assertThat(info.currentColor[0]).isEqualTo(PuyoColor.GREEN);
        assertThat(info.nextColor[0][0]).isEqualTo(PuyoColor.RED);
        assertThat(info.nextColor[0][1]).isEqualTo(PuyoColor.BLUE);
        assertThat(info.nextColor[1][0]).isEqualTo(PuyoColor.YELLOW);
        assertThat(info.nextColor[1][1]).isEqualTo(PuyoColor.YELLOW);
        assertThat(info.currentMainPos[0]).isEqualTo(1);
        assertThat(info.currentMainPos[1]).isEqualTo(2);
        assertThat(info.currentSubPos[0]).isEqualTo(0);
        assertThat(info.currentSubPos[1]).isEqualTo(2);
        assertThat(info.currentCursorRotate).isEqualTo(Rotation.DEGREE0);
        tc.moveCurrentLeft();
        tc.moveCurrentLeft();
        info = tc.makeTsumoInfo();
        assertThat(info.currentMainPos[0]).isEqualTo(1);
        assertThat(info.currentMainPos[1]).isEqualTo(1);
        assertThat(info.currentSubPos[0]).isEqualTo(0);
        assertThat(info.currentSubPos[1]).isEqualTo(1);
        assertThat(info.currentCursorRotate).isEqualTo(Rotation.DEGREE0);
    }

    @Test
    public void moveCurrentRight() {
        TsumoController tc = new TsumoController("rgrbyyrrgbrb", 0);
        tc.moveCurrentRight();
        TsumoInfo info = tc.makeTsumoInfo();
        assertThat(info.currentColor[1]).isEqualTo(PuyoColor.RED);
        assertThat(info.currentColor[0]).isEqualTo(PuyoColor.GREEN);
        assertThat(info.nextColor[0][0]).isEqualTo(PuyoColor.RED);
        assertThat(info.nextColor[0][1]).isEqualTo(PuyoColor.BLUE);
        assertThat(info.nextColor[1][0]).isEqualTo(PuyoColor.YELLOW);
        assertThat(info.nextColor[1][1]).isEqualTo(PuyoColor.YELLOW);
        assertThat(info.currentMainPos[0]).isEqualTo(1);
        assertThat(info.currentMainPos[1]).isEqualTo(4);
        assertThat(info.currentSubPos[0]).isEqualTo(0);
        assertThat(info.currentSubPos[1]).isEqualTo(4);
        assertThat(info.currentCursorRotate).isEqualTo(Rotation.DEGREE0);
        tc.moveCurrentRight();
        tc.moveCurrentRight();
        tc.moveCurrentRight();
        info = tc.makeTsumoInfo();
        assertThat(info.currentMainPos[0]).isEqualTo(1);
        assertThat(info.currentMainPos[1]).isEqualTo(6);
        assertThat(info.currentSubPos[0]).isEqualTo(0);
        assertThat(info.currentSubPos[1]).isEqualTo(6);
        assertThat(info.currentCursorRotate).isEqualTo(Rotation.DEGREE0);
    }

    @Test
    public void rotateCurrentLeft() {
        TsumoController tc = new TsumoController("rgrbyyrrgbrb", 0);

        // その場で回転するテスト
        tc.rotateCurrentLeft();
        TsumoInfo info = tc.makeTsumoInfo();
        assertThat(info.currentColor[1]).isEqualTo(PuyoColor.RED);
        assertThat(info.currentColor[0]).isEqualTo(PuyoColor.GREEN);
        assertThat(info.nextColor[0][0]).isEqualTo(PuyoColor.RED);
        assertThat(info.nextColor[0][1]).isEqualTo(PuyoColor.BLUE);
        assertThat(info.nextColor[1][0]).isEqualTo(PuyoColor.YELLOW);
        assertThat(info.nextColor[1][1]).isEqualTo(PuyoColor.YELLOW);
        assertThat(info.currentMainPos[0]).isEqualTo(1);
        assertThat(info.currentMainPos[1]).isEqualTo(3);
        assertThat(info.currentSubPos[0]).isEqualTo(1);
        assertThat(info.currentSubPos[1]).isEqualTo(2);
        assertThat(info.currentCursorRotate).isEqualTo(Rotation.DEGREE270);
        tc.rotateCurrentLeft();
        info = tc.makeTsumoInfo();
        assertThat(info.currentMainPos[0]).isEqualTo(1);
        assertThat(info.currentMainPos[1]).isEqualTo(3);
        assertThat(info.currentSubPos[0]).isEqualTo(2);
        assertThat(info.currentSubPos[1]).isEqualTo(3);
        assertThat(info.currentCursorRotate).isEqualTo(Rotation.DEGREE180);
        tc.rotateCurrentLeft();
        info = tc.makeTsumoInfo();
        assertThat(info.currentMainPos[0]).isEqualTo(1);
        assertThat(info.currentMainPos[1]).isEqualTo(3);
        assertThat(info.currentSubPos[0]).isEqualTo(1);
        assertThat(info.currentSubPos[1]).isEqualTo(4);
        assertThat(info.currentCursorRotate).isEqualTo(Rotation.DEGREE90);
        tc.rotateCurrentLeft();
        info = tc.makeTsumoInfo();
        assertThat(info.currentMainPos[0]).isEqualTo(1);
        assertThat(info.currentMainPos[1]).isEqualTo(3);
        assertThat(info.currentSubPos[0]).isEqualTo(0);
        assertThat(info.currentSubPos[1]).isEqualTo(3);
        assertThat(info.currentCursorRotate).isEqualTo(Rotation.DEGREE0);

        // 左端で回転するテスト
        tc.moveCurrentLeft();
        tc.moveCurrentLeft();
        tc.rotateCurrentLeft();
        info = tc.makeTsumoInfo();
        assertThat(info.currentMainPos[0]).isEqualTo(1);
        assertThat(info.currentMainPos[1]).isEqualTo(2);
        assertThat(info.currentSubPos[0]).isEqualTo(1);
        assertThat(info.currentSubPos[1]).isEqualTo(1);
        assertThat(info.currentCursorRotate).isEqualTo(Rotation.DEGREE270);
        tc.rotateCurrentLeft();
        info = tc.makeTsumoInfo();
        assertThat(info.currentMainPos[0]).isEqualTo(1);
        assertThat(info.currentMainPos[1]).isEqualTo(2);
        assertThat(info.currentSubPos[0]).isEqualTo(2);
        assertThat(info.currentSubPos[1]).isEqualTo(2);
        assertThat(info.currentCursorRotate).isEqualTo(Rotation.DEGREE180);
        tc.rotateCurrentLeft();
        info = tc.makeTsumoInfo();
        assertThat(info.currentMainPos[0]).isEqualTo(1);
        assertThat(info.currentMainPos[1]).isEqualTo(2);
        assertThat(info.currentSubPos[0]).isEqualTo(1);
        assertThat(info.currentSubPos[1]).isEqualTo(3);
        assertThat(info.currentCursorRotate).isEqualTo(Rotation.DEGREE90);
        tc.rotateCurrentLeft();
        info = tc.makeTsumoInfo();
        assertThat(info.currentMainPos[0]).isEqualTo(1);
        assertThat(info.currentMainPos[1]).isEqualTo(2);
        assertThat(info.currentSubPos[0]).isEqualTo(0);
        assertThat(info.currentSubPos[1]).isEqualTo(2);
        assertThat(info.currentCursorRotate).isEqualTo(Rotation.DEGREE0);

        // 右端で回転するテスト
        tc.moveCurrentRight();
        tc.moveCurrentRight();
        tc.moveCurrentRight();
        tc.moveCurrentRight();
        tc.moveCurrentRight();
        tc.rotateCurrentLeft();
        info = tc.makeTsumoInfo();
        assertThat(info.currentMainPos[0]).isEqualTo(1);
        assertThat(info.currentMainPos[1]).isEqualTo(6);
        assertThat(info.currentSubPos[0]).isEqualTo(1);
        assertThat(info.currentSubPos[1]).isEqualTo(5);
        assertThat(info.currentCursorRotate).isEqualTo(Rotation.DEGREE270);
        tc.rotateCurrentLeft();
        info = tc.makeTsumoInfo();
        assertThat(info.currentMainPos[0]).isEqualTo(1);
        assertThat(info.currentMainPos[1]).isEqualTo(6);
        assertThat(info.currentSubPos[0]).isEqualTo(2);
        assertThat(info.currentSubPos[1]).isEqualTo(6);
        assertThat(info.currentCursorRotate).isEqualTo(Rotation.DEGREE180);
        tc.rotateCurrentLeft();
        info = tc.makeTsumoInfo();
        assertThat(info.currentMainPos[0]).isEqualTo(1);
        assertThat(info.currentMainPos[1]).isEqualTo(5);
        assertThat(info.currentSubPos[0]).isEqualTo(1);
        assertThat(info.currentSubPos[1]).isEqualTo(6);
        assertThat(info.currentCursorRotate).isEqualTo(Rotation.DEGREE90);
        tc.rotateCurrentLeft();
        info = tc.makeTsumoInfo();
        assertThat(info.currentMainPos[0]).isEqualTo(1);
        assertThat(info.currentMainPos[1]).isEqualTo(5);
        assertThat(info.currentSubPos[0]).isEqualTo(0);
        assertThat(info.currentSubPos[1]).isEqualTo(5);
        assertThat(info.currentCursorRotate).isEqualTo(Rotation.DEGREE0);
    }

    @Test
    public void rotateCurrentRight() {
        TsumoController tc = new TsumoController("rgrbyyrrgbrb", 0);

        // その場で回転するテスト
        tc.rotateCurrentRight();
        TsumoInfo info = tc.makeTsumoInfo();
        assertThat(info.currentColor[1]).isEqualTo(PuyoColor.RED);
        assertThat(info.currentColor[0]).isEqualTo(PuyoColor.GREEN);
        assertThat(info.nextColor[0][0]).isEqualTo(PuyoColor.RED);
        assertThat(info.nextColor[0][1]).isEqualTo(PuyoColor.BLUE);
        assertThat(info.nextColor[1][0]).isEqualTo(PuyoColor.YELLOW);
        assertThat(info.nextColor[1][1]).isEqualTo(PuyoColor.YELLOW);
        assertThat(info.currentMainPos[0]).isEqualTo(1);
        assertThat(info.currentMainPos[1]).isEqualTo(3);
        assertThat(info.currentSubPos[0]).isEqualTo(1);
        assertThat(info.currentSubPos[1]).isEqualTo(4);
        assertThat(info.currentCursorRotate).isEqualTo(Rotation.DEGREE90);
        tc.rotateCurrentRight();
        info = tc.makeTsumoInfo();
        assertThat(info.currentMainPos[0]).isEqualTo(1);
        assertThat(info.currentMainPos[1]).isEqualTo(3);
        assertThat(info.currentSubPos[0]).isEqualTo(2);
        assertThat(info.currentSubPos[1]).isEqualTo(3);
        assertThat(info.currentCursorRotate).isEqualTo(Rotation.DEGREE180);
        tc.rotateCurrentRight();
        info = tc.makeTsumoInfo();
        assertThat(info.currentMainPos[0]).isEqualTo(1);
        assertThat(info.currentMainPos[1]).isEqualTo(3);
        assertThat(info.currentSubPos[0]).isEqualTo(1);
        assertThat(info.currentSubPos[1]).isEqualTo(2);
        assertThat(info.currentCursorRotate).isEqualTo(Rotation.DEGREE270);
        tc.rotateCurrentRight();
        info = tc.makeTsumoInfo();
        assertThat(info.currentMainPos[0]).isEqualTo(1);
        assertThat(info.currentMainPos[1]).isEqualTo(3);
        assertThat(info.currentSubPos[0]).isEqualTo(0);
        assertThat(info.currentSubPos[1]).isEqualTo(3);
        assertThat(info.currentCursorRotate).isEqualTo(Rotation.DEGREE0);

        // 左端で回転するテスト
        tc.moveCurrentLeft();
        tc.moveCurrentLeft();
        tc.rotateCurrentRight();
        info = tc.makeTsumoInfo();
        assertThat(info.currentMainPos[0]).isEqualTo(1);
        assertThat(info.currentMainPos[1]).isEqualTo(1);
        assertThat(info.currentSubPos[0]).isEqualTo(1);
        assertThat(info.currentSubPos[1]).isEqualTo(2);
        assertThat(info.currentCursorRotate).isEqualTo(Rotation.DEGREE90);
        tc.rotateCurrentRight();
        info = tc.makeTsumoInfo();
        assertThat(info.currentMainPos[0]).isEqualTo(1);
        assertThat(info.currentMainPos[1]).isEqualTo(1);
        assertThat(info.currentSubPos[0]).isEqualTo(2);
        assertThat(info.currentSubPos[1]).isEqualTo(1);
        assertThat(info.currentCursorRotate).isEqualTo(Rotation.DEGREE180);
        tc.rotateCurrentRight();
        info = tc.makeTsumoInfo();
        assertThat(info.currentMainPos[0]).isEqualTo(1);
        assertThat(info.currentMainPos[1]).isEqualTo(2);
        assertThat(info.currentSubPos[0]).isEqualTo(1);
        assertThat(info.currentSubPos[1]).isEqualTo(1);
        assertThat(info.currentCursorRotate).isEqualTo(Rotation.DEGREE270);
        tc.rotateCurrentRight();
        info = tc.makeTsumoInfo();
        assertThat(info.currentMainPos[0]).isEqualTo(1);
        assertThat(info.currentMainPos[1]).isEqualTo(2);
        assertThat(info.currentSubPos[0]).isEqualTo(0);
        assertThat(info.currentSubPos[1]).isEqualTo(2);
        assertThat(info.currentCursorRotate).isEqualTo(Rotation.DEGREE0);

        // 右端で回転するテスト
        tc.moveCurrentRight();
        tc.moveCurrentRight();
        tc.moveCurrentRight();
        tc.moveCurrentRight();
        tc.moveCurrentRight();
        tc.rotateCurrentRight();
        info = tc.makeTsumoInfo();
        assertThat(info.currentMainPos[0]).isEqualTo(1);
        assertThat(info.currentMainPos[1]).isEqualTo(5);
        assertThat(info.currentSubPos[0]).isEqualTo(1);
        assertThat(info.currentSubPos[1]).isEqualTo(6);
        assertThat(info.currentCursorRotate).isEqualTo(Rotation.DEGREE90);
        tc.rotateCurrentRight();
        info = tc.makeTsumoInfo();
        assertThat(info.currentMainPos[0]).isEqualTo(1);
        assertThat(info.currentMainPos[1]).isEqualTo(5);
        assertThat(info.currentSubPos[0]).isEqualTo(2);
        assertThat(info.currentSubPos[1]).isEqualTo(5);
        assertThat(info.currentCursorRotate).isEqualTo(Rotation.DEGREE180);
        tc.rotateCurrentRight();
        info = tc.makeTsumoInfo();
        assertThat(info.currentMainPos[0]).isEqualTo(1);
        assertThat(info.currentMainPos[1]).isEqualTo(5);
        assertThat(info.currentSubPos[0]).isEqualTo(1);
        assertThat(info.currentSubPos[1]).isEqualTo(4);
        assertThat(info.currentCursorRotate).isEqualTo(Rotation.DEGREE270);
        tc.rotateCurrentRight();
        info = tc.makeTsumoInfo();
        assertThat(info.currentMainPos[0]).isEqualTo(1);
        assertThat(info.currentMainPos[1]).isEqualTo(5);
        assertThat(info.currentSubPos[0]).isEqualTo(0);
        assertThat(info.currentSubPos[1]).isEqualTo(5);
        assertThat(info.currentCursorRotate).isEqualTo(Rotation.DEGREE0);
    }
}