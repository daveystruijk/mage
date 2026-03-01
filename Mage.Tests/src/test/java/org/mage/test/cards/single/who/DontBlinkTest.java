package org.mage.test.cards.single.who;

import mage.constants.PhaseStep;
import mage.constants.Zone;
import org.junit.Test;
import org.mage.test.player.TestPlayer;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * @author daveystruijk
 */
public class DontBlinkTest extends CardTestPlayerBase {

    @Test
    public void test_CastFromExile() {
        addCard(Zone.HAND, playerA, "Don't Blink");
        addCard(Zone.BATTLEFIELD, playerA, "Island", 2);

        addCard(Zone.BATTLEFIELD, playerB, "Island", 4);
        addCard(Zone.EXILED, playerB, "Misthollow Griffin");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerB, "Misthollow Griffin");
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Don't Blink", TestPlayer.NO_TARGET, "Misthollow Griffin");

        setStopAt(1, PhaseStep.END_TURN);
        execute();

        assertPermanentCount(playerB, "Misthollow Griffin", 0);
        assertExileCount(playerB, "Misthollow Griffin", 0);
        assertLibraryCount(playerB, "Misthollow Griffin", 1);
    }

    @Test
    public void test_EntersFromExile() {
        addCard(Zone.HAND, playerA, "Don't Blink");
        addCard(Zone.BATTLEFIELD, playerA, "Island", 2);

        addCard(Zone.BATTLEFIELD, playerB, "Plains");
        addCard(Zone.BATTLEFIELD, playerB, "Grizzly Bears");
        addCard(Zone.HAND, playerB, "Cloudshift");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerB, "Cloudshift", "Grizzly Bears");
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Don't Blink", TestPlayer.NO_TARGET, "Cloudshift");

        setStopAt(1, PhaseStep.END_TURN);
        execute();

        assertPermanentCount(playerB, "Grizzly Bears", 0);
        assertExileCount(playerB, "Grizzly Bears", 0);
        assertLibraryCount(playerB, "Grizzly Bears", 1);
    }
}
