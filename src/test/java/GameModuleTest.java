import org.junit.Assert;
import org.junit.Test;

public class GameModuleTest {

    @Test
    public void calculateTurn() {
        GameModule tester = new GameModule(0, 0);

        PlayerUpdate returnValue = new PlayerUpdate(0, "test",2, 11, "test landed on a dead tile and was moved back 3 tiles.");
        Assert.assertEquals("Test 1 failed.", returnValue,  tester.calculateTurn(2, 12, "test"));

        returnValue = new PlayerUpdate(0, "test", 5, 0, "test has won the game!");
        Assert.assertEquals("Test 2 failed", returnValue, tester.calculateTurn(5, 74, "test"));

        returnValue = new PlayerUpdate(0, "test", 5, 5, null);
        Assert.assertEquals("Test 3 failed", returnValue, tester.calculateTurn(5, 0, "test"));

        returnValue = new PlayerUpdate(0, "test", 4, 12, "test landed on a powerup tile and moved forward 3 tiles");
        Assert.assertEquals("Test 4 failed", returnValue, tester.calculateTurn(4, 5, "test"));
    }
}