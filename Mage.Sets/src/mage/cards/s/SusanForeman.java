package mage.cards.s;

import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.ContinuousEffect;
import mage.abilities.effects.Effect;
import mage.abilities.effects.ReplacementEffectImpl;
import mage.abilities.keyword.DoctorsCompanionAbility;
import mage.abilities.mana.GreenManaAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.Planes;
import mage.constants.SubType;
import mage.constants.SuperType;
import mage.game.Game;
import mage.game.command.CommandObject;
import mage.game.command.Plane;
import mage.game.events.GameEvent;
import mage.players.Player;

import java.util.UUID;

/**
 * @author daveystruijk
 */
public final class SusanForeman extends CardImpl {

    public SusanForeman(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{1}{G}");

        this.supertype.add(SuperType.LEGENDARY);
        this.subtype.add(SubType.TIME_LORD);
        this.power = new MageInt(1);
        this.toughness = new MageInt(1);

        // If you would planeswalk, instead look at the top two cards of your planar deck,
        // put one on the bottom of your planar deck and the other on top, then planeswalk.
        this.addAbility(new SimpleStaticAbility(new SusanForemanReplacementEffect()));

        // {T}: Add {G}.
        this.addAbility(new GreenManaAbility());

        // Doctor's companion
        this.addAbility(DoctorsCompanionAbility.getInstance());
    }

    private SusanForeman(final SusanForeman card) {
        super(card);
    }

    @Override
    public SusanForeman copy() {
        return new SusanForeman(this);
    }
}

class SusanForemanReplacementEffect extends ReplacementEffectImpl {

    private static final String KEY_PREFIX = "SusanForemanSkipPlaneswalkReplacement";

    SusanForemanReplacementEffect() {
        super(Duration.WhileOnBattlefield, Outcome.Benefit);
        staticText = "if you would planeswalk, instead look at the top two cards of your planar deck, "
                + "put one on the bottom of your planar deck and the other on top, then planeswalk";
    }

    private SusanForemanReplacementEffect(final SusanForemanReplacementEffect effect) {
        super(effect);
    }

    @Override
    public SusanForemanReplacementEffect copy() {
        return new SusanForemanReplacementEffect(this);
    }

    @Override
    public boolean checksEventType(GameEvent event, Game game) {
        return event.getType() == GameEvent.EventType.PLANESWALK;
    }

    @Override
    public boolean applies(GameEvent event, Ability source, Game game) {
        if (Boolean.TRUE.equals(game.getState().getValue(KEY_PREFIX + source.getControllerId()))) {
            return false;
        }
        Plane currentPlane = getCurrentPlane(event.getTargetId(), game);
        return currentPlane != null && source.isControlledBy(currentPlane.getControllerId());
    }

    @Override
    public boolean replaceEvent(GameEvent event, Ability source, Game game) {
        Plane currentPlane = getCurrentPlane(event.getTargetId(), game);
        if (currentPlane == null) {
            return false;
        }

        Player controller = game.getPlayer(source.getControllerId());
        if (controller == null) {
            return false;
        }

        Plane alternatePlane = getAlternatePlane(currentPlane.getName(), game);
        if (alternatePlane == null) {
            return false;
        }

        if (controller.chooseUse(
                Outcome.Benefit,
                "Keep planeswalking to " + currentPlane.getLogName() + "? "
                        + "(Choose No to planeswalk to " + alternatePlane.getLogName() + " instead)",
                source,
                game
        )) {
            return false;
        }

        removePlane(currentPlane, game);

        game.getState().setValue(KEY_PREFIX + source.getControllerId(), true);
        boolean result = game.addPlane(alternatePlane, controller.getId());
        game.getState().setValue(KEY_PREFIX + source.getControllerId(), null);
        return result;
    }

    private static Plane getCurrentPlane(UUID planeId, Game game) {
        for (CommandObject commandObject : game.getState().getCommand()) {
            if (commandObject instanceof Plane && commandObject.getId().equals(planeId)) {
                return (Plane) commandObject;
            }
        }
        return null;
    }

    private static Plane getAlternatePlane(String currentPlaneName, Game game) {
        if (game.getState().getSeenPlanes() != null && game.getState().getSeenPlanes().size() == Planes.values().length) {
            game.getState().resetSeenPlanes();
        }

        for (int i = 0; i < 100; i++) {
            Plane plane = Plane.createRandomPlane();
            if (plane != null
                    && !currentPlaneName.equals(plane.getName())
                    && !game.getState().getSeenPlanes().contains(plane.getName())) {
                return plane;
            }
        }
        for (int i = 0; i < 100; i++) {
            Plane plane = Plane.createRandomPlane();
            if (plane != null && !currentPlaneName.equals(plane.getName())) {
                return plane;
            }
        }
        return null;
    }

    private static void removePlane(Plane planeToRemove, Game game) {
        if (planeToRemove.getAbilities() != null) {
            for (Ability ability : planeToRemove.getAbilities()) {
                for (Effect effect : ability.getEffects()) {
                    if (effect instanceof ContinuousEffect) {
                        ((ContinuousEffect) effect).discard();
                    }
                }
            }
        }
        game.getState().removeTriggersOfSourceId(planeToRemove.getId());
        game.getState().getCommand().remove(planeToRemove);
    }
}
