package ch.realmtech.utilsUi;

import ch.realmtech.server.mod.utils.UiCreateNewWorldScreenUtils;
import ch.realmtech.server.registry.InvalideEvaluate;
import ch.realmtech.server.registry.Registry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TestUtilsUi {

    public static UiCreateNewWorldScreenUtils uiCreateNewWorldScreenUtils;

    @BeforeAll
    static void setup() throws InvalideEvaluate {
        uiCreateNewWorldScreenUtils = new UiCreateNewWorldScreenUtils();
        uiCreateNewWorldScreenUtils.evaluate(Registry.createRoot());
    }

    @Test
    void testSeedValideLong() {
        assertEquals(381378974, uiCreateNewWorldScreenUtils.parseTextFieldToSeed("381378974"));
        assertEquals(0, uiCreateNewWorldScreenUtils.parseTextFieldToSeed("0"));
        assertEquals(-312, uiCreateNewWorldScreenUtils.parseTextFieldToSeed("-312"));
        assertEquals(-381378974, uiCreateNewWorldScreenUtils.parseTextFieldToSeed("-381378974"));
    }

    @Test
    void testSeedString() {
        assertEquals(842, uiCreateNewWorldScreenUtils.parseTextFieldToSeed("hdsjkhjd"));
        assertEquals(404, uiCreateNewWorldScreenUtils.parseTextFieldToSeed("28fa21"));
        assertEquals(449, uiCreateNewWorldScreenUtils.parseTextFieldToSeed("-28fa21"));
        assertEquals(436, uiCreateNewWorldScreenUtils.parseTextFieldToSeed("28 fa21"));
        assertEquals(436, uiCreateNewWorldScreenUtils.parseTextFieldToSeed("28fa2 1"));
    }

    @Test
    void testSeedStringBlank() {
        // this test can fail, is random, but extremely unluckily to fail
        long testRandom = uiCreateNewWorldScreenUtils.parseTextFieldToSeed("");
        assertNotEquals(testRandom, uiCreateNewWorldScreenUtils.parseTextFieldToSeed(""));
    }

    @Test
    void testSeedStringNull() {
        // this test can fail, is random, but extremely unluckily to fail
        long testRandom = uiCreateNewWorldScreenUtils.parseTextFieldToSeed(null);
        assertNotEquals(testRandom, uiCreateNewWorldScreenUtils.parseTextFieldToSeed(null));
    }
}
