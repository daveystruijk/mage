package org.mage.test.cards.single.who;

import mage.constants.PhaseStep;
import mage.constants.Zone;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * @author daveystruijk
 */
public class TheForetoldSoldierTest extends CardTestPlayerBase {

    @Test
    public void test_DealingDamageForetells() {
        addCard(Zone.BATTLEFIELD, playerA, "The Foretold Soldier");
        addCard(Zone.BATTLEFIELD, playerA, "Forest", 2);

        addCard(Zone.BATTLEFIELD, playerB, "Grizzly Bears");

        attack(1, playerA, "The Foretold Soldier");
        block(1, playerB, "Grizzly Bears", "The Foretold Soldier");

        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertExileCount(playerA, "The Foretold Soldier", 1);
        assertPermanentCount(playerA, "The Foretold Soldier", 0);

        checkPlayableAbility("Foretell cost should be {1}{G}", 3, PhaseStep.PRECOMBAT_MAIN, playerA, "Foretell {1}{G}", true);
        checkPlayableAbility("Foretell {G}{G} should not be available", 3, PhaseStep.PRECOMBAT_MAIN, playerA, "Foretell {G}{G}", false);
        activateAbility(3, PhaseStep.PRECOMBAT_MAIN, playerA, "Foretell {1}{G}");

        setStopAt(3, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertExileCount(playerA, "The Foretold Soldier", 0);
        assertPermanentCount(playerA, "The Foretold Soldier", 1);
    }
}
