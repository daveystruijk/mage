package mage.cards.p;

import mage.abilities.Ability;
import mage.abilities.common.AsBecomesAttachedToCreatureSourceAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.costs.mana.GenericManaCost;
import mage.abilities.effects.ContinuousEffectImpl;
import mage.abilities.effects.common.ChooseACardNameEffect;
import mage.abilities.effects.common.ChooseCreatureTypeEffect;
import mage.abilities.keyword.CantBeBlockedSourceAbility;
import mage.abilities.keyword.EquipAbility;
import mage.abilities.keyword.WardAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.*;
import mage.game.Game;
import mage.game.permanent.Permanent;

import java.util.UUID;

/**
 * @author daveystruijk
 */
public final class PsychicPaper extends CardImpl {

    public PsychicPaper(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ARTIFACT}, "{2}");
        this.subtype.add(SubType.EQUIPMENT);

        // As this Equipment becomes attached to a creature, choose a creature card name and a creature type.
        AsBecomesAttachedToCreatureSourceAbility asAttachAbility = new AsBecomesAttachedToCreatureSourceAbility(
                new ChooseACardNameEffect(ChooseACardNameEffect.TypeOfName.CREATURE_NAME),
                "choose a creature card name and a creature type."
        );
        asAttachAbility.addEffect(new ChooseCreatureTypeEffect(Outcome.Benefit));
        this.addAbility(asAttachAbility);

        // Equipped creature has ward {1}, it can't be blocked, and its name and creature type
        // are the last chosen name and creature type.
        this.addAbility(new SimpleStaticAbility(new PsychicPaperEffect()));

        // Equip {2}
        this.addAbility(new EquipAbility(Outcome.Benefit, new GenericManaCost(2), false));
    }

    private PsychicPaper(final PsychicPaper card) {
        super(card);
    }

    @Override
    public PsychicPaper copy() {
        return new PsychicPaper(this);
    }
}

class PsychicPaperEffect extends ContinuousEffectImpl {

    PsychicPaperEffect() {
        super(Duration.WhileOnBattlefield, Outcome.Benefit);
        staticText = "equipped creature has ward {1}, it can't be blocked, and its name and creature type " +
                "are the last chosen name and creature type";
    }

    private PsychicPaperEffect(final PsychicPaperEffect effect) {
        super(effect);
    }

    @Override
    public PsychicPaperEffect copy() {
        return new PsychicPaperEffect(this);
    }

    @Override
    public boolean apply(Layer layer, SubLayer sublayer, Ability source, Game game) {
        Permanent equipment = source.getSourcePermanentIfItStillExists(game);
        if (equipment == null || equipment.getAttachedTo() == null) {
            return false;
        }
        Permanent equipped = game.getPermanent(equipment.getAttachedTo());
        if (equipped == null) {
            return false;
        }
        switch (layer) {
            case TextChangingEffects_3:
                // Set the equipped creature's name to the last chosen creature card name
                String chosenName = (String) game.getState().getValue(
                        source.getSourceId().toString() + ChooseACardNameEffect.INFO_KEY
                );
                if (chosenName != null) {
                    equipped.setName(chosenName);
                }
                return true;
            case TypeChangingEffects_4:
                // Replace all creature types with the last chosen creature type
                SubType chosenType = ChooseCreatureTypeEffect.getChosenCreatureType(source.getSourceId(), game);
                if (chosenType != null) {
                    equipped.removeAllCreatureTypes(game);
                    equipped.addSubType(game, chosenType);
                }
                return true;
            case AbilityAddingRemovingEffects_6:
                // Grant ward {1} and can't be blocked to the equipped creature
                equipped.addAbility(new WardAbility(new GenericManaCost(1), false), source.getSourceId(), game);
                equipped.addAbility(new CantBeBlockedSourceAbility(), source.getSourceId(), game);
                return true;
        }
        return false;
    }

    @Override
    public boolean apply(Game game, Ability source) {
        return false;
    }

    @Override
    public boolean hasLayer(Layer layer) {
        return layer == Layer.TextChangingEffects_3
                || layer == Layer.TypeChangingEffects_4
                || layer == Layer.AbilityAddingRemovingEffects_6;
    }
}
