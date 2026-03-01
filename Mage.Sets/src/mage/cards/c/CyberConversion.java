package mage.cards.c;

import mage.MageObjectReference;
import mage.abilities.Ability;
import mage.abilities.effects.ContinuousEffectImpl;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.continuous.BecomesFaceDownCreatureEffect;
import mage.abilities.effects.common.continuous.BecomesFaceDownCreatureEffect.FaceDownType;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Layer;
import mage.constants.Outcome;
import mage.constants.SubLayer;
import mage.constants.SubType;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.target.common.TargetCreaturePermanent;
import mage.target.targetpointer.FixedTarget;

import java.util.UUID;

/**
 * @author daveystruijk
 */
public final class CyberConversion extends CardImpl {

    public CyberConversion(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.INSTANT}, "{U}{U}");

        // Turn target creature face down. It's a 2/2 Cyberman artifact creature.
        this.getSpellAbility().addEffect(new CyberConversionEffect());
        this.getSpellAbility().addTarget(new TargetCreaturePermanent());
    }

    private CyberConversion(final CyberConversion card) {
        super(card);
    }

    @Override
    public CyberConversion copy() {
        return new CyberConversion(this);
    }
}

class CyberConversionEffect extends OneShotEffect {

    CyberConversionEffect() {
        super(Outcome.Benefit);
        staticText = "turn target creature face down. It's a 2/2 Cyberman artifact creature";
    }

    private CyberConversionEffect(final CyberConversionEffect effect) {
        super(effect);
    }

    @Override
    public CyberConversionEffect copy() {
        return new CyberConversionEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent target = game.getPermanent(source.getFirstTarget());
        if (target == null) {
            return false;
        }

        if (!target.isFaceDown(game)) {
            target.turnFaceDown(source, game, source.getControllerId());
        }

        MageObjectReference mor = new MageObjectReference(target, game);
        game.addEffect(new BecomesFaceDownCreatureEffect(null, mor, Duration.Custom, FaceDownType.MANUAL), source);
        game.addEffect(new CyberConversionContinuousEffect(mor), source);
        return true;
    }
}

class CyberConversionContinuousEffect extends ContinuousEffectImpl {

    CyberConversionContinuousEffect(MageObjectReference mor) {
        super(Duration.Custom, Layer.CopyEffects_1, SubLayer.FaceDownEffects_1b, Outcome.Neutral);
        this.setTargetPointer(new FixedTarget(mor));
    }

    private CyberConversionContinuousEffect(final CyberConversionContinuousEffect effect) {
        super(effect);
    }

    @Override
    public CyberConversionContinuousEffect copy() {
        return new CyberConversionContinuousEffect(this);
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
