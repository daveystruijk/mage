package mage.cards.d;

import mage.MageObjectReference;
import mage.abilities.Ability;
import mage.abilities.common.SagaAbility;
import mage.abilities.effects.ContinuousEffectImpl;
import mage.abilities.effects.OneShotEffect;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.cards.Cards;
import mage.cards.CardsImpl;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Layer;
import mage.constants.Outcome;
import mage.constants.SagaChapter;
import mage.constants.SubLayer;
import mage.constants.SubType;
import mage.constants.Zone;
import mage.filter.StaticFilters;
import mage.game.ExileZone;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.TargetPlayer;
import mage.target.targetpointer.FixedTargets;
import mage.util.CardUtil;

import java.util.Objects;
import java.util.Set;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author daveystruijk
 */
public final class DeathInHeaven extends CardImpl {

    public DeathInHeaven(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ENCHANTMENT}, "{3}{B}");

        this.subtype.add(SubType.SAGA);

        // (As this Saga enters and after your draw step, add a lore counter. Sacrifice after III.)
        SagaAbility sagaAbility = new SagaAbility(this);

        // I, II -- Target player mills two cards, then exiles their graveyard.
        sagaAbility.addChapterEffect(this, SagaChapter.CHAPTER_I, SagaChapter.CHAPTER_II,
                new DeathInHeavenMillAndExileEffect(), new TargetPlayer());

        // III -- Put all creature cards exiled with this enchantment onto the battlefield face down under your control. They're 2/2 Cyberman artifact creatures.
        sagaAbility.addChapterEffect(this, SagaChapter.CHAPTER_III, new DeathInHeavenEffect());

        this.addAbility(sagaAbility);
    }

    private DeathInHeaven(final DeathInHeaven card) {
        super(card);
    }

    @Override
    public DeathInHeaven copy() {
        return new DeathInHeaven(this);
    }
}

class DeathInHeavenMillAndExileEffect extends OneShotEffect {

    DeathInHeavenMillAndExileEffect() {
        super(Outcome.Exile);
        staticText = "target player mills two cards, then exiles their graveyard";
    }

    private DeathInHeavenMillAndExileEffect(final DeathInHeavenMillAndExileEffect effect) {
        super(effect);
    }

    @Override
    public DeathInHeavenMillAndExileEffect copy() {
        return new DeathInHeavenMillAndExileEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        Player targetPlayer = game.getPlayer(getTargetPointer().getFirst(game, source));
        if (controller == null || targetPlayer == null) {
            return false;
        }

        targetPlayer.millCards(2, source, game);
        Set<Card> cardsToExile = new HashSet<>(targetPlayer.getGraveyard().getCards(game));
        return controller.moveCardsToExile(
                cardsToExile, source, game, true,
                CardUtil.getExileZoneId(game, source), CardUtil.getSourceName(game, source)
        );
    }
}

class DeathInHeavenEffect extends OneShotEffect {

    DeathInHeavenEffect() {
        super(Outcome.Benefit);
        staticText = "put all creature cards exiled with this enchantment onto the battlefield "
                + "face down under your control. They're 2/2 Cyberman artifact creatures";
    }

    private DeathInHeavenEffect(final DeathInHeavenEffect effect) {
        super(effect);
    }

    @Override
    public DeathInHeavenEffect copy() {
        return new DeathInHeavenEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        if (controller == null) {
            return false;
        }

        ExileZone exileZone = game.getExile().getExileZone(CardUtil.getExileZoneId(game, source));
        if (exileZone == null || exileZone.isEmpty()) {
            return true;
        }

        Cards cardsToMove = new CardsImpl(exileZone.getCards(StaticFilters.FILTER_CARD_CREATURE, game));
        if (cardsToMove.isEmpty()) {
            return true;
        }

        game.addEffect(new DeathInHeavenContinuousEffect().setTargetPointer(new FixedTargets(
                cardsToMove
                        .getCards(game)
                        .stream()
                        .filter(Objects::nonNull)
                        .map(card -> new MageObjectReference(card, game, 1))
                        .collect(Collectors.toList())
        )), source);

        return controller.moveCards(
                cardsToMove.getCards(game), Zone.BATTLEFIELD, source, game,
                false, true, false, null
        );
    }
}

class DeathInHeavenContinuousEffect extends ContinuousEffectImpl {

    DeathInHeavenContinuousEffect() {
        super(Duration.Custom, Layer.CopyEffects_1, SubLayer.FaceDownEffects_1b, Outcome.Neutral);
    }

    private DeathInHeavenContinuousEffect(final DeathInHeavenContinuousEffect effect) {
        super(effect);
    }

    @Override
    public DeathInHeavenContinuousEffect copy() {
        return new DeathInHeavenContinuousEffect(this);
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
