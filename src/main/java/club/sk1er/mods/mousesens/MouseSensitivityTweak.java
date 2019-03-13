package club.sk1er.mods.mousesens;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.ModMetadata;

import java.util.Collections;

public class MouseSensitivityTweak extends DummyModContainer {

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
        return .1F;
    }

    public static float getSensitivityX() {
        return Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6F + 0.2F;
    }

    public static float getSensitivityY() {
        return getMouseSensitivity() * 0.6F + 0.2F;
    }

}
