package org.mage.test.cards.single.who;

import mage.constants.PhaseStep;
import mage.constants.Zone;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * @author daveystruijk
 */
public class ThePandoricaTest extends CardTestPlayerBase {

    @Test
    public void test_PhaseOutWhileTappedAndPhaseInOnLeaveBattlefield() {
        addCard(Zone.BATTLEFIELD, playerA, "The Pandorica");
        addCard(Zone.BATTLEFIELD, playerA, "Plains", 2);

        addCard(Zone.BATTLEFIELD, playerB, "Grizzly Bears");
        addCard(Zone.HAND, playerB, "Disenchant");
        addCard(Zone.BATTLEFIELD, playerB, "Plains", 2);

        activateAbility(1, PhaseStep.PRECOMBAT_MAIN, playerA, "{1}{W}, {T}", "Grizzly Bears");
        castSpell(2, PhaseStep.PRECOMBAT_MAIN, playerB, "Disenchant", "The Pandorica");

        checkPermanentCount("1: Grizzly Bears phases out", 1, PhaseStep.POSTCOMBAT_MAIN, playerA, "Grizzly Bears", 0);
        checkPermanentCount("2: Grizzly Bears can't phase in while The Pandorica is tapped", 1, PhaseStep.POSTCOMBAT_MAIN, playerB, "Grizzly Bears", 0);
        checkPermanentCount("3: Grizzly Bears phases in when The Pandorica leaves the battlefield", 2, PhaseStep.POSTCOMBAT_MAIN, playerB, "Grizzly Bears", 1);

        setStopAt(2, PhaseStep.POSTCOMBAT_MAIN);
        execute();
    }
}
