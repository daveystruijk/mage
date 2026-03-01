package mage.cards.d;

import mage.abilities.Ability;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.ReplacementEffectImpl;
import mage.abilities.keyword.CyclingAbility;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.Zone;
import mage.game.Game;
import mage.game.events.EntersTheBattlefieldEvent;
import mage.game.events.GameEvent;
import mage.game.stack.Spell;
import mage.players.Player;

import java.util.UUID;

/**
 * @author daveystruijk
 */
public final class DontBlink extends CardImpl {

    public DontBlink(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.INSTANT}, "{1}{U}");

        // Until end of turn, if one or more creatures would enter from exile or after being cast from exile, their owners shuffle them into their libraries instead.
        this.getSpellAbility().addEffect(new DontBlinkEffect());

        // Cycling {2}
        this.addAbility(new CyclingAbility(new ManaCostsImpl<>("{2}")));
    }

    private DontBlink(final DontBlink card) {
        super(card);
    }

    @Override
    public DontBlink copy() {
        return new DontBlink(this);
    }
}

class DontBlinkEffect extends ReplacementEffectImpl {

    DontBlinkEffect() {
        super(Duration.EndOfTurn, Outcome.Benefit);
        staticText = "Until end of turn, if one or more creatures would enter from exile "
                + "or after being cast from exile, their owners shuffle them into their libraries instead";
    }

    private DontBlinkEffect(final DontBlinkEffect effect) {
        super(effect);
    }

    @Override
    public DontBlinkEffect copy() {
        return new DontBlinkEffect(this);
    }

    @Override
    public boolean checksEventType(GameEvent event, Game game) {
        return event.getType() == GameEvent.EventType.ENTERS_THE_BATTLEFIELD;
    }

    @Override
    public boolean applies(GameEvent event, Ability source, Game game) {
        EntersTheBattlefieldEvent entersEvent = (EntersTheBattlefieldEvent) event;
        Card card = entersEvent.getTarget();
        if (card == null || !card.isCreature(game)) {
            return false;
        }

        Zone fromZone = entersEvent.getFromZone();
        if (fromZone == Zone.EXILED) {
            return true;
        }

        if (fromZone == Zone.STACK) {
            Spell spell = game.getSpellOrLKIStack(event.getTargetId());
            return spell != null && spell.getFromZone() == Zone.EXILED;
        }

        return false;
    }

    @Override
    public boolean replaceEvent(GameEvent event, Ability source, Game game) {
        Card card = game.getCard(event.getTargetId());
        if (card == null) {
            card = ((EntersTheBattlefieldEvent) event).getTarget();
        }

        if (card == null) {
            return false;
        }

        Player owner = game.getPlayer(card.getOwnerId());
        if (owner == null) {
            return false;
        }

        if (!owner.moveCards(card, Zone.LIBRARY, source, game)) {
            return false;
        }
        owner.shuffleLibrary(source, game);
        return true;
    }
}
