package dungeoncreator.utils;

// Source : https://github.com/pascallj/mineshot-revived

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.settings.PointOfView;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_5;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;


@EventBusSubscriber(bus = EventBusSubscriber.Bus.FORGE)
public class OrthoViewHandler implements PrivateAccessor {
	private static final Minecraft MC = Minecraft.getInstance();
	private static final String KEY_CATEGORY = "key.categories.mineshotrevived";
	private static final float ZOOM_STEP = 0.5f;
	private static final float ROTATE_STEP = 15;
	private static final float ROTATE_SPEED = 4;
	private static final float SECONDS_PER_TICK = 1f / 20f;

	private final KeyBinding keyToggle = new KeyBinding("key.dungeoncreator.ortho.toggle", GLFW_KEY_KP_5,
			KEY_CATEGORY);

	private boolean enabled;
	private boolean freeCam;
	private boolean clip;

	private float zoom;
	private float xRot;
	private float yRot;

	private PointOfView thirdPersonView;

	private static OrthoViewHandler instance = null;

	public static OrthoViewHandler getInstance() {
		if(instance == null)
			instance = new OrthoViewHandler();

		return instance;
	}

	private OrthoViewHandler() {
		ClientRegistry.registerKeyBinding(keyToggle);
		reset();
	}

	private void reset() {
		freeCam = false;
		clip = false;

		zoom = 9.5f;
		xRot = 50;
		yRot = -45;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void enable() {

		if (!enabled) {
			/*
			 * clippingEnabled = clippingHelper.isEnabled();
			 * clippingHelper.setEnabled(false);
			 */
			reset();
		}

		enabled = true;
	}

	public void disable() {
		if (enabled) {
			/* clippingHelper.setEnabled(clippingEnabled); */
		}

		enabled = false;
	}

	public void toggle() {
		if (isEnabled()) {
			MC.gameSettings.setPointOfView(PointOfView.FIRST_PERSON);
			disable();
		} else {
			MC.gameSettings.setPointOfView(PointOfView.THIRD_PERSON_BACK);
			enable();
		}
	}


	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent evt) {
		boolean mod = false;

		// change perspectives, using modifier key for opposite sides
		if (keyToggle.isKeyDown()) {
			mod = true;
			toggle();
		}

		// update stepped rotation/zoom controls
		// note: the smooth controls are handled in onFogDensity, since they need to be
		// executed on every frame
		if (mod) {
			// snap values to step units
			xRot = Math.round(xRot / ROTATE_STEP) * ROTATE_STEP;
			yRot = Math.round(yRot / ROTATE_STEP) * ROTATE_STEP;
			zoom = Math.round(zoom / ZOOM_STEP) * ZOOM_STEP;
		}
	}

	@SubscribeEvent
	public void onClientTickEvent(final ClientTickEvent event) {
		if (!enabled || event.phase != Phase.START) {
			return;
		}
	}

	@SubscribeEvent
	public void onRenderTickStart(RenderTickEvent evt) {
		if (!enabled || evt.phase != Phase.START) {
			return;
		}

		if (!freeCam) {
			// Turn off thirdPersonView off temporary
			thirdPersonView = MC.gameSettings.getPointOfView();
			MC.gameSettings.setPointOfView(PointOfView.FIRST_PERSON);
		}

	}

	@SubscribeEvent
	public void cameraSetup(CameraSetup event) {
		if (!enabled) {
			return;
		}

		if (!freeCam) {
			// Execute the last part of ActiveRenderInfo->update (but don't care about the
			// renderViewEntity.isSleeping part) because we have overridden it by turning
			// thirdPersonView off temporarily.
			// However this time with our camera angles instead of the entity's.
			// We also don't need to distinguish between thirdPerson and thirdPersonReverse
			// cameras
			setDirection(MC, yRot + 180.0F, xRot);

			if (thirdPersonView != PointOfView.FIRST_PERSON) {
				movePosition(MC, -calcCameraDistance(MC, 4.0D), 0.0D, 0.0D);

				// Make sure the player is rendered for this frame (side effect of temporarily
				// disabling thirdPersonView)
				setThirdPerson(MC, true);
			}

			// Set thirdPersonView back to what it was
			MC.gameSettings.setPointOfView(thirdPersonView);

			event.setPitch(xRot);
			event.setYaw(yRot + 180);
		}
	}

	@SubscribeEvent
	public void onFogDensity(EntityViewRenderEvent.FogDensity evt) {
		if (!enabled) {
			return;
		}

		float width = zoom
				* (MC.getMainWindow().getFramebufferWidth() / (float) MC.getMainWindow().getFramebufferHeight());
		float height = zoom;

		// override projection matrix
		RenderSystem.matrixMode(GL_PROJECTION);
		RenderSystem.loadIdentity();

		RenderSystem.ortho(-width, width, -height, height, clip ? 0 : -9999, 9999);
	}
}
