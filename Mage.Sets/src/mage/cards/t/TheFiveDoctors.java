package mage.cards.t;

import mage.abilities.Ability;
import mage.abilities.condition.common.KickedCondition;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.keyword.KickerAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.cards.Cards;
import mage.cards.CardsImpl;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.SubType;
import mage.constants.Zone;
import mage.filter.FilterCard;
import mage.game.Game;
import mage.players.Player;
import mage.target.TargetCard;
import mage.target.common.TargetCardInLibrary;
import mage.target.common.TargetCardInYourGraveyard;

import java.util.UUID;

/**
 * @author daveystruijk
 */
public final class TheFiveDoctors extends CardImpl {

    static final FilterCard filter = new FilterCard("up to five Doctor cards");

    static {
        filter.add(SubType.DOCTOR.getPredicate());
    }

    public TheFiveDoctors(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.SORCERY}, "{5}{G}");

        // Kicker {5}
        this.addAbility(new KickerAbility("{5}"));

        // Search your library and/or graveyard for up to five Doctor cards, reveal them, and put them into your hand. If you search your library this way, shuffle. If this spell was kicked, put those cards onto the battlefield instead of putting them into your hand.
        this.getSpellAbility().addEffect(new TheFiveDoctorsEffect());
    }

    private TheFiveDoctors(final TheFiveDoctors card) {
        super(card);
    }

    @Override
    public TheFiveDoctors copy() {
        return new TheFiveDoctors(this);
    }
}

class TheFiveDoctorsEffect extends OneShotEffect {

    private static final int MAX_CARDS = 5;

    TheFiveDoctorsEffect() {
        super(Outcome.Benefit);
        staticText = "search your library and/or graveyard for up to five Doctor cards, reveal them, "
                + "and put them into your hand. If you search your library this way, shuffle. "
                + "If this spell was kicked, put those cards onto the battlefield instead of putting them into your hand";
    }

    private TheFiveDoctorsEffect(final TheFiveDoctorsEffect effect) {
        super(effect);
    }

    @Override
    public TheFiveDoctorsEffect copy() {
        return new TheFiveDoctorsEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        if (controller == null) {
            return false;
        }

        Cards cards = new CardsImpl();
        boolean searchedLibrary = false;

        if (controller.chooseUse(outcome, "Search your library for up to five Doctor cards?", source, game)) {
            TargetCardInLibrary target = new TargetCardInLibrary(0, MAX_CARDS, TheFiveDoctors.filter);
            target.withNotTarget(true);
            controller.searchLibrary(target, source, game);
            cards.addAll(target.getTargets());
            searchedLibrary = true;
        }

        int remainingCards = MAX_CARDS - cards.size();
        if (remainingCards > 0
                && controller.chooseUse(
                outcome,
                "Search your graveyard for up to " + remainingCards + " Doctor card" + (remainingCards == 1 ? "" : "s") + '?',
                source,
                game
        )) {
            TargetCard target = new TargetCardInYourGraveyard(0, remainingCards, TheFiveDoctors.filter, true);
            target.withNotTarget(true);
            controller.choose(outcome, controller.getGraveyard(), target, source, game);
            cards.addAll(target.getTargets());
        }

        cards.removeIf(uuid -> {
            Zone zone = game.getState().getZone(uuid);
            return zone != Zone.LIBRARY && zone != Zone.GRAVEYARD;
        });

        if (!cards.isEmpty()) {
            controller.revealCards(source, cards, game);
            controller.moveCards(cards, KickedCondition.ONCE.apply(game, source) ? Zone.BATTLEFIELD : Zone.HAND, source, game);
        }

        if (searchedLibrary) {
            controller.shuffleLibrary(source, game);
        }

        return true;
    }
}
