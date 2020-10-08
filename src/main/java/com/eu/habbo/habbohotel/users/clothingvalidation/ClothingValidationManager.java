package com.eu.habbo.habbohotel.users.clothingvalidation;

import com.eu.habbo.habbohotel.users.Habbo;
import gnu.trove.TIntCollection;
import gnu.trove.set.hash.TIntHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class ClothingValidationManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClothingValidationManager.class);

    public static String FIGUREDATA_URL = "";
    public static boolean VALIDATE_ON_HC_EXPIRE = false;
    public static boolean VALIDATE_ON_LOGIN = false;
    public static boolean VALIDATE_ON_CHANGE_LOOKS = false;
    public static boolean VALIDATE_ON_MIMIC = false;
    public static boolean VALIDATE_ON_MANNEQUIN = false;
    public static boolean VALIDATE_ON_FBALLGATE = false;

    private static final Figuredata FIGUREDATA = new Figuredata();

    /**
     * Parses the new figuredata.xml file
     * @param newUrl URI of figuredata.xml file. Can be a file path or URL
     */
    public static void reloadFiguredata(String newUrl) {
        try {
            FIGUREDATA.parseXML(newUrl);
        } catch (Exception e) {
            VALIDATE_ON_HC_EXPIRE = false;
            VALIDATE_ON_LOGIN = false;
            VALIDATE_ON_CHANGE_LOOKS = false;
            VALIDATE_ON_MIMIC = false;
            VALIDATE_ON_MANNEQUIN = false;
            VALIDATE_ON_FBALLGATE = false;
            LOGGER.error("Caught exception", e);
        }
    }

    /**
     * Validates a figure string on a given user
     * @param habbo User to validate
     * @return Cleaned figure string
     */
    public static String validateLook(Habbo habbo) {
        return validateLook(habbo.getHabboInfo().getLook(), habbo.getHabboInfo().getGender().name(), habbo.getHabboStats().hasActiveClub(), habbo.getInventory().getWardrobeComponent().getClothingSets());
    }

    /**
     * Validates a given figure string and gender on a given user
     * @param habbo User to validate
     * @param look Figure string
     * @param gender Gender (M/F)
     * @return Cleaned figure string
     */
    public static String validateLook(Habbo habbo, String look, String gender) {
        return validateLook(look, gender, habbo.getHabboStats().hasActiveClub(), habbo.getInventory().getWardrobeComponent().getClothingSets());
    }

    /**
     * Validates a given figure string against a given gender
     * @param look Figure string
     * @param gender Gender (M/F)
     * @return Cleaned figure string
     */
    public static String validateLook(String look, String gender) {
        return validateLook(look, gender, false, new TIntHashSet());
    }

    /**
     * Validates a given figure string against a given gender with club clothing option
     * @param look Figure string
     * @param gender Gender (M/F)
     * @param isHC Boolean indicating if club clothing is permitted
     * @return Cleaned figure string
     */
    public static String validateLook(String look, String gender, boolean isHC) {
        return validateLook(look, gender, isHC, new TIntHashSet());
    }

    /**
     * Validates a figure string with all available options
     * @param look Figure string
     * @param gender Gender (M/F)
     * @param isHC Boolean indicating if club clothing is permitted
     * @param ownedClothing Array of owned clothing set IDs. If sellable and setId not in this array clothing will be removed
     * @return Cleaned figure string
     */
    public static String validateLook(String look, String gender, boolean isHC, TIntCollection ownedClothing) {
        if(FIGUREDATA.palettes.size() == 0 || FIGUREDATA.settypes.size() == 0)
            return look;

        String[] newLookParts = look.split(Pattern.quote("."));
        ArrayList<String> lookParts = new ArrayList<>();

        for(String lookpart : newLookParts) {
            if(lookpart.contains("-")) {
                try {
                    String[] data = lookpart.split(Pattern.quote("-"));
                    if (data.length > 1) {
                        FiguredataSettype settype = FIGUREDATA.settypes.get(data[0]);
                        if (settype == null) {
                            throw new Exception("Set type " + data[0] + " does not exist");
                        }

                        FiguredataPalette palette = FIGUREDATA.palettes.get(settype.paletteId);
                        if (palette == null) {
                            throw new Exception("Palette " + settype.paletteId + " does not exist");
                        }

                        int setId;
                        FiguredataSettypeSet set;

                        setId = Integer.parseInt(data[1]);
                        set = settype.getSet(setId);
                        if (set == null)
                            throw new Exception("Set " + setId + " does not exist in SetType");

                        if ((set.club && !isHC) || !set.selectable || (set.sellable && !ownedClothing.contains(set.id))) {
                            if(gender.equalsIgnoreCase("M") && !isHC && !settype.mandatoryMale0)
                                continue;

                            if(gender.equalsIgnoreCase("F") && !isHC && !settype.mandatoryFemale0)
                                continue;

                            if(gender.equalsIgnoreCase("M") && isHC && !settype.mandatoryMale1)
                                continue;

                            if(gender.equalsIgnoreCase("F") && isHC && !settype.mandatoryFemale1)
                                continue;

                            set = settype.getFirstNonHCSetForGender(gender);
                            setId = set.id;
                        }

                        ArrayList<String> dataParts = new ArrayList<>();

                        int color1 = -1;
                        int color2 = -1;

                        if (data.length > 2 && set.colorable) {
                            color1 = Integer.parseInt(data[2]);
                            FiguredataPaletteColor color = palette.getColor(color1);
                            if (color == null || (color.club && !isHC)) {
                                color1 = palette.getFirstNonHCColor().id;
                            }
                        }

                        if (data.length > 3 && set.colorable) {
                            color2 = Integer.parseInt(data[3]);
                            FiguredataPaletteColor color = palette.getColor(color2);
                            if (color == null || (color.club && !isHC)) {
                                color2 = palette.getFirstNonHCColor().id;
                            }
                        }

                        dataParts.add(settype.type);
                        dataParts.add("" + setId);

                        if (color1 > -1) {
                            dataParts.add("" + color1);
                        }

                        if (color2 > -1) {
                            dataParts.add("" + color2);
                        }

                        lookParts.add(String.join("-", dataParts));
                    }
                }
                catch (Exception e) {
                    //habbo.alert(e.getMessage());
                }
            }
        }

        return String.join(".", lookParts);
    }

}
