package org.mage.test.cards.single.who;

import mage.constants.PhaseStep;
import mage.constants.Zone;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * @author daveystruijk
 */
public class DeathInHeavenTest extends CardTestPlayerBase {

    @Test
    public void test_Chapters() {
        skipInitShuffling();

        addCard(Zone.HAND, playerA, "Death in Heaven");
        addCard(Zone.BATTLEFIELD, playerA, "Swamp", 4);

        addCard(Zone.GRAVEYARD, playerB, "Silvercoat Lion@graveCreature");
        addCard(Zone.GRAVEYARD, playerB, "Island@graveLand");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Death in Heaven");
        addTarget(playerA, playerB);
        addTarget(playerA, playerB);

        setStopAt(5, PhaseStep.PRECOMBAT_MAIN);
        execute();

        assertExileCount(playerB, "Silvercoat Lion", 0);
        assertGraveyardCount(playerB, "Silvercoat Lion", 0);

        assertGraveyardCount(playerB, "Island", 0);
        assertExileCount(playerB, "Island", 1);
    }
}
