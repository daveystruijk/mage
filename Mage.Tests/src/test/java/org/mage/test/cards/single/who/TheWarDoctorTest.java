package org.mage.test.cards.single.who;

import mage.constants.PhaseStep;
import mage.constants.Zone;
import mage.counters.CounterType;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * @author daveystruijk
 */
public class TheWarDoctorTest extends CardTestPlayerBase {

    @Test
    public void test_ExileAddsTimeCounterAndAttackExilesCreature() {
        addCard(Zone.BATTLEFIELD, playerA, "The War Doctor");
        addCard(Zone.BATTLEFIELD, playerA, "Plains");
        addCard(Zone.BATTLEFIELD, playerA, "Memnite");
        addCard(Zone.HAND, playerA, "Swords to Plowshares");

        addCard(Zone.BATTLEFIELD, playerB, "Llanowar Elves");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Swords to Plowshares", "Memnite");
        attack(1, playerA, "The War Doctor");
        addTarget(playerA, "Llanowar Elves");

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertCounterCount("The War Doctor", CounterType.TIME, 2);
        assertExileCount(playerA, "Memnite", 1);
        assertExileCount(playerB, "Llanowar Elves", 1);
        assertGraveyardCount(playerB, "Llanowar Elves", 0);
    }
}
