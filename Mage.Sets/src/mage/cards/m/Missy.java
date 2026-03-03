package mage.cards.m;

import mage.MageInt;
import mage.MageObjectReference;
import mage.abilities.Ability;
import mage.abilities.common.DiesCreatureTriggeredAbility;
import mage.abilities.effects.ContinuousEffectImpl;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.FaceVillainousChoiceOpponentsEffect;
import mage.abilities.triggers.BeginningOfEndStepTriggeredAbility;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.choices.FaceVillainousChoice;
import mage.choices.VillainousChoice;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Layer;
import mage.constants.Outcome;
import mage.constants.SubLayer;
import mage.constants.SubType;
import mage.constants.SuperType;
import mage.constants.Zone;
import mage.filter.FilterPermanent;
import mage.filter.common.FilterControlledPermanent;
import mage.filter.common.FilterCreaturePermanent;
import mage.filter.predicate.Predicates;
import mage.filter.predicate.mageobject.AnotherPredicate;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.targetpointer.FixedTarget;

import java.util.UUID;

/**
 * @author daveystruijk
 */
public final class Missy extends CardImpl {

    private static final FilterCreaturePermanent filter = new FilterCreaturePermanent("another nonartifact creature");
    private static final FaceVillainousChoice choice = new FaceVillainousChoice(
            Outcome.Damage, new MissyFirstChoice(), new MissySecondChoice()
    );

    static {
        filter.add(AnotherPredicate.instance);
        filter.add(Predicates.not(CardType.ARTIFACT.getPredicate()));
    }

    public Missy(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{3}{U}{B}{R}");

        this.supertype.add(SuperType.LEGENDARY);
        this.subtype.add(SubType.TIME_LORD);
        this.subtype.add(SubType.ROGUE);
        this.power = new MageInt(4);
        this.toughness = new MageInt(5);

        // Whenever another nonartifact creature dies, return it to the battlefield under your control face down and tapped. It's a 2/2 Cyberman artifact creature.
        this.addAbility(new DiesCreatureTriggeredAbility(
                new MissyRaiseCybermanEffect(), false, filter, true
        ));

        // At the beginning of your end step, each opponent faces a villainous choice -- Each artifact creature you control deals 1 damage to that opponent, or you draw a card and chaos ensues.
        this.addAbility(new BeginningOfEndStepTriggeredAbility(new FaceVillainousChoiceOpponentsEffect(choice)));
    }

    private Missy(final Missy card) {
        super(card);
    }

    @Override
    public Missy copy() {
        return new Missy(this);
    }
}

class MissyRaiseCybermanEffect extends OneShotEffect {

    MissyRaiseCybermanEffect() {
        super(Outcome.Benefit);
        staticText = "return it to the battlefield under your control face down and tapped. "
                + "It's a 2/2 Cyberman artifact creature";
    }

    private MissyRaiseCybermanEffect(final MissyRaiseCybermanEffect effect) {
        super(effect);
    }

    @Override
    public MissyRaiseCybermanEffect copy() {
        return new MissyRaiseCybermanEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        Card card = game.getCard(getTargetPointer().getFirst(game, source));
        if (controller == null || card == null || game.getState().getZone(card.getId()) != Zone.GRAVEYARD) {
            return false;
        }

        game.addEffect(new MissyCybermanContinuousEffect(new MageObjectReference(
                card.getId(), card.getZoneChangeCounter(game) + 1, game
        )), source);
        return controller.moveCards(card, Zone.BATTLEFIELD, source, game, true, true, false, null);
    }
}

class MissyCybermanContinuousEffect extends ContinuousEffectImpl {

    MissyCybermanContinuousEffect(MageObjectReference mor) {
        super(Duration.Custom, Layer.CopyEffects_1, SubLayer.FaceDownEffects_1b, Outcome.Neutral);
        this.setTargetPointer(new FixedTarget(mor));
    }

    private MissyCybermanContinuousEffect(final MissyCybermanContinuousEffect effect) {
        super(effect);
    }

    @Override
    public MissyCybermanContinuousEffect copy() {
        return new MissyCybermanContinuousEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent target = game.getPermanent(getTargetPointer().getFirst(game, source));
        if (target == null || !target.isFaceDown(game)) {
            discard();
            return false;
        }

        target.removeAllSuperTypes(game);
        target.removeAllCardTypes(game);
        target.removeAllSubTypes(game);
        target.addCardType(game, CardType.ARTIFACT, CardType.CREATURE);
        target.addSubType(game, SubType.CYBERMAN);
        target.getPower().setModifiedBaseValue(2);
        target.getToughness().setModifiedBaseValue(2);
        return true;
    }
}

class MissyFirstChoice extends VillainousChoice {

    private static final FilterPermanent filter = new FilterControlledPermanent("artifact creatures you control");

    static {
        filter.add(CardType.ARTIFACT.getPredicate());
        filter.add(CardType.CREATURE.getPredicate());
    }

    MissyFirstChoice() {
        super("Each artifact creature you control deals 1 damage to that opponent", "Each artifact creature your opponent controls deals 1 damage to you");
    }

    @Override
    public boolean doChoice(Player player, Game game, Ability source) {
        for (Permanent permanent : game.getBattlefield().getActivePermanents(filter, source.getControllerId(), source, game)) {
            player.damage(1, permanent.getId(), source, game);
        }
        return true;
    }
}

class MissySecondChoice extends VillainousChoice {

    MissySecondChoice() {
        super("you draw a card and chaos ensues", "{controller} draws a card and chaos ensues");
    }

    @Override
    public boolean doChoice(Player player, Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        if (controller == null) {
            return false;
        }
        controller.drawCards(1, source, game);
        return true;
    }
}
