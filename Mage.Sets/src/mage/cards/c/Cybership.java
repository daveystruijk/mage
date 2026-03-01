package mage.cards.c;

import mage.MageInt;
import mage.MageObjectReference;
import mage.abilities.Ability;
import mage.abilities.common.DealsCombatDamageToAPlayerTriggeredAbility;
import mage.abilities.effects.ContinuousEffectImpl;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.keyword.CrewAbility;
import mage.abilities.keyword.FlyingAbility;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.cards.Cards;
import mage.cards.CardsImpl;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Layer;
import mage.constants.Outcome;
import mage.constants.SubLayer;
import mage.constants.SubType;
import mage.constants.Zone;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.targetpointer.FixedTargets;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author daveystruijk
 */
public final class Cybership extends CardImpl {

    public Cybership(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ARTIFACT}, "{6}");

        this.subtype.add(SubType.VEHICLE);
        this.power = new MageInt(8);
        this.toughness = new MageInt(8);

        // Flying
        this.addAbility(FlyingAbility.getInstance());

        // Whenever this Vehicle deals combat damage to a player, put the top two cards of that player's library onto the battlefield face down under your control. They're 2/2 Cyberman artifact creatures.
        this.addAbility(new DealsCombatDamageToAPlayerTriggeredAbility(new CybershipEffect(), false, true)
                .setTriggerPhrase("Whenever this Vehicle deals combat damage to a player, "));

        // Crew 4
        this.addAbility(new CrewAbility(4));
    }

    private Cybership(final Cybership card) {
        super(card);
    }

    @Override
    public Cybership copy() {
        return new Cybership(this);
    }
}

class CybershipEffect extends OneShotEffect {

    CybershipEffect() {
        super(Outcome.Benefit);
        staticText = "put the top two cards of that player's library onto the battlefield "
                + "face down under your control. They're 2/2 Cyberman artifact creatures";
    }

    private CybershipEffect(final CybershipEffect effect) {
        super(effect);
    }

    @Override
    public CybershipEffect copy() {
        return new CybershipEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        Player damagedPlayer = game.getPlayer(getTargetPointer().getFirst(game, source));
        if (controller == null || damagedPlayer == null) {
            return false;
        }

        Cards cardsToMove = new CardsImpl(damagedPlayer.getLibrary().getTopCards(game, 2));
        if (cardsToMove.isEmpty()) {
            return false;
        }

        game.addEffect(new CybershipContinuousEffect().setTargetPointer(new FixedTargets(
                cardsToMove
                        .getCards(game)
                        .stream()
                        .map(card -> new MageObjectReference(card, game, 1))
                        .collect(Collectors.toList())
        )), source);

        return controller.moveCards(
                cardsToMove.getCards(game), Zone.BATTLEFIELD, source, game,
                false, true, false, null
        );
    }
}

class CybershipContinuousEffect extends ContinuousEffectImpl {

    CybershipContinuousEffect() {
        super(Duration.Custom, Layer.CopyEffects_1, SubLayer.FaceDownEffects_1b, Outcome.Neutral);
    }

    private CybershipContinuousEffect(final CybershipContinuousEffect effect) {
        super(effect);
    }

    @Override
    public CybershipContinuousEffect copy() {
        return new CybershipContinuousEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        boolean found = false;
        for (UUID targetId : getTargetPointer().getTargets(game, source)) {
            Permanent permanent = game.getPermanent(targetId);
            if (permanent == null || !permanent.isFaceDown(game)) {
                continue;
            }

            found = true;
            permanent.removeAllSuperTypes(game);
            permanent.removeAllCardTypes(game);
            permanent.removeAllSubTypes(game);
            permanent.addCardType(game, CardType.ARTIFACT, CardType.CREATURE);
            permanent.addSubType(game, SubType.CYBERMAN);
            permanent.getPower().setModifiedBaseValue(2);
            permanent.getToughness().setModifiedBaseValue(2);
        }

        if (!found) {
            discard();
            return false;
        }
        return true;
    }
}
