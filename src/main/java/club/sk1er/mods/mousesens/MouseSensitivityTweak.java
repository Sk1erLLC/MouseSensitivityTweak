package club.sk1er.mods.mousesens;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.commons.io.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class MouseSensitivityTweak extends DummyModContainer {

    public static boolean OBF = true;
    private static float ySensativity = .5F;

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
        return ySensativity;
    }

    @SuppressWarnings("unused")
    public static float getSensitivityY() {
        float v = getMouseSensitivity() * 0.6F + 0.2F;
        return v * v * v * 8.0F;
    }

    public static void applyGuiChanges(List<GuiButton> buttonList, int width, int height) {
        int max = 0;
        for (GuiButton guiButton : buttonList) {
            max = Math.max(guiButton.id, max);
            if (guiButton.id == GameSettings.Options.SENSITIVITY.ordinal()) {
                guiButton.setWidth(100);
                guiButton.xPosition = width / 2 - 52;
            }
            if (guiButton.id == GameSettings.Options.INVERT_MOUSE.ordinal()) {
                guiButton.setWidth(100);
            }
        }
        for (GuiButton guiButton : buttonList) {
            if (guiButton.displayString.contains("Y Sensitivity: "))
                return;
        }
        max++;
        buttonList.add(new YSensSlider(max, width / 2 + 50, 18));

    }

    public static void setSensitivity(float f) {
        ySensativity = f;
    }

    @Mod.EventHandler
    @Subscribe
    public void preInit(FMLPreInitializationEvent event) {
        System.out.println("FML PREINIT EVENT");
        File suggestedConfigurationFile = event.getSuggestedConfigurationFile();
        if (suggestedConfigurationFile.exists()) {
            try {
                ySensativity = Float.parseFloat(FileUtils.readFileToString(suggestedConfigurationFile));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (!suggestedConfigurationFile.exists())
                    suggestedConfigurationFile.createNewFile();
                FileWriter fw = new FileWriter(suggestedConfigurationFile);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(Float.toString(ySensativity));
                bw.close();
                fw.close();
                System.out.println("SAVING");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }
}
