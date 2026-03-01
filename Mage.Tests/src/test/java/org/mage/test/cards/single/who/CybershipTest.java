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
public class CybershipTest extends CardTestPlayerBase {

    @Test
    public void test_DealsCombatDamageToAPlayer() {
        addCard(Zone.BATTLEFIELD, playerA, "Cybership");
        addCard(Zone.BATTLEFIELD, playerA, "Grizzly Bears@crewOne");
        addCard(Zone.BATTLEFIELD, playerA, "Runeclaw Bear@crewTwo");

        addCard(Zone.LIBRARY, playerB, "Island@cyberOne");
        addCard(Zone.LIBRARY, playerB, "Mountain@cyberTwo");

        activateAbility(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Crew 4");
        setChoice(playerA, "Grizzly Bears@crewOne");
        setChoice(playerA, "Runeclaw Bear@crewTwo");
        attack(1, playerA, "Cybership");

        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        String faceDownCreature = EmptyNames.FACE_DOWN_CREATURE.getTestCommand();
        assertPermanentCount(playerA, faceDownCreature, 2);
        assertPermanentCount(playerB, faceDownCreature, 0);
        assertLibraryCount(playerB, 0);
        assertLife(playerB, 12);

        assertPowerToughness(playerA, "Island@cyberOne", 2, 2);
        assertType("Island@cyberOne", CardType.ARTIFACT, true);
        assertType("Island@cyberOne", CardType.CREATURE, true);
        assertSubtype("Island@cyberOne", SubType.CYBERMAN);

        assertPowerToughness(playerA, "Mountain@cyberTwo", 2, 2);
        assertType("Mountain@cyberTwo", CardType.ARTIFACT, true);
        assertType("Mountain@cyberTwo", CardType.CREATURE, true);
        assertSubtype("Mountain@cyberTwo", SubType.CYBERMAN);
    }
}
