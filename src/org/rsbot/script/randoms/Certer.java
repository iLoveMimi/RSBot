package org.rsbot.script.randoms;

import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSComponent;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

/*
 * Certer Random event solver
 * Coding by joku.rules, Fixed by FuglyNerd
 * Interface data taken from Certer solver by Nightmares18
 */
@ScriptManifest(authors = {"joku.rules"}, name = "Certer", version = 1.0)
public class Certer extends Random {

	private final int[] MODEL_IDS = {2807, 8828, 8829, 8832, 8833, 8834, 8835,
	                                 8836, 8837};
	private final int[] bookPiles = {42352, 42354};
	private final String[] ITEM_NAMES = {"bowl", "battleaxe", "fish",
	                                     "shield", "helmet", "ring", "shears", "sword", "spade"};

	private boolean readyToLeave = false;
	private int failCount = 0;

	@Override
	public boolean activateCondition() {
		return game.isLoggedIn() && objects.getNearest(bookPiles) != null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public int loop() {
		if (!activateCondition() && readyToLeave) {
			readyToLeave = false;
			failCount = 0;
			log("I think we've solved the certer");
			return -1;
		}

		if (interfaces.getComponent(241, 4).containsText("Ahem, ")) {
			readyToLeave = false;
		}

		if (interfaces.getComponent(241, 4).containsText("Correct.")
				|| interfaces.getComponent(241, 4).containsText(
				"You can go now.")) {
			readyToLeave = true;
		}

		if (readyToLeave) {
			int PORTAL_ID = 11368;
			final RSObject portal = objects.getNearest(PORTAL_ID);
			if (portal != null) {
				final RSTile portalLocation = portal.getLocation();
				if (calc.distanceTo(portal) < 4) {
					portal.doAction("Enter");
					return random(3000, 4000);
				} else {
					walking.walkTileMM(walking.randomize(new RSTile(
							portalLocation.getX() - 1, portalLocation.getY()),
					                                     1, 1));
					return random(6000, 8000);
				}
			}
		}

		if (interfaces.getComponent(184, 0).isValid()) {
			final int modelID = interfaces.getComponent(184, 8).getComponents()[3]
					.getModelID();
			String itemName = null;
			for (int i = 0; i < MODEL_IDS.length; i++) {
				if (MODEL_IDS[i] == modelID) {
					itemName = ITEM_NAMES[i];
				}
			}

			if (itemName == null) {
				log("The object couldn't be identified! ID: " + modelID);
				failCount++;
				if (failCount > 10) {
					stopScript(false);
					return -1;
				}
				return random(1000, 2000);
			}

			for (int j = 0; j < 3; j++) {
				final RSComponent iface = interfaces.getComponent(184, 8)
				                                    .getComponents()[j];
				if (iface.containsText(itemName)) {
					iface.doClick();
					return random(3000, 5000);
				}
			}
		}

		if (interfaces.canContinue()) {
			interfaces.clickContinue();
			return random(3000, 4000);
		}

		final RSNPC certer = npcs.getNearest("Niles", "Miles", "Giles");
		if (certer != null) {
			if (calc.distanceTo(certer) < 4) {
				certer.doAction("Talk-to");
				return random(4000, 5000);
			} else {
				final RSTile certerLocation = certer.getLocation();
				walking.walkTileMM(walking.randomize(
						new RSTile(certerLocation.getX() + 2, certerLocation
								.getY()), 1, 1));
				return random(6000, 8000);
			}
		}

		failCount++;
		if (failCount > 10) {
			stopScript(false);
			return -1;
		}
		return random(1000, 2000);
	}
}