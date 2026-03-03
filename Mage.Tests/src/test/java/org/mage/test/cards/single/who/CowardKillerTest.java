package org.mage.test.cards.single.who;

import mage.constants.PhaseStep;
import mage.constants.Zone;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * @author daveystruijk
 */
public class CowardKillerTest extends CardTestPlayerBase {

    @Test
    public void test_KillerDamagesEachCreatureThatSharesType() {
        addCard(Zone.BATTLEFIELD, playerA, "Balduvian Bears");
        addCard(Zone.BATTLEFIELD, playerA, "Runeclaw Bear");
        addCard(Zone.BATTLEFIELD, playerA, "Silvercoat Lion");

        addCard(Zone.HAND, playerB, "Coward // Killer");
        addCard(Zone.BATTLEFIELD, playerB, "Mountain", 4);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerB, "Killer", "Balduvian Bears");

        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertPermanentCount(playerA, "Balduvian Bears", 0);
        assertPermanentCount(playerA, "Runeclaw Bear", 0);
        assertPermanentCount(playerA, "Silvercoat Lion", 1);
    }
}
