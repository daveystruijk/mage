package mage.cards.d;

import mage.abilities.Ability;
import mage.abilities.triggers.BeginningOfUpkeepTriggeredAbility;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.common.SacrificeSourceCost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.counter.AddCountersSourceEffect;
import mage.abilities.effects.common.discard.LookTargetHandChooseDiscardEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.Zone;
import mage.counters.CounterType;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.target.common.TargetOpponent;

import java.util.UUID;

/**
 * @author jeffwadsworth
 */
public final class DiscordantDirge extends CardImpl {

    public DiscordantDirge(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ENCHANTMENT}, "{3}{B}{B}");

        // At the beginning of your upkeep, you may put a verse counter on Discordant Dirge.
        this.addAbility(new BeginningOfUpkeepTriggeredAbility(
                new AddCountersSourceEffect(CounterType.VERSE.createInstance(), true), true));

        // {B}, Sacrifice Discordant Dirge: Look at target opponent's hand and choose up to X cards from it, where X is the number of verse counters on Discordant Dirge. That player discards those cards.
        Ability ability = new SimpleActivatedAbility(Zone.BATTLEFIELD,
                new DiscordantDirgeEffect(),
                new ManaCostsImpl<>("{B}"));
        ability.addCost(new SacrificeSourceCost());
        ability.addTarget(new TargetOpponent());
        this.addAbility(ability);
    }

    private DiscordantDirge(final DiscordantDirge card) {
        super(card);
    }

    @Override
    public DiscordantDirge copy() {
        return new DiscordantDirge(this);
    }
}

class DiscordantDirgeEffect extends OneShotEffect {

    DiscordantDirgeEffect() {
        super(Outcome.Benefit);
        staticText = "Look at target opponent's hand and choose up to X cards from it, " +
                "where X is the number of verse counters on {this}. That player discards those cards";
    }

    private DiscordantDirgeEffect(final DiscordantDirgeEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent discordantDirge = game.getPermanentOrLKIBattlefield(source.getSourceId());
        if (discordantDirge == null) {
            return false;
        }
        int verseCounters = discordantDirge.getCounters(game).getCount(CounterType.VERSE);
        return new LookTargetHandChooseDiscardEffect(true, verseCounters).apply(game, source);
    }

    @Override
    public DiscordantDirgeEffect copy() {
        return new DiscordantDirgeEffect(this);
    }
}
