package club.sk1er.mods.mousesens;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.stream.TwitchStream;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class YSensSlider extends GuiButton {
    public boolean dragging;
    private float sliderValue;



    public YSensSlider(int p_i45017_1_, int p_i45017_2_, int p_i45017_3_) {
        super(p_i45017_1_, p_i45017_2_, p_i45017_3_, 100, 20, "");
        this.sliderValue = MouseSensitivityTweak.getMouseSensitivity();
        this.displayString = getDisplay();
    }

    /**
     * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
     * this button.
     */
    protected int getHoverState(boolean mouseOver) {
        return 0;
    }
    private float valueMin=0.0F;
    private float valueMax = 1.0F;
    private float valueStep = 0.0F;
    public float snapToStepClamp(float p_148268_1_) {
        p_148268_1_ = this.snapToStep(p_148268_1_);
        return clamp_float(p_148268_1_, this.valueMin, this.valueMax);
    }

    protected float snapToStep(float p_148264_1_) {
        if (this.valueStep > 0.0F) {
            p_148264_1_ = this.valueStep * (float) Math.round(p_148264_1_ / this.valueStep);
        }

        return p_148264_1_;
    }

    public float denormalizeValue(float p_148262_1_) {
        return this.snapToStepClamp(this.valueMin + (this.valueMax - this.valueMin) * clamp_float(p_148262_1_, 0.0F, 1.0F));
    }
    public float normalizeValue(float p_148266_1_)
    {
        return clamp_float((this.snapToStepClamp(p_148266_1_) - this.valueMin) / (this.valueMax - this.valueMin), 0.0F, 1.0F);
    }
    /**
     * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
     */
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            if (this.dragging) {
                this.sliderValue = (float) (mouseX - (this.xPosition + 4)) / (float) (this.width - 8);
                this.sliderValue = clamp_float(this.sliderValue, 0.0F, 1.0F);
                float f = denormalizeValue(this.sliderValue);
                MouseSensitivityTweak.setSensitivity(f);
                this.sliderValue = normalizeValue(f);
                this.displayString = getDisplay();
            }

            mc.getTextureManager().bindTexture(buttonTextures);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.xPosition + (int) (this.sliderValue * (float) (this.width - 8)), this.yPosition, 0, 66, 4, 20);
            this.drawTexturedModalRect(this.xPosition + (int) (this.sliderValue * (float) (this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
        }
    }

    /**
     * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
     * e).
     */
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            this.sliderValue = (float) (mouseX - (this.xPosition + 4)) / (float) (this.width - 8);
            this.sliderValue = clamp_float(this.sliderValue, 0.0F, 1.0F);
            MouseSensitivityTweak.setSensitivity(denormalizeValue(this.sliderValue));
            this.displayString = getDisplay();
            this.dragging = true;
            return true;
        } else {
            return false;
        }
    }
    public static float clamp_float(float num, float min, float max)
    {
        return num < min ? min : (num > max ? max : num);
    }

    /**
     * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
     */
    public void mouseReleased(int mouseX, int mouseY) {
        this.dragging = false;
    }

    public String getDisplay() {
        float f = sliderValue;
        String s ="Y Sensitivity: ";
        return (f == 0.0F ? s + I18n.format("options.sensitivity.min") : (f == 1.0F ? s + I18n.format("options.sensitivity.max" ) : s + (int)(f * 200.0F) + "%"));

    }
}