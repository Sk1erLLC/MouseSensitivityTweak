package club.sk1er.mods.mousesens;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.ModMetadata;

import java.util.Collections;
import java.util.List;

public class MouseSensitivityTweak extends DummyModContainer {

    private static float mouseSensativity = .5F;

    public MouseSensitivityTweak() {
        super(getMetaData());
    }

    private static ModMetadata getMetaData() {
        ModMetadata metadata = new ModMetadata();
        metadata.modId = "mouse_sensitivity_tweak";
        metadata.version = "1.0";

        metadata.name = "Mouse Sensitivity Tweak";
        metadata.description = "Allows independent customization of X and Y mouse sensitivity";

        metadata.url = "";
        metadata.updateUrl = "";

        metadata.authorList = Collections.singletonList("Sk1er");
        return metadata;
    }

    public static float getMouseSensitivity() {
        return mouseSensativity;
    }

    @SuppressWarnings("unused")
    public static float getSensitivityY() {
        float v = getMouseSensitivity() * 0.1F + 0.2F;
        return v * v * v * 8.0F;
    }

    public static void applyGuiChanges(List<GuiButton> buttonList, int width, int height) {
        int max = 0;
        for (GuiButton guiButton : buttonList) {
            max = Math.max(guiButton.id, max);
            if (guiButton.id == GameSettings.Options.SENSITIVITY.ordinal()) {
                guiButton.setWidth(70);
            }
        }
        max++;
        buttonList.add(new YSensSlider(max, width / 2 - 155 + 160 + 70, height / 6 - 12 + 24 * 2));

    }

    public static void setSensitivity(float f) {
        mouseSensativity = f;
    }
}
