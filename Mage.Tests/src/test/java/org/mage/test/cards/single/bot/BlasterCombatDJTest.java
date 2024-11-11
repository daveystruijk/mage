package org.mage.test.cards.single.bot;

import mage.abilities.keyword.TrampleAbility;
import mage.constants.PhaseStep;
import mage.constants.Zone;
import mage.counters.CounterType;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * @author daveystruijk
 */
public class BlasterCombatDJTest extends CardTestPlayerBase {
    
    private static final String blaster = "Blaster, Combat DJ";
    private static final String arcboundWorker = "Arcbound Worker"; // 0/0 Modular 1
    private static final String ashnodsAltar = "Ashnod's Altar"; // Sacrifice a creature: Add {C}{C}

    @Test
    public void testModularMechanics() {
        addCard(Zone.BATTLEFIELD, playerA, blaster);
        addCard(Zone.BATTLEFIELD, playerA, ashnodsAltar);
        addCard(Zone.BATTLEFIELD, playerA, "Wastes");
        addCard(Zone.HAND, playerA, arcboundWorker);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, arcboundWorker);
        waitStackResolved(1, PhaseStep.PRECOMBAT_MAIN);
        execute();

        // Expect both modular triggers
        assertCounterCount(playerA, arcboundWorker, CounterType.P1P1, 2);

        // Sacrifice arcbound worker

        // Expect 
    }
}
