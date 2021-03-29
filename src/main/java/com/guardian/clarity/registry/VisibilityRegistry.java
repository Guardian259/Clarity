package com.guardian.clarity.registry;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.guardian.clarity.Clarity.ARMORVISIBILITY;
import static com.guardian.clarity.Clarity.LOG;

public class VisibilityRegistry {
    protected static final List<ArmorMaterial> materialList = new ArrayList<>();
    protected static final Map<String, float[]> VISIBILITY_MULTIPLIERS = new HashMap<>();

    //TODO: This Needs Testing, currently does not generate the correct values. my math is wrong somewhere
    /**Takes in a given ArmorMaterial and generates a corresponding array of visibility values based on the
     * ArmorMaterial's durabilityMultiplier, preset list of VISIBILITY_MULTIPLIERS, and the Armor Material's protectionAmounts
     * @param material The material used to generate visibility values
     */
    private static int[] generateValues(ArmorMaterial material) {

        float[] visibilityMulitipliers;
        int[] values = new int[4];
        //int[] protectionAmount = new int[4];
        //returns the durabilityMultiplier by undoing the multiplication done by getDurability
        int durability = (material.getDurability(EquipmentSlot.fromTypeIndex(EquipmentSlot.Type.ARMOR, 0)) / 13);

        /*pulls the corresponding multipliers based off of the material durability.
        Its not perfect but should adequately approximate for undefined visibilities*/
        if (durability >= 33) {
            visibilityMulitipliers = VISIBILITY_MULTIPLIERS.get("visible");
        } else if(durability >= 15) {
            visibilityMulitipliers = VISIBILITY_MULTIPLIERS.get("normal");
        } else{
            visibilityMulitipliers = VISIBILITY_MULTIPLIERS.get("sneaky");
        }

        /*Currently does not generate the appropriate estimations*/
        for (int i = 0; i < visibilityMulitipliers.length; i++) {
            int protectionAmount = material.getProtectionAmount(EquipmentSlot.fromTypeIndex(EquipmentSlot.Type.ARMOR, i));
            values[i] = (int)(((visibilityMulitipliers[i]/100) * protectionAmount) * durability);
        }

        return values;
    }

    /**Loops through all identified ArmorMaterials, at SERVER_STARTING and checks for any existing keys,
     * if no preexisting key is present a new ARMORVISIBILITY HashMap is registered & stored
     * @param materialList the list of ArmorMaterials identified at server startup
     */
    private static void setVisibilityValues(List<ArmorMaterial> materialList) {
        for (ArmorMaterial currentMaterial :
                materialList) {
            if (!ARMORVISIBILITY.containsKey(currentMaterial)) {
                register(currentMaterial, generateValues(currentMaterial));
            }
        }
    }

    /**Registers Armor Visibility Values in the ARMORVISIBILITY HashMap. This is used to determine
     * the players nameplate visibility, as well as, the players visibility to mobs
     * @param armorMaterial the armorMaterial to be associated with the visibility array
     * @param array the int[] array containing the four armor visibility values
     */
    public static void register(ArmorMaterial armorMaterial, int[] array) {
        ARMORVISIBILITY.put(armorMaterial, array);
    }

    /**Runs at ServerLifecycleEvents.SERVER_STARTING; first places preset VISIBILITY_MULTIPLIERS
     * then iterates through all loaded items, identifying instances of ArmorItem,
     * and storing all ArmorMaterials in an ArrayList; Will then run setVisibilityValues for said ArrayList to determine
     * whether or not to generate approximations
     * @see VisibilityRegistry#setVisibilityValues(java.util.List)
     */
    public static void init() {
        VISIBILITY_MULTIPLIERS.put("visible", new float[]{17.86F, 32.00F, 38.00F, 12.50F});
        VISIBILITY_MULTIPLIERS.put("normal", new float[]{18.75F, 31.00F, 38.00F, 12.50F});
        VISIBILITY_MULTIPLIERS.put("sneaky", new float[]{12.50F,25.00F, 50.00F, 12.50F});

        LOG.info("Generating Armor Visibility Values");
        Registry.ITEM.forEach(item -> {
            if (item instanceof ArmorItem) {
                materialList.add(((ArmorItem) item).getMaterial());
            }
        });
        //will generate approximations based on all ArmorMaterials if no visibilityValues are present
        setVisibilityValues(materialList);

        LOG.info("Generation Success");
    }
}
