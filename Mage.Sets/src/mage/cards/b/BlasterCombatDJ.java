package mage.cards.b;

import mage.MageInt;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.common.continuous.GainAbilityControlledEffect;
import mage.abilities.effects.common.continuous.GainAbilityControlledSpellsEffect;
import mage.abilities.keyword.ModularAbility;
import mage.abilities.keyword.MoreThanMeetsTheEyeAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.*;
import mage.filter.FilterPermanent;
import mage.filter.common.FilterNonlandCard;
import mage.filter.predicate.Predicates;
import mage.filter.predicate.mageobject.AnotherPredicate;
import mage.filter.predicate.permanent.TokenPredicate;

import java.util.UUID;

/**
 * @author daveystruijk
 */
public final class BlasterCombatDJ extends CardImpl {

    private static final FilterPermanent battlefieldFilter
            = new FilterPermanent("other nontoken artifact creatures and vehicles you control");
    private static final FilterNonlandCard cardFilter
            = new FilterNonlandCard("other nontoken artifact creatures and vehicles you control");

    static {
        // other
        battlefieldFilter.add(AnotherPredicate.instance);
        // nontoken
        battlefieldFilter.add(TokenPredicate.FALSE);
        // artifact creatures and vehicles
        battlefieldFilter.add(Predicates.or(
                Predicates.and(
                        CardType.ARTIFACT.getPredicate(),
                        CardType.CREATURE.getPredicate()
                ),
                SubType.VEHICLE.getPredicate()
        ));
        // you control
        battlefieldFilter.add(TargetController.YOU.getControllerPredicate());
    }

    static {
        // artifact creatures and vehicles
        cardFilter.add(Predicates.or(
                Predicates.and(
                        CardType.ARTIFACT.getPredicate(),
                        CardType.CREATURE.getPredicate()
                ),
                SubType.VEHICLE.getPredicate()
        ));
    }

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
        this.addAbility(new SimpleStaticAbility(new GainAbilityControlledEffect(
                new ModularAbility(null, 1), Duration.WhileOnBattlefield,
                battlefieldFilter, true
        )));
        this.addAbility(new SimpleStaticAbility(new GainAbilityControlledSpellsEffect(
                new ModularAbility(null, 1), cardFilter
        )));

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

