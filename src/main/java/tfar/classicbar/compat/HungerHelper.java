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

  static Class<?> clazz3;
  static Field f3;
  static Object instance;

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

    if (ClassicBar.PITWEAKS) {
      try {
        clazz3 = Class.forName("us.bemrose.mc.pitweaks.TweakConfig$PlayerTweakConfig");
        instance = clazz3.newInstance();
        f3 = clazz3.getField("uncapFood");
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

  private static double getPITweaksHunger(EntityPlayer player){
    try {

      return f3.getBoolean(instance) ? player.getFoodStats().getFoodLevel() : 20;
    } catch (IllegalAccessException e){
      e.printStackTrace();
      return 20;
    }
  }

  public static double getMaxHunger(EntityPlayer player) {
    double max = 20;

    if (ClassicBar.PITWEAKS) return getPITweaksHunger(player);
    if (ClassicBar.RANDOMTWEAKS) return getMaxRTHunger(player);
    else if (ClassicBar.IBLIS) return getMaxIblisHunger(player);

    return max;
  }

}
