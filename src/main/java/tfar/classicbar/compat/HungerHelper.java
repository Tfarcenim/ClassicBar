package tfar.classicbar.compat;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import tfar.classicbar.ClassicBar;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class HungerHelper {

  static Class<?> clazz1;
  static Method m1;
  static Field f1;

  static Class<?> clazz2;
  static Field f2;

  static {
    if (ClassicBar.IBLIS) {
      try {

        clazz1 = Class.forName("iblis.player.PlayerCharacteristics");
        m1 = clazz1.getMethod("getCurrentValue", EntityLivingBase.class);
        f1 = clazz1.getField("GLUTTONY");

      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    if (ClassicBar.RANDOMTWEAKS) {
      try {
        clazz2 = Class.forName("com.therandomlabs.randomtweaks.config.RTConfig$Hunger");
        f2 = clazz2.getField("maximumHungerLevel");
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }


  private static double getMaxIblisHunger(EntityPlayer player) {
    try {
      return (double) m1.invoke(f1.get(null), player);
    } catch (Exception e) {
      e.printStackTrace();
      return 20;
    }
  }

  private static double getMaxRTHunger(EntityPlayer player) {
    try {
      return (int) f2.get(null);
    } catch (Exception e) {
      e.printStackTrace();
      return 20;
    }
  }

  public static double getMaxHunger(EntityPlayer player) {
    if (ClassicBar.RANDOMTWEAKS) return getMaxRTHunger(player);
    else if (ClassicBar.IBLIS) return getMaxIblisHunger(player);
    return Math.max(player.getFoodStats().getFoodLevel(),20);
  }

}
