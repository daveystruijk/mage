package org.mage.test.cards.single.unf;

import mage.abilities.keyword.FlyingAbility;
import mage.constants.PhaseStep;
import mage.constants.Zone;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * @author daveystruijk
 */
public class ExchangeOfWordsTest extends CardTestPlayerBase {

    private static final String exchange = "Exchange of Words";
    private static final String lion = "Silvercoat Lion";
    private static final String drake = "Wind Drake";
    private static final String elf = "Llanowar Elves";
    private static final String murder = "Murder";
    private static final String disenchant = "Disenchant";
    private static final String cloudshift = "Cloudshift";

    @Test
    public void testNoExchangeIfSourceLeavesBeforeTriggerResolves() {
        setStrictChooseMode(true);

        addCard(Zone.BATTLEFIELD, playerA, "Island", 3);
        addCard(Zone.BATTLEFIELD, playerB, "Plains", 2);
        addCard(Zone.BATTLEFIELD, playerA, lion);
        addCard(Zone.BATTLEFIELD, playerA, drake);
        addCard(Zone.HAND, playerA, exchange);
        addCard(Zone.HAND, playerB, disenchant);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, exchange);
        addTarget(playerA, lion);
        addTarget(playerA, drake);
        waitStackResolved(1, PhaseStep.PRECOMBAT_MAIN, 1);
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerB, disenchant, exchange);

        setStopAt(1, PhaseStep.END_TURN);
        execute();

        assertAbility(playerA, lion, FlyingAbility.getInstance(), false);
        assertAbility(playerA, drake, FlyingAbility.getInstance(), true);
    }

    @Test
    public void testNoExchangeIfTargetIsIllegal() {
        setStrictChooseMode(true);

        addCard(Zone.BATTLEFIELD, playerA, "Island", 3);
        addCard(Zone.BATTLEFIELD, playerB, "Swamp", 3);
        addCard(Zone.BATTLEFIELD, playerA, lion);
        addCard(Zone.BATTLEFIELD, playerA, drake);
        addCard(Zone.HAND, playerA, exchange);
        addCard(Zone.HAND, playerB, murder);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, exchange);
        addTarget(playerA, lion);
        addTarget(playerA, drake);
        waitStackResolved(1, PhaseStep.PRECOMBAT_MAIN, 1);
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerB, murder, drake);

        setStopAt(1, PhaseStep.END_TURN);
        execute();

        assertGraveyardCount(playerA, drake, 1);
        assertAbility(playerA, lion, FlyingAbility.getInstance(), false);
    }

    @Test
    public void testExchangePersistsIfOneCreatureLeaves() {
        setStrictChooseMode(true);

        addCard(Zone.BATTLEFIELD, playerA, "Island", 3);
        addCard(Zone.BATTLEFIELD, playerB, "Swamp", 3);
        addCard(Zone.BATTLEFIELD, playerA, lion);
        addCard(Zone.BATTLEFIELD, playerA, elf);
        addCard(Zone.HAND, playerA, exchange);
        addCard(Zone.HAND, playerA, "Giant Growth");
        addCard(Zone.HAND, playerB, murder);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, exchange);
        addTarget(playerA, lion);
        addTarget(playerA, elf);
        castSpell(2, PhaseStep.PRECOMBAT_MAIN, playerB, murder, elf);

        checkPlayableAbility("Lion keeps mana ability", 3, PhaseStep.PRECOMBAT_MAIN, playerA, "{T}: Add {G}", true);
        activateAbility(3, PhaseStep.PRECOMBAT_MAIN, playerA, "{T}: Add {G}");
        castSpell(3, PhaseStep.PRECOMBAT_MAIN, playerA, "Giant Growth", lion);

        setStopAt(3, PhaseStep.END_TURN);
        execute();

        assertGraveyardCount(playerA, elf, 1);
        assertPowerToughness(playerA, lion, 5, 5);
    }

    @Test
    public void testExchangeEndsWhenEnchantmentLeavesBattlefield() {
        setStrictChooseMode(true);

        addCard(Zone.BATTLEFIELD, playerA, "Island", 3);
        addCard(Zone.BATTLEFIELD, playerB, "Plains", 2);
        addCard(Zone.BATTLEFIELD, playerA, lion);
        addCard(Zone.BATTLEFIELD, playerA, drake);
        addCard(Zone.HAND, playerA, exchange);
        addCard(Zone.HAND, playerB, disenchant);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, exchange);
        addTarget(playerA, lion);
        addTarget(playerA, drake);

        checkAbility("lion has flying while exchanged", 1, PhaseStep.BEGIN_COMBAT, playerA, lion, FlyingAbility.class, true);
        checkAbility("drake loses flying while exchanged", 1, PhaseStep.BEGIN_COMBAT, playerA, drake, FlyingAbility.class, false);

        castSpell(2, PhaseStep.PRECOMBAT_MAIN, playerB, disenchant, exchange);

        checkAbility("lion loses flying after disenchant", 2, PhaseStep.BEGIN_COMBAT, playerA, lion, FlyingAbility.class, false);
        checkAbility("drake regains flying after disenchant", 2, PhaseStep.BEGIN_COMBAT, playerA, drake, FlyingAbility.class, true);

        setStopAt(2, PhaseStep.END_TURN);
        execute();
    }

    @Test
    public void testBlinkBreaksExchangeForReturnedCreature() {
        setStrictChooseMode(true);

        addCard(Zone.BATTLEFIELD, playerA, "Island", 3);
        addCard(Zone.BATTLEFIELD, playerA, "Plains");
        addCard(Zone.BATTLEFIELD, playerA, lion);
        addCard(Zone.BATTLEFIELD, playerA, drake);
        addCard(Zone.HAND, playerA, exchange);
        addCard(Zone.HAND, playerA, cloudshift);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, exchange);
        addTarget(playerA, lion);
        addTarget(playerA, drake);

        checkAbility("lion has flying while exchanged", 1, PhaseStep.BEGIN_COMBAT, playerA, lion, FlyingAbility.class, true);
        checkAbility("drake loses flying while exchanged", 1, PhaseStep.BEGIN_COMBAT, playerA, drake, FlyingAbility.class, false);

        castSpell(2, PhaseStep.PRECOMBAT_MAIN, playerA, cloudshift, lion);

        checkAbility("blinked lion is no longer exchanged", 2, PhaseStep.BEGIN_COMBAT, playerA, lion, FlyingAbility.class, false);
        checkAbility("drake keeps old exchanged text", 2, PhaseStep.BEGIN_COMBAT, playerA, drake, FlyingAbility.class, false);

        setStopAt(2, PhaseStep.END_TURN);
        execute();
    }
}
