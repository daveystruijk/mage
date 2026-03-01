package org.mage.test.cards.single.who;

import mage.constants.PhaseStep;
import mage.constants.Zone;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * @author daveystruijk
 */
public class OsgoodOperationDoubleTest extends CardTestPlayerBase {

    @Test
    public void test_CopyAndParadox() {
        addCard(Zone.HAND, playerA, "Osgood, Operation Double");
        addCard(Zone.GRAVEYARD, playerA, "Deep Analysis");
        addCard(Zone.BATTLEFIELD, playerA, "Island", 6);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Osgood, Operation Double");

        activateAbility(1, PhaseStep.POSTCOMBAT_MAIN, playerA, "Flashback");
        addTarget(playerA, playerA);

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.END_TURN);
        execute();

        assertPermanentCount(playerA, "Osgood, Operation Double", 2);
        assertPermanentCount(playerA, "Clue Token", 2);
        assertExileCount(playerA, "Deep Analysis", 1);
    }
}
