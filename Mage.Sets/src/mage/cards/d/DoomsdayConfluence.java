package mage.cards.d;

import mage.abilities.Ability;
import mage.abilities.dynamicvalue.common.GetXValue;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.CreateTokenEffect;
import mage.abilities.effects.common.SacrificeAllEffect;
import mage.abilities.effects.common.discard.DiscardEachPlayerEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.choices.ChoiceImpl;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.TargetController;
import mage.filter.common.FilterCreaturePermanent;
import mage.filter.predicate.Predicates;
import mage.game.Game;
import mage.game.permanent.token.DalekToken;
import mage.players.Player;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author daveystruijk
 */
public final class DoomsdayConfluence extends CardImpl {

    public DoomsdayConfluence(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.SORCERY}, "{X}{X}{B}");

        // Choose X. You may choose the same mode more than once.
        // * Each player sacrifices a nonartifact creature of their choice.
        // * Create a 3/3 black Dalek artifact creature token with menace.
        // * Each opponent discards a card.
        this.getSpellAbility().addEffect(new DoomsdayConfluenceEffect());
    }

    private DoomsdayConfluence(final DoomsdayConfluence card) {
        super(card);
    }

    @Override
    public DoomsdayConfluence copy() {
        return new DoomsdayConfluence(this);
    }
}

class DoomsdayConfluenceEffect extends OneShotEffect {

    private static final FilterCreaturePermanent filter = new FilterCreaturePermanent("nonartifact creature");

    private static final String MODE_SACRIFICE = "Each player sacrifices a nonartifact creature of their choice";
    private static final String MODE_TOKEN = "Create a 3/3 black Dalek artifact creature token with menace";
    private static final String MODE_DISCARD = "Each opponent discards a card";

    static {
        filter.add(Predicates.not(CardType.ARTIFACT.getPredicate()));
    }

    DoomsdayConfluenceEffect() {
        super(Outcome.Benefit);
        staticText = "choose X. You may choose the same mode more than once.";
    }

    private DoomsdayConfluenceEffect(final DoomsdayConfluenceEffect effect) {
        super(effect);
    }

    @Override
    public DoomsdayConfluenceEffect copy() {
        return new DoomsdayConfluenceEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        if (controller == null) {
            return false;
        }

        int xValue = GetXValue.instance.calculate(game, source, this);
        if (xValue < 1) {
            return true;
        }

        ChoiceImpl choice = new ChoiceImpl(true);
        choice.setMessage("Choose a mode");
        Set<String> choices = new LinkedHashSet<>();
        choices.add(MODE_SACRIFICE);
        choices.add(MODE_TOKEN);
        choices.add(MODE_DISCARD);
        choice.setChoices(choices);

        for (int i = 0; i < xValue; i++) {
            if (!controller.choose(outcome, choice, game)) {
                return false;
            }

            String selectedMode = choice.getChoice();
            choice.clearChoice();
            if (MODE_SACRIFICE.equals(selectedMode)) {
                new SacrificeAllEffect(filter).apply(game, source);
                continue;
            }
            if (MODE_TOKEN.equals(selectedMode)) {
                new CreateTokenEffect(new DalekToken()).apply(game, source);
                continue;
            }
            if (MODE_DISCARD.equals(selectedMode)) {
                new DiscardEachPlayerEffect(TargetController.OPPONENT).apply(game, source);
            }
        }
        return true;
    }
}
