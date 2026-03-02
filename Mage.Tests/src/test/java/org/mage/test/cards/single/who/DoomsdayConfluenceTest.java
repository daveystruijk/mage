package org.mage.test.cards.single.who;

import mage.constants.PhaseStep;
import mage.constants.Zone;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * @author daveystruijk
 */
public class DoomsdayConfluenceTest extends CardTestPlayerBase {

    @Test
    public void test_ChooseThreeModesWithDuplicates() {
        addCard(Zone.HAND, playerA, "Doomsday Confluence");
        addCard(Zone.BATTLEFIELD, playerA, "Swamp", 7);

        setChoice(playerA, "X=3");
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Doomsday Confluence");
        setChoice(playerA, "Create a 3/3 black Dalek artifact creature token with menace");
        setChoice(playerA, "Create a 3/3 black Dalek artifact creature token with menace");
        setChoice(playerA, "Create a 3/3 black Dalek artifact creature token with menace");

        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertPermanentCount(playerA, "Dalek Token", 3);
    }
}
