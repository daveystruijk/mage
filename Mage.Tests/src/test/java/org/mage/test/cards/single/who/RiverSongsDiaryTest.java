package org.mage.test.cards.single.who;

import mage.constants.PhaseStep;
import mage.constants.Zone;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * @author daveystruijk
 */
public class RiverSongsDiaryTest extends CardTestPlayerBase {

    @Test
    public void testExileSpellsCastFromHand() {
        addCard(Zone.BATTLEFIELD, playerA, "River Song's Diary");
        addCard(Zone.HAND, playerA, "Shock");
        addCard(Zone.BATTLEFIELD, playerA, "Mountain");

        addCard(Zone.BATTLEFIELD, playerB, "Silvercoat Lion");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Shock", "Silvercoat Lion");

        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertExileCount(playerA, "Shock", 1);
        assertGraveyardCount(playerA, "Shock", 0);
    }

    @Test
    public void testExileMultipleSpellsCastFromHand() {
        addCard(Zone.BATTLEFIELD, playerA, "River Song's Diary");
        addCard(Zone.BATTLEFIELD, playerA, "Island", 4);
        addCard(Zone.HAND, playerA, "Reach Through Mists", 4);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Reach Through Mists");
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Reach Through Mists");
        castSpell(1, PhaseStep.POSTCOMBAT_MAIN, playerA, "Reach Through Mists");
        castSpell(1, PhaseStep.POSTCOMBAT_MAIN, playerA, "Reach Through Mists");

        setStopAt(1, PhaseStep.END_TURN);
        execute();

        assertExileCount(playerA, "Reach Through Mists", 4);
        assertGraveyardCount(playerA, "Reach Through Mists", 0);
    }
}
