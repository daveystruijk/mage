package mage.cards.b;

import mage.MageInt;
import mage.abilities.keyword.MoreThanMeetsTheEyeAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.*;

import java.util.UUID;

/**
 * @author daveystruijk
 */
public final class BlasterCombatDJ extends CardImpl {

    public BlasterCombatDJ(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ARTIFACT, CardType.CREATURE}, "{3}{R}{G}");

        this.addSuperType(SuperType.LEGENDARY);
        this.subtype.add(SubType.ROBOT);
        this.power = new MageInt(3);
        this.toughness = new MageInt(3);
        this.secondSideCardClazz = mage.cards.b.BlasterMoraleBooster.class;

        // More Than Meets the Eye {1}{R}{G}
        this.addAbility(new MoreThanMeetsTheEyeAbility(this, "{1}{R}{G}"));

        // Other nontoken artifact creatures and Vehicles you control have modular 1.

        // Whenever you put one or more +1/+1 counters on Blaster, convert it.
    }

    private BlasterCombatDJ(final BlasterCombatDJ card) {
        super(card);
    }

    @Override
    public BlasterCombatDJ copy() {
        return new BlasterCombatDJ(this);
    }
}

