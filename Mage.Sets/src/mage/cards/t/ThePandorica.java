package mage.cards.t;

import mage.MageObjectReference;
import mage.abilities.Ability;
import mage.abilities.common.ActivateAsSorceryActivatedAbility;
import mage.abilities.common.LeavesBattlefieldTriggeredAbility;
import mage.abilities.common.SkipUntapOptionalAbility;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.ContinuousRuleModifyingEffectImpl;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.UntapTargetEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.filter.common.FilterNonlandPermanent;
import mage.filter.predicate.mageobject.AnotherPredicate;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.permanent.Permanent;
import mage.target.TargetPermanent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author daveystruijk
 */
public final class ThePandorica extends CardImpl {

    private static final FilterNonlandPermanent filter = new FilterNonlandPermanent("another target nonland permanent");

    static {
        filter.add(AnotherPredicate.instance);
    }

    public ThePandorica(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ARTIFACT}, "{4}");

        // You may choose not to untap The Pandorica during your untap step.
        this.addAbility(new SkipUntapOptionalAbility());

        // {1}{W}, {T}: Untap another target nonland permanent, then it phases out.
        // It can't phase in for as long as The Pandorica remains tapped.
        // When The Pandorica becomes untapped or leaves the battlefield, that permanent phases in.
        // Activate only as a sorcery.
        Ability ability = new ActivateAsSorceryActivatedAbility(new UntapTargetEffect(), new ManaCostsImpl<>("{1}{W}"));
        ability.addCost(new TapSourceCost());
        ability.addEffect(new ThePandoricaPhaseOutTargetEffect().setText(", then it phases out. It can't phase in for as long as {this} remains tapped. "
                + "When {this} becomes untapped or leaves the battlefield, that permanent phases in"));
        ability.addTarget(new TargetPermanent(filter));
        this.addAbility(ability);

        this.addAbility(new ThePandoricaTriggeredAbility(new ThePandoricaPhaseInStoredEffect()));
    }

    private ThePandorica(final ThePandorica card) {
        super(card);
    }

    @Override
    public ThePandorica copy() {
        return new ThePandorica(this);
    }
}

class ThePandoricaPhaseOutTargetEffect extends OneShotEffect {

    ThePandoricaPhaseOutTargetEffect() {
        super(Outcome.Detriment);
    }

    private ThePandoricaPhaseOutTargetEffect(final ThePandoricaPhaseOutTargetEffect effect) {
        super(effect);
    }

    @Override
    public ThePandoricaPhaseOutTargetEffect copy() {
        return new ThePandoricaPhaseOutTargetEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent sourcePermanent = source.getSourcePermanentIfItStillExists(game);
        Permanent permanent = game.getPermanent(getTargetPointer().getFirst(game, source));
        if (sourcePermanent == null || permanent == null) {
            return false;
        }

        MageObjectReference mor = new MageObjectReference(permanent, game);
        permanent.phaseOut(game);

        List<MageObjectReference> references = (List<MageObjectReference>) game.getState().getValue(ThePandoricaUtil.getKey(source));
        if (references == null) {
            references = new ArrayList<>();
        }
        references.add(mor);
        game.getState().setValue(ThePandoricaUtil.getKey(source), references);

        game.addEffect(new ThePandoricaPhasePreventEffect(mor), source);

        return true;
    }
}

class ThePandoricaPhasePreventEffect extends ContinuousRuleModifyingEffectImpl {

    private final MageObjectReference mor;

    ThePandoricaPhasePreventEffect(MageObjectReference mor) {
        super(Duration.WhileOnBattlefield, Outcome.Neutral);
        this.mor = mor;
    }

    private ThePandoricaPhasePreventEffect(final ThePandoricaPhasePreventEffect effect) {
        super(effect);
        this.mor = effect.mor;
    }

    @Override
    public ThePandoricaPhasePreventEffect copy() {
        return new ThePandoricaPhasePreventEffect(this);
    }

    @Override
    public boolean checksEventType(GameEvent event, Game game) {
        return event.getType() == GameEvent.EventType.PHASE_IN;
    }

    @Override
    public boolean applies(GameEvent event, Ability source, Game game) {
        Permanent sourcePermanent = source.getSourcePermanentIfItStillExists(game);
        return sourcePermanent != null
                && sourcePermanent.isTapped()
                && mor.refersTo(event.getTargetId(), game);
    }
}

class ThePandoricaTriggeredAbility extends LeavesBattlefieldTriggeredAbility {

    ThePandoricaTriggeredAbility(OneShotEffect effect) {
        super(effect, false);
        setTriggerPhrase("When {this} becomes untapped or leaves the battlefield, ");
    }

    private ThePandoricaTriggeredAbility(final ThePandoricaTriggeredAbility ability) {
        super(ability);
    }

    @Override
    public ThePandoricaTriggeredAbility copy() {
        return new ThePandoricaTriggeredAbility(this);
    }

    @Override
    public boolean checkEventType(GameEvent event, Game game) {
        return super.checkEventType(event, game)
                || event.getType() == GameEvent.EventType.UNTAPPED;
    }

    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
        if (event.getType() == GameEvent.EventType.UNTAPPED) {
            return event.getTargetId().equals(getSourceId());
        }
        return super.checkTrigger(event, game);
    }
}

class ThePandoricaPhaseInStoredEffect extends OneShotEffect {

    ThePandoricaPhaseInStoredEffect() {
        super(Outcome.Benefit);
        staticText = "that permanent phases in";
    }

    private ThePandoricaPhaseInStoredEffect(final ThePandoricaPhaseInStoredEffect effect) {
        super(effect);
    }

    @Override
    public ThePandoricaPhaseInStoredEffect copy() {
        return new ThePandoricaPhaseInStoredEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        List<MageObjectReference> references = (List<MageObjectReference>) game.getState().getValue(ThePandoricaUtil.getKey(source));
        if (references == null || references.isEmpty()) {
            return false;
        }

        for (MageObjectReference mor : references) {
            Permanent permanent = mor.getPermanent(game);
            if (permanent != null) {
                permanent.phaseIn(game);
            }
        }

        game.getState().setValue(ThePandoricaUtil.getKey(source), null);
        return true;
    }
}

final class ThePandoricaUtil {

    private ThePandoricaUtil() {
    }

    static String getKey(Ability source) {
        return source.getSourceId() + "_thePandorica";
    }
}
