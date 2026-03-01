package org.mage.test.cards.single.who;

import mage.constants.PhaseStep;
import mage.constants.Zone;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * @author daveystruijk
 */
public class TheFiveDoctorsTest extends CardTestPlayerBase {

    @Test
    public void test_WithoutKicker() {
        addCard(Zone.HAND, playerA, "The Five Doctors");
        addCard(Zone.BATTLEFIELD, playerA, "Forest", 6);

        addCard(Zone.LIBRARY, playerA, "The Fourth Doctor");
        addCard(Zone.LIBRARY, playerA, "The Sixth Doctor");
        addCard(Zone.LIBRARY, playerA, "The Seventh Doctor");
        addCard(Zone.GRAVEYARD, playerA, "The Ninth Doctor");
        addCard(Zone.GRAVEYARD, playerA, "The Tenth Doctor");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "The Five Doctors");
        setChoice(playerA, true); // search library
        addTarget(playerA, "The Fourth Doctor^The Sixth Doctor^The Seventh Doctor");
        setChoice(playerA, true); // search graveyard
        addTarget(playerA, "The Ninth Doctor^The Tenth Doctor");

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertHandCount(playerA, "The Fourth Doctor", 1);
        assertHandCount(playerA, "The Sixth Doctor", 1);
        assertHandCount(playerA, "The Seventh Doctor", 1);
        assertHandCount(playerA, "The Ninth Doctor", 1);
        assertHandCount(playerA, "The Tenth Doctor", 1);
        assertGraveyardCount(playerA, "The Five Doctors", 1);
    }

    @Test
    public void test_WithKicker() {
        addCard(Zone.HAND, playerA, "The Five Doctors");
        addCard(Zone.BATTLEFIELD, playerA, "Forest", 11);

        addCard(Zone.LIBRARY, playerA, "The Fourth Doctor");
        addCard(Zone.LIBRARY, playerA, "The Sixth Doctor");
        addCard(Zone.GRAVEYARD, playerA, "The Ninth Doctor");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "The Five Doctors");
        setChoice(playerA, true); // pay kicker
        setChoice(playerA, true); // search library
        addTarget(playerA, "The Fourth Doctor^The Sixth Doctor");
        setChoice(playerA, true); // search graveyard
        addTarget(playerA, "The Ninth Doctor");

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertPermanentCount(playerA, "The Fourth Doctor", 1);
        assertPermanentCount(playerA, "The Sixth Doctor", 1);
        assertPermanentCount(playerA, "The Ninth Doctor", 1);
        assertHandCount(playerA, "The Fourth Doctor", 0);
        assertHandCount(playerA, "The Sixth Doctor", 0);
        assertHandCount(playerA, "The Ninth Doctor", 0);
        assertGraveyardCount(playerA, "The Five Doctors", 1);
    }
}
