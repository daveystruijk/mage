package mage.cards.r;

import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.ContinuousEffect;
import mage.abilities.effects.ContinuousEffectImpl;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.LoseLifeSourceControllerEffect;
import mage.abilities.effects.common.asthought.PlayFromNotOwnHandZoneTargetEffect;
import mage.abilities.keyword.ConspireAbility;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.*;
import mage.filter.StaticFilters;
import mage.game.Game;
import mage.game.stack.Spell;
import mage.game.stack.StackObject;
import mage.players.Player;
import mage.target.targetpointer.FixedTarget;
import mage.abilities.triggers.BeginningOfUpkeepTriggeredAbility;
import mage.util.CardUtil;

import java.util.UUID;

/**
 * @author daveystruijk
 */
public final class RassilonTheWarPresident extends CardImpl {

    public RassilonTheWarPresident(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{3}{U}{B}");

        this.supertype.add(SuperType.LEGENDARY);
        this.subtype.add(SubType.TIME_LORD);
        this.subtype.add(SubType.NOBLE);
        this.power = new MageInt(3);
        this.toughness = new MageInt(4);

        // At the beginning of your upkeep, you lose 2 life and exile the top card of your library.
        // You may play that card for as long as it remains exiled.
        Ability ability = new BeginningOfUpkeepTriggeredAbility(new LoseLifeSourceControllerEffect(2));
        ability.addEffect(new RassilonTheWarPresidentExileEffect().concatBy("and"));
        this.addAbility(ability);

        // Each noncreature spell you cast from exile has conspire.
        this.addAbility(new SimpleStaticAbility(new RassilonTheWarPresidentConspireEffect()));
    }

    private RassilonTheWarPresident(final RassilonTheWarPresident card) {
        super(card);
    }

    @Override
    public RassilonTheWarPresident copy() {
        return new RassilonTheWarPresident(this);
    }
}

class RassilonTheWarPresidentExileEffect extends OneShotEffect {

    RassilonTheWarPresidentExileEffect() {
        super(Outcome.Benefit);
        staticText = "exile the top card of your library. You may play that card for as long as it remains exiled";
    }

    private RassilonTheWarPresidentExileEffect(final RassilonTheWarPresidentExileEffect effect) {
        super(effect);
    }

    @Override
    public RassilonTheWarPresidentExileEffect copy() {
        return new RassilonTheWarPresidentExileEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        if (controller == null || !controller.getLibrary().hasCards()) {
            return false;
        }

        Card card = controller.getLibrary().getFromTop(game);
        if (card == null) {
            return false;
        }

        String keyForPlayer = "Shared::EndOfGame::PlayerMayPlay=" + controller.getId();
        UUID exileId = CardUtil.getExileZoneId(keyForPlayer, game);
        String exileName = controller.getName() + " may play for as long as cards remains exiled";

        if (controller.moveCardsToExile(card, source, game, true, exileId, exileName)) {
            ContinuousEffect effect = new PlayFromNotOwnHandZoneTargetEffect(Duration.EndOfGame);
            effect.setTargetPointer(new FixedTarget(card, game));
            game.addEffect(effect, source);
            return true;
        }

        return false;
    }
}

class RassilonTheWarPresidentConspireEffect extends ContinuousEffectImpl {

    private final ConspireAbility conspireAbility;

    RassilonTheWarPresidentConspireEffect() {
        super(Duration.WhileOnBattlefield, Layer.AbilityAddingRemovingEffects_6, SubLayer.NA, Outcome.AddAbility);
        staticText = "Each noncreature spell you cast from exile has conspire. "
                + "<i>(As you cast that spell, you may tap two untapped creatures you control that share a color with it. "
                + "When you do, copy it and you may choose new targets for the copy.)</i>";
        this.conspireAbility = new ConspireAbility(ConspireAbility.ConspireTargets.MORE);
    }

    private RassilonTheWarPresidentConspireEffect(final RassilonTheWarPresidentConspireEffect effect) {
        super(effect);
        this.conspireAbility = effect.conspireAbility;
    }

    @Override
    public RassilonTheWarPresidentConspireEffect copy() {
        return new RassilonTheWarPresidentConspireEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        for (StackObject stackObject : game.getStack()) {
            if (!(stackObject instanceof Spell) || stackObject.isCopy() || !stackObject.isControlledBy(source.getControllerId())) {
                continue;
            }

            Spell spell = (Spell) stackObject;
            if (spell.getFromZone() != Zone.EXILED || !StaticFilters.FILTER_SPELL_NON_CREATURE.match(spell, game)) {
                continue;
            }

            game.getState().addOtherAbility(spell.getCard(), conspireAbility.setAddedById(source.getSourceId()));
        }

        return true;
    }
}
