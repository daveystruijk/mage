package mage.cards.t;

import mage.MageInt;
import mage.MageObjectReference;
import mage.abilities.Ability;
import mage.abilities.TriggeredAbilityImpl;
import mage.abilities.common.AttacksTriggeredAbility;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.counter.AddCountersSourceEffect;
import mage.abilities.effects.common.replacement.DiesReplacementEffect;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.SubType;
import mage.constants.SuperType;
import mage.constants.Zone;
import mage.counters.CounterType;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.events.ZoneChangeGroupEvent;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.common.TargetAnyTarget;

import java.util.UUID;

/**
 * @author daveystruijk
 */
public final class TheWarDoctor extends CardImpl {

    public TheWarDoctor(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{2}{R}{W}");

        this.supertype.add(SuperType.LEGENDARY);
        this.subtype.add(SubType.TIME_LORD);
        this.subtype.add(SubType.DOCTOR);
        this.power = new MageInt(3);
        this.toughness = new MageInt(5);

        // Whenever one or more other permanents phase out and whenever one or more other cards are put into exile from anywhere, put a time counter on The War Doctor.
        this.addAbility(new TheWarDoctorTriggeredAbility());

        // Whenever The War Doctor attacks, it deals damage equal to the number of time counters on it to any target. If a creature dealt damage this way would die this turn, exile it instead.
        Ability ability = new AttacksTriggeredAbility(new TheWarDoctorEffect(), false);
        ability.addTarget(new TargetAnyTarget());
        this.addAbility(ability);
    }

    private TheWarDoctor(final TheWarDoctor card) {
        super(card);
    }

    @Override
    public TheWarDoctor copy() {
        return new TheWarDoctor(this);
    }
}

class TheWarDoctorTriggeredAbility extends TriggeredAbilityImpl {

    TheWarDoctorTriggeredAbility() {
        super(Zone.BATTLEFIELD, new AddCountersSourceEffect(CounterType.TIME.createInstance()));
    }

    private TheWarDoctorTriggeredAbility(final TheWarDoctorTriggeredAbility ability) {
        super(ability);
    }

    @Override
    public TheWarDoctorTriggeredAbility copy() {
        return new TheWarDoctorTriggeredAbility(this);
    }

    @Override
    public boolean checkEventType(GameEvent event, Game game) {
        return event.getType() == GameEvent.EventType.PHASE_OUT
                || event.getType() == GameEvent.EventType.ZONE_CHANGE_GROUP;
    }

    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
        Permanent sourcePermanent = getSourcePermanentIfItStillExists(game);
        if (sourcePermanent == null || !sourcePermanent.isPhasedIn()) {
            return false;
        }

        if (event.getType() == GameEvent.EventType.PHASE_OUT) {
            return !event.getTargetId().equals(getSourceId());
        }

        ZoneChangeGroupEvent zEvent = (ZoneChangeGroupEvent) event;
        return zEvent.getToZone() == Zone.EXILED
                && zEvent.getCards() != null
                && zEvent.getCards().stream().map(Card::getId).anyMatch(id -> !id.equals(getSourceId()));
    }

    @Override
    public String getRule() {
        return "Whenever one or more other permanents phase out and whenever one or more other cards "
                + "are put into exile from anywhere, put a time counter on {this}.";
    }
}

class TheWarDoctorEffect extends OneShotEffect {

    TheWarDoctorEffect() {
        super(Outcome.Damage);
        staticText = "it deals damage equal to the number of time counters on it to any target. "
                + "If a creature dealt damage this way would die this turn, exile it instead";
    }

    private TheWarDoctorEffect(final TheWarDoctorEffect effect) {
        super(effect);
    }

    @Override
    public TheWarDoctorEffect copy() {
        return new TheWarDoctorEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent sourcePermanent = game.getPermanent(source.getSourceId());
        if (sourcePermanent == null) {
            sourcePermanent = (Permanent) game.getLastKnownInformation(source.getSourceId(), Zone.BATTLEFIELD);
        }
        if (sourcePermanent == null) {
            return false;
        }

        int damage = sourcePermanent.getCounters(game).getCount(CounterType.TIME);

        Permanent permanent = game.getPermanent(getTargetPointer().getFirst(game, source));
        if (permanent != null) {
            permanent.damage(damage, sourcePermanent.getId(), source, game, false, true);
            if (permanent.isCreature(game)) {
                game.addEffect(new DiesReplacementEffect(new MageObjectReference(permanent, game), Duration.EndOfTurn), source);
            }
            return true;
        }

        Player player = game.getPlayer(getTargetPointer().getFirst(game, source));
        if (player != null) {
            player.damage(damage, sourcePermanent.getId(), source, game);
            return true;
        }

        return false;
    }
}
