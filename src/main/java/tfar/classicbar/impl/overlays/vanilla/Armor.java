package tfar.classicbar.impl.overlays.vanilla;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.api.BarSide;
import tfar.classicbar.api.BarType;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.BarInfo;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.api.Color;
import tfar.classicbar.util.ModUtils;

public class Armor extends BarOverlayImpl {

    private static final EquipmentSlot[] armorList = new EquipmentSlot[]{EquipmentSlot.HEAD,
            EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    public static final BarInfo INFO = BarInfo.createSimpleVanilla("armor",
            player -> calculateArmorValue(player) >= 1,Armor::calculateArmorValue,fixed(20f));

    public Armor(BarSettings barSettings) {
        super(INFO,barSettings);
    }

    public static final Codec<Armor> CODEC = RecordCodecBuilder.create(
            objectInstance -> codecStart(objectInstance)
                    .apply(objectInstance,Armor::new)
    );

    @Override
    public Codec<? extends BarOverlayImpl> codec() {
        return CODEC;
    }

    @Override
    public void renderBar(ForgeGui gui, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        double armor = calculateArmorValue(player);
        double barWidth = getBarWidth(player, 0);

        int xStart = screenWidth / 2 + getHOffset();

        if (getSide() == BarSide.RIGHT) {
            xStart += WIDTH - barWidth;
        }

        int yStart = screenHeight - vOffset;
        //bar background
        renderBarBackground(graphics, player, screenWidth, screenHeight, vOffset);
        //how many type are there? remember to start at 0
        int index = (int) Math.min(Math.ceil(armor / 20), ConfigCache.armor.size()) - 1;
        Color primary = getPrimaryBarColor(index);

        if (index == 0) {
            //calculate bar color
            //draw portion of bar based on armor
            renderPartialBar(primary,graphics, xStart + 2, yStart + 2, barWidth);
        } else {
            //we have wrapped, draw 2 bars
            //draw first bar
            //case 1: bar is not capped and is partially filled
            if (armor % 20 != 0) {
                Color secondary = getSecondaryBarColor(index - 1);
                //draw complete first bar
                renderFullBar(secondary, graphics, xStart + 2, yStart + 2);
                //draw partial second bar
                double w = ModUtils.getWidth(armor % 20, 20);
                double f = xStart + (getSide() == BarSide.RIGHT ? WIDTH - w : 0);
                renderPartialBar(primary,graphics, f + 2, yStart + 2, w);
            }
            //case 2, bar is a multiple of 20, or it is capped
            else {
                //draw complete second bar
                renderFullBar(primary, graphics, xStart + 2, yStart + 2);
            }
        }
    }

    @Override
    public void renderBarDecorations(ForgeGui gui, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {

    }

    public Color getPrimaryBarColor(int index) {
        return ConfigCache.armor.get(index);
    }

    public Color getSecondaryBarColor(int index) {
        return ConfigCache.armor.get(index);
    }

    public static int getDamagedAmount(Player player) {
        int warningAmount = 0;
        for (EquipmentSlot slot : armorList) {
            ItemStack stack = player.getItemBySlot(slot);
            if (!(stack.getItem() instanceof ArmorItem)) continue;
            int max = stack.getMaxDamage();
            int current = stack.getDamageValue();
            int percentage = 100;
            if (max != 0) percentage = 100 * (max - current) / (max);
            if (percentage < 5) {
                warningAmount += ((ArmorItem) stack.getItem()).getDefense();
            }
        }
        return warningAmount;
    }

    @Override
    public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        double armor = calculateArmorValue(player);
        //draw armor amount
        int index = (int) Math.min(Math.ceil(armor / 20), ConfigCache.armor.size()) - 1;
        int c = getPrimaryBarColor(index).colorToText();
        textHelper(graphics, xStart, yStart, armor, c);
    }

    @Override
    public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        //Draw armor icon
        ModUtils.drawTexturedModalRect(getIconRL(),graphics, xStart, yStart, 43, 9, 9, 9);
    }

    private static float calculateArmorValue(Player player) {
        return player.getArmorValue();
    }
}