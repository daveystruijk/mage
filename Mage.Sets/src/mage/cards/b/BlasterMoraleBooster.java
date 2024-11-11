package mage.cards.b;

import mage.abilities.keyword.TransformAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.*;

import java.util.UUID;

/**
 * @author daveystruijk
 */
public final class BlasterMoraleBooster extends CardImpl {

    public BlasterMoraleBooster(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ARTIFACT}, "");

        this.addSuperType(SuperType.LEGENDARY);
        this.color.setRed(true);
        this.color.setGreen(true);
        this.nightCard = true;

        // Modular 3

        // {X}, {T}: Move X +1/+1 counters from Blaster onto another target artifact. That artifact gains haste until end of turn. If Blaster has no +1/+1 counters on it, convert it. Activate only as a sorcery.

        // Transform Ability
        this.addAbility(new TransformAbility());
    }

    private BlasterMoraleBooster(final BlasterMoraleBooster card) {
        super(card);
    }

    @Override
    public BlasterMoraleBooster copy() {
        return new BlasterMoraleBooster(this);
    }
}

