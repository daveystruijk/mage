package mage.cards.a;

import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.triggers.BeginningOfCombatTriggeredAbility;
import mage.abilities.effects.common.counter.AddCountersTargetEffect;
import mage.abilities.keyword.DayboundAbility;
import mage.abilities.keyword.HexproofAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.counters.CounterType;
import mage.filter.StaticFilters;
import mage.target.TargetPermanent;

import java.util.UUID;

/**
 * @author TheElk801
 */
public final class AvabruckCaretaker extends CardImpl {

    public AvabruckCaretaker(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{4}{G}{G}");

        this.subtype.add(SubType.HUMAN);
        this.subtype.add(SubType.WEREWOLF);
        this.power = new MageInt(4);
        this.toughness = new MageInt(4);
        this.secondSideCardClazz = mage.cards.h.HollowhengeHuntmaster.class;

        // Hexproof
        this.addAbility(HexproofAbility.getInstance());

        // At the beginning of combat on your turn, put two +1/+1 counters on another target creature you control.
        Ability ability = new BeginningOfCombatTriggeredAbility(
                new AddCountersTargetEffect(
                        CounterType.P1P1.createInstance(2)
                )
        );
        ability.addTarget(new TargetPermanent(StaticFilters.FILTER_ANOTHER_TARGET_CREATURE_YOU_CONTROL));
        this.addAbility(ability);

        // Daybound
        this.addAbility(new DayboundAbility());
    }

    private AvabruckCaretaker(final AvabruckCaretaker card) {
        super(card);
    }

    @Override
    public AvabruckCaretaker copy() {
        return new AvabruckCaretaker(this);
    }
}
