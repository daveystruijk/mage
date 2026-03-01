package org.mage.test.cards.single.who;

import mage.constants.CardType;
import mage.constants.EmptyNames;
import mage.constants.PhaseStep;
import mage.constants.SubType;
import mage.constants.Zone;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * @author daveystruijk
 */
public class CyberConversionTest extends CardTestPlayerBase {

    @Test
    public void test_Basic() {
        addCard(Zone.BATTLEFIELD, playerA, "Grizzly Bears");

        addCard(Zone.HAND, playerB, "Cyber Conversion");
        addCard(Zone.BATTLEFIELD, playerB, "Island", 2);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerB, "Cyber Conversion", "Grizzly Bears");

        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        String faceDownCreature = EmptyNames.FACE_DOWN_CREATURE.getTestCommand();
        assertPermanentCount(playerA, faceDownCreature, 1);
        assertPowerToughness(playerA, faceDownCreature, 2, 2);
        assertType(faceDownCreature, CardType.ARTIFACT, true);
        assertSubtype(faceDownCreature, SubType.CYBERMAN);
        assertNotSubtype(faceDownCreature, SubType.BEAR);
    }
}
