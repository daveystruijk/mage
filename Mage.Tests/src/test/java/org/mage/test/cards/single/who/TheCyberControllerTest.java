package org.mage.test.cards.single.who;

import mage.constants.PhaseStep;
import mage.constants.Zone;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * @author daveystruijk
 */
public class TheCyberControllerTest extends CardTestPlayerBase {

    @Test
    public void test_EntersBattlefield() {
        setStrictChooseMode(true);
        skipInitShuffling();

        addCard(Zone.HAND, playerA, "The Cyber-Controller");
        addCard(Zone.BATTLEFIELD, playerA, "Island", 2);
        addCard(Zone.BATTLEFIELD, playerA, "Swamp");
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 2);

        addCard(Zone.LIBRARY, playerB, "Silvercoat Lion@cyberman");
        addCard(Zone.LIBRARY, playerB, "Island@notCreature");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "The Cyber-Controller");
        setChoice(playerA, "X=2");

        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertPermanentCount(playerA, 7);
        assertGraveyardCount(playerB, "Island", 1);
        assertGraveyardCount(playerB, "Silvercoat Lion", 0);
    }
}
