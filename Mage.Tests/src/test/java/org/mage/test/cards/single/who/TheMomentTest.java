package org.mage.test.cards.single.who;

import mage.constants.PhaseStep;
import mage.constants.Zone;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * @author daveystruijk
 */
public class TheMomentTest extends CardTestPlayerBase {

    @Test
    public void test_DestroyByTimeCounters() {
        addCard(Zone.BATTLEFIELD, playerA, "The Moment");
        addCard(Zone.BATTLEFIELD, playerA, "Memnite");
        addCard(Zone.BATTLEFIELD, playerA, "Grizzly Bears");
        addCard(Zone.BATTLEFIELD, playerA, "Island", 3);

        addCard(Zone.BATTLEFIELD, playerB, "Silvercoat Lion");
        addCard(Zone.BATTLEFIELD, playerB, "Island");

        activateAbility(3, PhaseStep.PRECOMBAT_MAIN, playerA, "{3}, {T}");

        setStopAt(3, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertGraveyardCount(playerA, "The Moment", 1);
        assertPermanentCount(playerA, "Memnite", 0);
        assertPermanentCount(playerA, "Grizzly Bears", 0);
        assertPermanentCount(playerB, "Silvercoat Lion", 0);
        assertPermanentCount(playerB, "Island", 1);
    }

    @Test
    public void test_PhaseOutUntilSourceLeavesBattlefield() {
        addCard(Zone.BATTLEFIELD, playerA, "The Moment");
        addCard(Zone.BATTLEFIELD, playerA, "Grizzly Bears");
        addCard(Zone.BATTLEFIELD, playerA, "Island", 2);

        addCard(Zone.HAND, playerB, "Disenchant");
        addCard(Zone.BATTLEFIELD, playerB, "Plains", 2);

        activateAbility(1, PhaseStep.PRECOMBAT_MAIN, playerA, "{2}, {T}");

        castSpell(2, PhaseStep.PRECOMBAT_MAIN, playerB, "Disenchant", "The Moment");

        checkPermanentCount("1: Grizzly Bears phases out", 1, PhaseStep.POSTCOMBAT_MAIN, playerA, "Grizzly Bears", 0);

        setStopAt(2, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertGraveyardCount(playerA, "The Moment", 1);
        assertPermanentCount(playerA, "Grizzly Bears", 1);
    }
}
