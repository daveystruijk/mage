package org.mage.test.cards.single.who;

import mage.constants.PhaseStep;
import mage.constants.Zone;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * @author daveystruijk
 */
public class TheEighthDoctorTest extends CardTestPlayerBase {

    @Test
    public void test_EntersBattlefieldMillsThree() {
        addCard(Zone.HAND, playerA, "The Eighth Doctor");
        addCard(Zone.BATTLEFIELD, playerA, "Plains", 4);
        addCard(Zone.BATTLEFIELD, playerA, "Island", 2);
        addCard(Zone.LIBRARY, playerA, "Forest", 3);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "The Eighth Doctor");

        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertPermanentCount(playerA, "The Eighth Doctor", 1);
        assertGraveyardCount(playerA, 3);
    }

    @Test
    public void test_HistoricPermanentFromGraveyardGainsExileReplacement() {
        addCard(Zone.BATTLEFIELD, playerA, "The Eighth Doctor");
        addCard(Zone.BATTLEFIELD, playerA, "Forest", 2);
        addCard(Zone.GRAVEYARD, playerA, "Mox Amber");
        addCard(Zone.HAND, playerA, "Naturalize");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Mox Amber");
        castSpell(1, PhaseStep.POSTCOMBAT_MAIN, playerA, "Naturalize", "Mox Amber");

        setStopAt(1, PhaseStep.END_TURN);
        execute();

        assertExileCount(playerA, "Mox Amber", 1);
        assertGraveyardCount(playerA, "Mox Amber", 0);
    }
}
