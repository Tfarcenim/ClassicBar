package tfar.classicbar.overlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tfar.classicbar.Color;
import tfar.classicbar.compat.HungerHelper;

import static tfar.classicbar.ColorUtils.calculateScaledColor;
import static tfar.classicbar.ColorUtils.hex2Color;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.config.ModConfig.*;

public class OverlaySuperclass {

  private final Minecraft mc = Minecraft.getMinecraft();

  private float foodAlpha = 0;
  private boolean foodIncrease = true;

  private int updateCounter = 0;
  private double playerHealth = 0;
  private long healthUpdateCounter = 0;
  private double lastPlayerHealth = 0;
  private double displayHealth = 0;
  private int alpha;

  long lastSystemTime;

  private double mountHealth = 0;

  private float armorAlpha = 1;
  private static EntityEquipmentSlot[] armorList = new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD,
          EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};

  @SubscribeEvent
  public void renderBars(RenderGameOverlayEvent.Pre event) {
    Entity entity = mc.getRenderViewEntity();
    if (!(entity instanceof EntityPlayer)) return;
    EntityPlayer player = (EntityPlayer) entity;
    int scaledWidth = event.getResolution().getScaledWidth();
    int scaledHeight = event.getResolution().getScaledHeight();
    switch (event.getType()) {

      //this draws first
      case HEALTH: {
        event.setCanceled(true);
        updateCounter = mc.ingameGUI.getUpdateCounter();

        double health = player.getHealth();
        boolean highlight = healthUpdateCounter > (long) updateCounter && (healthUpdateCounter - (long) updateCounter) / 3 % 2 == 1;

        //player is damaged and resistant
        if (health < playerHealth && player.hurtResistantTime > 0) {
          healthUpdateCounter = (long) (updateCounter + 20);
          lastPlayerHealth = playerHealth;
        } else if (health > playerHealth && player.hurtResistantTime > 0) {
          healthUpdateCounter = (long) (updateCounter + 10);
          /* lastPlayerHealth = playerHealth;*/
        }
        double absorb = player.getAbsorptionAmount();
        playerHealth = health;
        displayHealth = health + (lastPlayerHealth - health) * ((double) player.hurtResistantTime / player.maxHurtResistantTime);

        IAttributeInstance maxHealthAttribute = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        int xStart = scaledWidth / 2 - 91;
        int yStart = scaledHeight - GuiIngameForge.left_height;
        GuiIngameForge.left_height +=10;
        double maxHealth = maxHealthAttribute.getAttributeValue();

        mc.profiler.startSection("health");
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        int k5 = 16;

        if (player.isPotionActive(MobEffects.POISON)) k5 += 36;//evaluates to 52
        else if (player.isPotionActive(MobEffects.WITHER)) k5 += 72;//evaluates to 88

        int i4 = (highlight) ? 18 : 0;

        //Bind our Custom bar
        mc.getTextureManager().bindTexture(ICON_BAR);
        //Bar background
        drawTexturedModalRect(xStart, yStart, 0, i4, 81, 9);

        //is the bar changing
        //Pass 1, draw bar portion
        alpha = health <= 0 ? 1 : health / maxHealth <= general.overlays.lowHealthThreshold && general.overlays.lowHealthWarning ?
                (int) (Minecraft.getSystemTime() / 250) % 2 : 1;

        //interpolate the bar
        if (displayHealth != health) {
          //reset to white
          GlStateManager.color(1, 1, 1, alpha);
          if (displayHealth > health) {
            //draw interpolation
            drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, getWidth(displayHealth, maxHealth), 7);
            //Health is increasing, idk what to do here
          } else {/*
                  f = xStart + getWidth(health, maxHealth);
                  drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(health - displayHealth, maxHealth), 7, general.style, true, true);*/
          }
        }

        //calculate bar color

        calculateScaledColor(health, maxHealth, k5).color2Gla(alpha);
        //draw portion of bar based on health remaining
        drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, getWidth(health, maxHealth), 7);


        if (k5 == 52) {
          //draw poison overlay
          GlStateManager.color(0, .5f, 0, .5f);
          drawTexturedModalRect(xStart + 1, yStart + 1, 1, 36, getWidth(health, maxHealth), 7);
        }
        //draw absorption bar if it exists
        if (absorb > 0) {
          GuiIngameForge.right_height += 10;
          int index = (int) Math.ceil(absorb / maxHealth) - 1;
         // if (general.overlays.swap) yStart -= 10;
          Color.reset();
          //no wrapping
          if (absorb <= maxHealth) {
            if (!general.overlays.fullAbsorptionBar) drawScaledBar(absorb, maxHealth, xStart, yStart - 9, true);
            else drawTexturedModalRect(xStart, yStart - 10, 0, 0, 81, 9);

            switch (k5) {
              case 16: {
                hex2Color(colors.advancedColors.absorptionColorValues[0]).color2Gl();
                break;
              }
              case 52: {
                hex2Color(colors.advancedColors.absorptionPoisonColorValues[0]).color2Gl();
                break;
              }
              case 88: {
                hex2Color(colors.advancedColors.absorptionWitherColorValues[0]).color2Gl();
                break;
              }
            }

            drawTexturedModalRect(xStart + 1, yStart - 9, 1, 10, getWidth(absorb, maxHealth), 7);
          } else {
            //draw background bar
            drawTexturedModalRect(xStart, yStart - 10, 0, 0, 81, 9);
            //we have wrapped, draw 2 bars
            //don't crash from arrayindexoutofbounds
            if (index >= colors.advancedColors.absorptionColorValues.length - 1)
              index = colors.advancedColors.absorptionColorValues.length - 1;
            //draw first full bar
            switch (k5) {
              case 16: {
                hex2Color(colors.advancedColors.absorptionColorValues[index]).color2Gl();
                break;
              }
              case 52: {
                hex2Color(colors.advancedColors.absorptionPoisonColorValues[index]).color2Gl();
                break;
              }
              case 88: {
                hex2Color(colors.advancedColors.absorptionWitherColorValues[index]).color2Gl();
                break;
              }
            }
            drawTexturedModalRect(xStart + 1, yStart - 9, 1, 10, 79, 7);
            //is it on the edge or capped already?
            if (absorb % maxHealth != 0 && index < colors.advancedColors.absorptionColorValues.length - 1) {
              //draw second partial bar
              switch (k5) {
                case 16: {
                  hex2Color(colors.advancedColors.absorptionColorValues[index]).color2Gl();
                  break;
                }
                case 52: {
                  hex2Color(colors.advancedColors.absorptionPoisonColorValues[index]).color2Gl();
                  break;
                }
                case 88: {
                  hex2Color(colors.advancedColors.absorptionWitherColorValues[index]).color2Gl();
                  break;
                }
              }
              drawTexturedModalRect(xStart + 1, yStart - 9, 1, 10, getWidth(absorb % maxHealth, maxHealth), 7);
            }
          }
          // handle the text
          int a1 = getStringLength((int) absorb + "");
          int a2 = general.displayIcons ? 1 : 0;
          int a3 = (int) absorb;
          int c = 0;

          switch (k5) {
            case 16: {
              c = hex2Color(colors.advancedColors.absorptionColorValues[index]).colorToText();
              break;
            }
            case 52: {
              c = hex2Color(colors.advancedColors.absorptionPoisonColorValues[index]).colorToText();
              break;
            }
            case 88: {
              c = hex2Color(colors.advancedColors.absorptionWitherColorValues[index]).colorToText();
              break;
            }
          }

          drawStringOnHUD(a3 + "", xStart - a1 - 9 * a2 - 5, yStart - 11, c);
          if (general.overlays.swap) yStart += 10;
        }
        int h1 = (int) Math.round(health);
        int i2 = general.displayIcons ? 1 : 0;
        if (numbers.showPercent) h1 = (int) (100 * health / maxHealth);
        int i1 = getStringLength(h1 + "");

        if (numbers.showHealthNumbers)
          drawStringOnHUD(h1 + "", xStart - 9 * i2 - i1 + leftTextOffset, yStart - 1, calculateScaledColor(health, maxHealth, k5).colorToText());

        //Reset back to normal settings
        Color.reset();

        mc.getTextureManager().bindTexture(ICON_VANILLA);
        if (absorb > 0) {
          GuiIngameForge.left_height += 10;
        }

        if (general.displayIcons) {
          int i5 = (player.world.getWorldInfo().isHardcoreModeEnabled()) ? 5 : 0;
          //Draw health icon
          //heart background
          drawTexturedModalRect(xStart - 10, yStart, 16, 9 * i5, 9, 9);
          //heart
          drawTexturedModalRect(xStart - 10, yStart, 36 + k5, 9 * i5, 9, 9);
          if (absorb > 0) {
            if (general.overlays.swap) yStart -= 10;
            //draw absorption icon
            drawTexturedModalRect(xStart - 10, yStart - 10, 16, 9 * i5, 9, 9);
            drawTexturedModalRect(xStart - 10, yStart - 10, 160, 0, 9, 9);
          }
        }
        //Reset back to normal settings

        GlStateManager.disableBlend();
        //Revert our state back
        GlStateManager.popMatrix();
        mc.profiler.endSection();
      }break;

      //this draws second
      case ARMOR: {
        //armor stuff
        event.setCanceled(true);
        double armor = calculateArmorValue();
        if (armor < 1) return;
        boolean warning = false;
        int warningAmount = 0;
        for (EntityEquipmentSlot slot : armorList) {
          if (!general.overlays.lowArmorWarning) break;
          ItemStack stack = player.getItemStackFromSlot(slot);
          int max = stack.getMaxDamage();
          int current = stack.getItemDamage();
          int percentage = 100;
          if (max != 0) percentage = 100 * (max - current) / (max);
          if (percentage < 5) {
            if (!(stack.getItem() instanceof ItemArmor)) continue;
            warning = true;
            warningAmount += ((ItemArmor) stack.getItem()).getArmorMaterial().getDamageReductionAmount(slot);
          }
        }

        //Push to avoid lasting changes
        if (warning && general.overlays.lowArmorWarning) armorAlpha = (int) (Minecraft.getSystemTime() / 250) % 2;
        if (general.overlays.swap) {
        }
        int xStart = scaledWidth / 2 - 91;
        int yStart = scaledHeight - GuiIngameForge.left_height;
        GuiIngameForge.left_height +=10;
        mc.profiler.startSection("armor");
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();

        //Bind our Custom bar
        mc.getTextureManager().bindTexture(ICON_BAR);
        //Bar background

        //Pass 1, draw bar portion
        //how many layers are there? remember to start at 0
        int index = (int) Math.min(Math.ceil(armor / 20) - 1, colors.advancedColors.armorColorValues.length - 1);

        armor -= warningAmount;
        //if armor >20
        if (armor + warningAmount <= 20) {
          //bar background
          if (!general.overlays.fullArmorBar) drawScaledBar(armor + warningAmount, 20, xStart, yStart + 1, true);
          else drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9);
          //calculate bar color
          hex2Color(colors.advancedColors.armorColorValues[0]).color2Gl();
          //draw portion of bar based on armor
          drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, getWidth(armor, 20), 7);

          //draw damaged bar
          hex2Color(colors.advancedColors.armorColorValues[0]).color2Gla(armorAlpha);
          drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, getWidth(armor + warningAmount, 20), 7);
        } else {
          //we have wrapped, draw 2 bars
          //bar background
          drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9);

          //draw first bar
          //case 1: bar is not capped and is partially filled
          if (warningAmount != 0 || index < colors.advancedColors.armorColorValues.length && (armor + warningAmount) % 20 != 0) {
            //draw complete first bar
            hex2Color(colors.advancedColors.armorColorValues[index - 1]).color2Gl();
            drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, 79, 7);

            //draw partial second bar
            hex2Color(colors.advancedColors.armorColorValues[index]).color2Gl();
            drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, getWidth(armor % 20, 20), 7);
          }
          //case 2, bar is a multiple of 20 or it is capped
          else {
            //draw complete second bar
            hex2Color(colors.advancedColors.armorColorValues[index]).color2Gl();
            drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, 79, 7);
          }
          // now handle the low armor warning
          if (warningAmount > 0) {
            //armor and armor warning on same index
            if ((int) Math.ceil((warningAmount + armor) / 20) == (int) Math.ceil(armor / 20)) {
              //draw one bar
              hex2Color(colors.advancedColors.armorColorValues[index]).color2Gla(armorAlpha);
              drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, getWidth(armor + warningAmount - index * 20, 20), 7);
            }
          }
        }
        //draw armor amount
        int i1 = (int) Math.floor(armor + warningAmount);
        int i3 = (general.displayIcons) ? 1 : 0;
        int c = Integer.decode(colors.advancedColors.armorColorValues[index]);
        if (numbers.showPercent) i1 = (int) (armor + warningAmount) * 5;
        int i2 = getStringLength(i1 + "");
        if (numbers.showArmorNumbers) drawStringOnHUD(i1 + "", xStart - 9 * i3 - i2 + leftTextOffset, yStart - 1, c);
        //Reset back to normal settings

        Color.reset();

        mc.getTextureManager().bindTexture(ICON_VANILLA);

        if (general.displayIcons)
          //Draw armor icon
          drawTexturedModalRect(xStart - 10, yStart, 43, 9, 9, 9);

        //armor icon
        GlStateManager.disableBlend();
        //Revert our state back
        GlStateManager.popMatrix();
        mc.profiler.endSection();

        //armor toughness

      }break;

      //this draws third
      case FOOD: {
        event.setCanceled(true);
        if (player.getRidingEntity() == null) {
          double hunger = player.getFoodStats().getFoodLevel();
          double maxHunger = HungerHelper.getMaxHunger(player);
          double currentSat = player.getFoodStats().getSaturationLevel();
          float exhaustion = getExhaustion(player);
          //Push to avoid lasting changes
          int xStart = scaledWidth / 2 + 10;
          int yStart = scaledHeight - GuiIngameForge.right_height;
          GuiIngameForge.right_height += 10;

          mc.profiler.startSection("hunger");
          GlStateManager.pushMatrix();
          GlStateManager.enableBlend();
          float alpha2 = hunger / maxHunger <= general.overlays.lowHungerThreshold && general.overlays.lowHungerWarning ? (int) (Minecraft.getSystemTime() / 250) % 2 : 1;

          //Bind our Custom bar
          mc.getTextureManager().bindTexture(ICON_BAR);
          //Bar background
          Color.reset();
          drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9);
          //draw portion of bar based on hunger amount
          float f = xStart + 79 - getWidth(hunger, maxHunger);
          boolean flag = player.isPotionActive(MobEffects.HUNGER);
          hex2Color(flag ? colors.hungerBarDebuffColor : colors.hungerBarColor).color2Gla(alpha2);
          drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(hunger, maxHunger), 7);
          if (currentSat > 0 && general.overlays.hunger.showSaturationBar) {
            //draw saturation
            hex2Color(flag ? colors.saturationBarDebuffColor : colors.saturationBarColor).color2Gla(alpha2);
            f += getWidth(hunger, maxHunger) - getWidth(currentSat, maxHunger);
            drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(currentSat, maxHunger), 7);
          }
          //render held hunger overlay
          if (general.overlays.hunger.showHeldFoodOverlay &&
                  player.getHeldItemMainhand().getItem() instanceof ItemFood) {
            ItemStack stack = player.getHeldItemMainhand();
            if (foodIncrease) foodAlpha += general.overlays.hunger.transitionSpeed;
            else foodAlpha -= general.overlays.hunger.transitionSpeed;
            if (foodAlpha >= 1) foodIncrease = false;
            else if (foodAlpha <= 0) foodIncrease = true;
            ItemFood foodItem = ((ItemFood) stack.getItem());
            double hungerOverlay = foodItem.getHealAmount(stack);
            double saturationMultiplier = foodItem.getSaturationModifier(stack);
            double potentialSat = 2 * hungerOverlay * saturationMultiplier;

            //Draw Potential hunger
            double hungerWidth = Math.min(maxHunger - hunger, hungerOverlay);
            //don't render the bar at all if hunger is full
            if (hunger < maxHunger) {
              f = xStart - getWidth(hungerWidth + hunger, maxHunger) + 78;
              hex2Color(flag ? colors.hungerBarDebuffColor : colors.hungerBarColor).color2Gla(foodAlpha);
              drawTexturedModalRect(f + 1, yStart + 1, 1, 10, getWidth(hunger + hungerOverlay, maxHunger), 7);
            }

            //Draw Potential saturation
            if (general.overlays.hunger.showSaturationBar) {
              //maximum potential saturation cannot combine with current saturation to go over 20
              double saturationWidth = Math.min(potentialSat, maxHunger - currentSat);

              //Potential Saturation cannot go over potential hunger + current hunger combined
              saturationWidth = Math.min(saturationWidth, hunger + hungerWidth);
              saturationWidth = Math.min(saturationWidth, hungerOverlay + hunger);
              if ((potentialSat + currentSat) > (hunger + hungerWidth)) {
                double diff = (potentialSat + currentSat) - (hunger + hungerWidth);
                saturationWidth = potentialSat - diff;
              }
              //offset used to decide where to place the bar
              f = xStart - getWidth(saturationWidth + currentSat, maxHunger) + 78;
              hex2Color(flag ? colors.saturationBarDebuffColor : colors.saturationBarColor).color2Gla(foodAlpha);
              if (true)//currentSat > 0)
                drawTexturedModalRect(f + 1, yStart + 1, 1, 10, getWidth(saturationWidth + currentSat, maxHunger), 7);
              else ;//drawTexturedModalRect(f, yStart+1, 1, 10, getWidthfloor(saturationWidth,20), 7);

            }
          }

          if (general.overlays.hunger.showExhaustionOverlay) {
            exhaustion = Math.min(exhaustion, 4);
            f = xStart - getWidth(exhaustion, 4) + 80;
            //draw exhaustion
            GlStateManager.color(1, 1, 1, .25f);
            drawTexturedModalRect(f, yStart + 1, 1, 28, getWidth(exhaustion, 4f), 9);
          }

          //draw hunger amount
          int h1 = (int) Math.floor(hunger);

          int i3 = general.displayIcons ? 1 : 0;
          if (numbers.showPercent) h1 = (int) hunger * 5;
          int c = Integer.decode(flag ? colors.hungerBarDebuffColor : colors.hungerBarColor);
          if (numbers.showHungerNumbers) drawStringOnHUD(h1 + "", xStart + 9 * i3 + rightTextOffset, yStart - 1, c);

          //Reset back to normal settings
          Color.reset();

          mc.getTextureManager().bindTexture(ICON_VANILLA);

          if (general.displayIcons) {

            int k5 = 52;
            int k6 = 16;
            if (flag) {
              k5 += 36;
              k6 = k5 + 45;
            }
            //Draw hunger icon
            //hunger background
            drawTexturedModalRect(xStart + 82, yStart, k6, 27, 9, 9);

            //hunger
            drawTexturedModalRect(xStart + 82, yStart, k5, 27, 9, 9);
          }
          GlStateManager.disableBlend();
          //Revert our state back
          GlStateManager.popMatrix();
          mc.profiler.endSection();
        }

        if (general.overlays.displayToughnessBar) {
          //armor toughness stuff
          double armorToughness = player.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue();
          if (armorToughness >= 1) {
            //Push to avoid lasting changes
            int xStart = scaledWidth / 2 + 10;
            int yStart = scaledHeight - GuiIngameForge.right_height;
            GuiIngameForge.right_height += 10;
            mc.profiler.startSection("armortoughness");
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();

            //Bind our Custom bar
            mc.getTextureManager().bindTexture(ICON_BAR);
            int f;

            //Bar background
            if (general.displayIcons)
              //Draw armor toughness icon
              drawTexturedModalRect(xStart + 82, yStart, 83, 0, 9, 9);
            //draw bar portion
            int toughnessindex = (int) Math.min(Math.ceil(armorToughness / 20) - 1, colors.advancedColors.armorToughnessColorValues.length - 1);

            if (armorToughness <= 20) {
              f = xStart + 79 - getWidth(armorToughness, 20);
              if (!general.overlays.fullToughnessBar) drawScaledBar(armorToughness, 20, f - 1, yStart, false);
              else drawTexturedModalRect(f, yStart, 0, 0, 81, 9);

              //calculate bar color
              hex2Color(colors.advancedColors.armorToughnessColorValues[0]).color2Gl();
              //draw portion of bar based on armor toughness amount
              drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(armorToughness, 20), 7);

            } else {
              drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9);
              //we have wrapped, draw 2 bars
              int size = colors.advancedColors.armorToughnessColorValues.length;
              //if we are out of colors wrap the bar
              if (toughnessindex < size && armorToughness % 20 != 0) {

                //draw complete first bar
                hex2Color(colors.advancedColors.armorToughnessColorValues[toughnessindex - 1]).color2Gl();
                drawTexturedModalRect(xStart, yStart + 1, 0, 10, 79, 7);

                //draw partial second bar
                f = xStart + 79 - getWidth(armorToughness % 20, 20);

                hex2Color(colors.advancedColors.armorToughnessColorValues[toughnessindex]).color2Gl();
                drawTexturedModalRect(f, yStart + 1, 0, 10, getWidth(armorToughness % 20, 20), 7);
              }
              //case 2, bar is a multiple of 20 or it is capped
              else {
                //draw complete second bar
                hex2Color(colors.advancedColors.armorToughnessColorValues[toughnessindex]).color2Gl();
                drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, 79, 7);
              }
            }

            //draw armor toughness amount
            int iq1 = (int) Math.floor(armorToughness);
            int iq2 = (general.displayIcons) ? 1 : 0;

            int toughnesscolor = Integer.decode(colors.advancedColors.armorToughnessColorValues[toughnessindex]);
            if (numbers.showPercent) iq1 = (int) armorToughness * 5;
            if (numbers.showArmorToughnessNumbers)
              drawStringOnHUD(iq1 + "", xStart + 9 * iq2 + rightTextOffset, yStart - 1, toughnesscolor);
            //Reset back to normal settings

            mc.getTextureManager().bindTexture(ICON_VANILLA);


            //Revert our state back
            GlStateManager.popMatrix();
            mc.profiler.endSection();
          }
        }
      }break;

      case HEALTHMOUNT: {
        //Push to avoid lasting changes
        updateCounter = mc.ingameGUI.getUpdateCounter();
        if (!(player.getRidingEntity() instanceof EntityLivingBase)) return;

        EntityLivingBase mount = (EntityLivingBase) player.getRidingEntity();
        if (mount.isDead) return;
        event.setCanceled(true);
        double mountHealth = mount.getHealth();

        boolean highlight = healthUpdateCounter > (long) updateCounter && (healthUpdateCounter - (long) updateCounter) / 3L % 2L == 1L;

        if (mountHealth < this.mountHealth && player.hurtResistantTime > 0) {
          lastSystemTime = Minecraft.getSystemTime();
          healthUpdateCounter = (long) (updateCounter + 20);
        } else if (mountHealth > this.mountHealth && player.hurtResistantTime > 0) {
          lastSystemTime = Minecraft.getSystemTime();
          healthUpdateCounter = (long) (updateCounter + 10);
        }

        this.mountHealth = mountHealth;
        IAttributeInstance maxHealthAttribute = mount.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        int xStart = scaledWidth / 2 + 10;
        int yStart = scaledHeight - 39;
        double maxHealth = maxHealthAttribute.getAttributeValue();

        mc.profiler.startSection("mountHealth");
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        int i4 = (highlight) ? 18 : 0;


        //Bind our Custom bar
        mc.getTextureManager().bindTexture(ICON_BAR);
        //Bar background
        drawTexturedModalRect(xStart, yStart, 0, i4, 81, 9);

        //is the bar changing
        //Pass 1, draw bar portion

        //calculate bar color
        calculateScaledColor(mountHealth, maxHealth, 16).color2Gl();
        float f = xStart + 79 - getWidth(mountHealth, maxHealth);
        //draw portion of bar based on mountHealth remaining
        drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(mountHealth, maxHealth), 7);
        //draw mountHealth amount
        int h1 = (int) Math.ceil(mountHealth);

        int i3 = general.displayIcons ? 1 : 0;
        if (numbers.showPercent) h1 = (int) (100 * mountHealth / maxHealth);
        if (numbers.showMountHealthNumbers)
          drawStringOnHUD(h1 + "", xStart + 9 * i3 + rightTextOffset, yStart - 1, calculateScaledColor(mountHealth, maxHealth, 16).colorToText());

        //Reset back to normal settings
        Color.reset();

        mc.getTextureManager().bindTexture(ICON_VANILLA);

        if (general.displayIcons) {
          //Draw mountHealth icon
          //heart background
          drawTexturedModalRect(xStart + 82, yStart, 16, 0, 9, 9);
          //heart
          drawTexturedModalRect(xStart + 82, yStart, 88, 9, 9, 9);

        }

        //Reset back to normal settings

        GlStateManager.disableBlend();
        //Revert our state back
        GlStateManager.popMatrix();
        mc.profiler.endSection();
      }break;

      case AIR: {
        event.setCanceled(true);
        int air = player.getAir();
        if (air >= 300) return;
        //Push to avoid lasting changes

        int xStart = scaledWidth / 2 + 10;
        int yStart = scaledHeight - GuiIngameForge.right_height;
        GuiIngameForge.right_height +=10;

        mc.profiler.startSection("air");
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();

        //Bind our Custom bar
        mc.getTextureManager().bindTexture(ICON_BAR);
        //Bar background
        drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9);

        //draw portion of bar based on air amount

        float f = xStart + 79 - getWidth(air, 300);
        hex2Color(colors.oxygenBarColor).color2Gl();
        drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(air, 300), 7);

        //draw air amount
        int h1 = (int) Math.floor(air / 20);

        int c = Integer.decode(colors.oxygenBarColor);
        int i3 = general.displayIcons ? 1 : 0;
        if (numbers.showPercent) h1 = air / 3;
        if (numbers.showOxygenNumbers)
          drawStringOnHUD(h1 + "", xStart + 9 * i3 + rightTextOffset, yStart - 1, c);
        //Reset back to normal settings
        Color.reset();
        mc.getTextureManager().bindTexture(ICON_VANILLA);
        if (general.displayIcons) {
          //Draw air icon
          drawTexturedModalRect(xStart + 82, yStart, 16, 18, 9, 9);
        }
        GlStateManager.disableBlend();
        //Revert our state back
        GlStateManager.popMatrix();
        mc.profiler.endSection();
      }break;
    }
  }

  private int calculateArmorValue() {
    int currentArmorValue = mc.player.getTotalArmorValue();

    for (ItemStack itemStack : mc.player.getArmorInventoryList()) {
      if (itemStack.getItem() instanceof ISpecialArmor) {
        ISpecialArmor specialArmor = (ISpecialArmor) itemStack.getItem();
        currentArmorValue += specialArmor.getArmorDisplay(mc.player, itemStack, 0);
      }
    }
    return currentArmorValue;
  }
}