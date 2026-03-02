package mage.cards.r;

import mage.MageObjectReference;
import mage.abilities.Ability;
import mage.abilities.TriggeredAbilityImpl;
import mage.abilities.condition.Condition;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.ReplacementEffectImpl;
import mage.abilities.triggers.BeginningOfUpkeepTriggeredAbility;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.Zone;
import mage.game.ExileZone;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.events.ZoneChangeEvent;
import mage.game.stack.Spell;
import mage.players.Player;
import mage.util.CardUtil;

import java.util.UUID;

/**
 * @author daveystruijk
 */
public final class RiverSongsDiary extends CardImpl {

    public RiverSongsDiary(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ARTIFACT}, "{3}");

        // Imprint -- Whenever a player casts an instant or sorcery spell from their hand,
        // exile it instead of putting it into a graveyard as it resolves.
        this.addAbility(new RiverSongsDiaryTriggeredAbility());

        // At the beginning of your upkeep, if there are four or more cards exiled with this artifact,
        // choose one of them at random. You may cast it without paying its mana cost.
        this.addAbility(new BeginningOfUpkeepTriggeredAbility(new RiverSongsDiaryCastEffect())
                .withInterveningIf(RiverSongsDiaryCondition.instance));
    }

    private RiverSongsDiary(final RiverSongsDiary card) {
        super(card);
    }

    @Override
    public RiverSongsDiary copy() {
        return new RiverSongsDiary(this);
    }
}

class RiverSongsDiaryTriggeredAbility extends TriggeredAbilityImpl {

    RiverSongsDiaryTriggeredAbility() {
        super(Zone.BATTLEFIELD, null, false);
    }

    private RiverSongsDiaryTriggeredAbility(final RiverSongsDiaryTriggeredAbility ability) {
        super(ability);
    }

    @Override
    public RiverSongsDiaryTriggeredAbility copy() {
        return new RiverSongsDiaryTriggeredAbility(this);
    }

    @Override
    public boolean checkEventType(GameEvent event, Game game) {
        return event.getType() == GameEvent.EventType.SPELL_CAST;
    }

    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
        Spell spell = game.getStack().getSpell(event.getTargetId());
        if (spell == null || !spell.isInstantOrSorcery(game) || spell.getFromZone() != Zone.HAND) {
            return false;
        }
        this.getEffects().clear();
        this.addEffect(new RiverSongsDiaryExileEffect(spell, game));
        return true;
    }

    @Override
    public String getRule() {
        return "Imprint -- Whenever a player casts an instant or sorcery spell from their hand, "
                + "exile it instead of putting it into a graveyard as it resolves.";
    }
}

class RiverSongsDiaryExileEffect extends ReplacementEffectImpl {

    private final MageObjectReference morSpell;
    private final MageObjectReference morCard;

    RiverSongsDiaryExileEffect(Spell spell, Game game) {
        super(Duration.WhileOnStack, Outcome.Benefit);
        this.morSpell = new MageObjectReference(spell.getCard(), game);
        this.morCard = new MageObjectReference(spell.getMainCard(), game);
    }

    private RiverSongsDiaryExileEffect(final RiverSongsDiaryExileEffect effect) {
        super(effect);
        this.morSpell = effect.morSpell;
        this.morCard = effect.morCard;
    }

    @Override
    public boolean checksEventType(GameEvent event, Game game) {
        return event.getType() == GameEvent.EventType.ZONE_CHANGE;
    }

    @Override
    public boolean applies(GameEvent event, Ability source, Game game) {
        ZoneChangeEvent zEvent = (ZoneChangeEvent) event;
        return zEvent.getFromZone() == Zone.STACK
                && zEvent.getToZone() == Zone.GRAVEYARD
                && morSpell.refersTo(event.getSourceId(), game)
                && morCard.refersTo(event.getTargetId(), game);
    }

    @Override
    public boolean replaceEvent(GameEvent event, Ability source, Game game) {
        Spell sourceSpell = morSpell.getSpell(game);
        if (sourceSpell == null || sourceSpell.isCopy()) {
            return false;
        }
        Player player = game.getPlayer(sourceSpell.getOwnerId());
        if (player == null) {
            return false;
        }
        player.moveCardsToExile(
                sourceSpell, source, game, false,
                CardUtil.getExileZoneId(game, source),
                CardUtil.getSourceName(game, source)
        );
        return true;
    }

    @Override
    public RiverSongsDiaryExileEffect copy() {
        return new RiverSongsDiaryExileEffect(this);
    }
}

enum RiverSongsDiaryCondition implements Condition {
    instance;

    @Override
    public boolean apply(Game game, Ability source) {
        ExileZone exileZone = game.getExile().getExileZone(CardUtil.getExileZoneId(game, source));
        return exileZone != null && exileZone.size() >= 4;
    }
}

class RiverSongsDiaryCastEffect extends OneShotEffect {

    RiverSongsDiaryCastEffect() {
        super(Outcome.Benefit);
        staticText = "choose one of them at random. You may cast it without paying its mana cost";
    }

    private RiverSongsDiaryCastEffect(final RiverSongsDiaryCastEffect effect) {
        super(effect);
    }

    @Override
    public RiverSongsDiaryCastEffect copy() {
        return new RiverSongsDiaryCastEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player player = game.getPlayer(source.getControllerId());
        ExileZone exileZone = game.getExile().getExileZone(CardUtil.getExileZoneId(game, source));
        if (player == null || exileZone == null || exileZone.isEmpty()) {
            return false;
        }
        Card card = exileZone.getRandom(game);
        return card != null && CardUtil.castSpellWithAttributesForFree(player, source, game, card);
    }
}
