package org.mage.test.cards.single.who;

import mage.constants.PhaseStep;
import mage.constants.Zone;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * @author daveystruijk
 */
public class RassilonTheWarPresidentTest extends CardTestPlayerBase {

    @Test
    public void test_UpkeepTrigger() {
        skipInitShuffling();

        addCard(Zone.BATTLEFIELD, playerA, "Rassilon, the War President");
        addCard(Zone.LIBRARY, playerA, "Divination", 3);

        setStopAt(1, PhaseStep.PRECOMBAT_MAIN);
        execute();

        assertLife(playerA, 18);
        assertExileCount(playerA, "Divination", 1);
    }
}
