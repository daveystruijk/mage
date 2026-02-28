package mage.cards.e;

import mage.MageObjectReference;
import mage.abilities.Ability;
import mage.abilities.common.EntersBattlefieldTriggeredAbility;
import mage.abilities.effects.ContinuousEffectImpl;
import mage.abilities.effects.OneShotEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Layer;
import mage.constants.Outcome;
import mage.constants.SubLayer;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.target.common.TargetCreaturePermanent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author daveystruijk
 */
public final class ExchangeOfWords extends CardImpl {

    public ExchangeOfWords(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ENCHANTMENT}, "{1}{U}{U}");

        // When this enchantment enters, choose two target creatures. For as long as this enchantment remains on the battlefield, exchange the text boxes of those creatures.
        Ability ability = new EntersBattlefieldTriggeredAbility(new ExchangeOfWordsEffect());
        ability.addTarget(new TargetCreaturePermanent(2));
        this.addAbility(ability);
    }

    private ExchangeOfWords(final ExchangeOfWords card) {
        super(card);
    }

    @Override
    public ExchangeOfWords copy() {
        return new ExchangeOfWords(this);
    }
}

class ExchangeOfWordsEffect extends OneShotEffect {

    ExchangeOfWordsEffect() {
        super(Outcome.Benefit);
        staticText = "choose two target creatures. For as long as this enchantment remains on the battlefield, exchange the text boxes of those creatures";
    }

    private ExchangeOfWordsEffect(final ExchangeOfWordsEffect effect) {
        super(effect);
    }

    @Override
    public ExchangeOfWordsEffect copy() {
        return new ExchangeOfWordsEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        if (source.getSourcePermanentIfItStillExists(game) == null) {
            return false;
        }

        List<UUID> targets = source.getTargets().get(0).getTargets();
        if (targets.size() != 2) {
            return false;
        }

        Permanent firstCreature = game.getPermanent(targets.get(0));
        Permanent secondCreature = game.getPermanent(targets.get(1));
        if (firstCreature == null || secondCreature == null) {
            return false;
        }

        game.addEffect(new ExchangeOfWordsContinuousEffect(firstCreature, secondCreature, game), source);
        return true;
    }
}

class ExchangeOfWordsContinuousEffect extends ContinuousEffectImpl {

    private final MageObjectReference firstCreature;
    private final MageObjectReference secondCreature;
    private final List<Ability> firstTextBox;
    private final List<Ability> secondTextBox;

    ExchangeOfWordsContinuousEffect(Permanent firstCreature, Permanent secondCreature, Game game) {
        super(Duration.UntilSourceLeavesBattlefield, Layer.AbilityAddingRemovingEffects_6, SubLayer.NA, Outcome.Neutral);
        this.firstCreature = new MageObjectReference(firstCreature, game);
        this.secondCreature = new MageObjectReference(secondCreature, game);
        this.firstTextBox = copyAbilities(firstCreature.getAbilities(game));
        this.secondTextBox = copyAbilities(secondCreature.getAbilities(game));
        staticText = "For as long as {this} remains on the battlefield, exchange the text boxes of those creatures";
    }

    private ExchangeOfWordsContinuousEffect(final ExchangeOfWordsContinuousEffect effect) {
        super(effect);
        this.firstCreature = effect.firstCreature;
        this.secondCreature = effect.secondCreature;
        this.firstTextBox = copyAbilities(effect.firstTextBox);
        this.secondTextBox = copyAbilities(effect.secondTextBox);
    }

    @Override
    public ExchangeOfWordsContinuousEffect copy() {
        return new ExchangeOfWordsContinuousEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        exchangeTextBox(firstCreature, secondTextBox, source, game);
        exchangeTextBox(secondCreature, firstTextBox, source, game);
        return true;
    }

    private static void exchangeTextBox(MageObjectReference permanentRef, List<Ability> textBox, Ability source, Game game) {
        Permanent permanent = permanentRef.getPermanent(game);
        if (permanent == null) {
            return;
        }

        permanent.removeAllAbilities(source.getSourceId(), game);
        textBox.forEach(ability -> permanent.addAbility(ability, source.getSourceId(), game, true));
    }

    private static List<Ability> copyAbilities(List<Ability> abilities) {
        List<Ability> result = new ArrayList<>();
        abilities.forEach(ability -> result.add(ability.copy()));
        return result;
    }
}
