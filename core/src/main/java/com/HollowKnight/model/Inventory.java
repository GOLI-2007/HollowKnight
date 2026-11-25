package com.HollowKnight.model;

import java.util.ArrayList;
import java.util.List;

public class Inventory {

    private static Inventory instance;
    private final List<Charm> charms;
    private final Nail nail;
    private int geo;
    private int maxNotches;
    private int usedNotches;

    private Inventory() {
        maxNotches = 3;
        usedNotches = 0;

        nail = new Nail(
            "Old Nail",
            "A traditional weapon of Hallownest. Its blade is blunt with age and wear."
        );

        charms = new ArrayList<>();

        charms.add(new Charm(
            "soul_catcher",
            "Soul Catcher",
            "Used by shamans to draw more SOUL from the world around them.",
            1
        ));

        charms.add(new Charm(
            "dashmaster",
            "Dashmaster",
            "Bear the likeness of an eccentric bug known only as 'The Dashmaster'.",
            1
        ));

        charms.add(new Charm(
            "strength",
            "Unbreakable Strength",
            "Strengthens the bearer, increasing the damage they deal to enemies.",
            1
        ));

        charms.add(new Charm(
            "quick_slash",
            "Quick Slash",
            "Born from imperfect, discarded nails that have fused together.",
            1
        ));

        charms.add(new Charm(
            "quick_focus",
            "Quick Focus",
            "A charm containing a crystal lens.",
            1
        ));

        charms.add(new Charm(
            "heavy_blow",
            "Heavy Blow",
            "Formed from the nails of fallen warriors.",
            1
        ));

        charms.add(new Charm(
            "sharp_shadow",
            "Sharp Shadow",
            "Contains a forbidden spell that transforms shadows into deadly weapons.",
            1
        ));

        charms.add(new Charm(
            "void_heart",
            "Void Heart",
            "An emptiness that was hidden within.",
            1
        ));
    }

    public static Inventory getInstance() {

        if (instance == null) {
            instance = new Inventory();
        }

        return instance;
    }

    public boolean isCharmEquipped(String id) {
        for (Charm charm : charms) {
            if (charm.getId().equals(id) && charm.isEquipped()) {
                return true;
            }
        }
        return false;
    }

    public List<Charm> getCharms() {
        return charms;
    }
    public Nail getNail() {
        return nail;
    }
    public int getGeo() {
        return geo;
    }
    public void addGeo(int amount) {
        geo += amount;
    }
    public void removeGeo(int amount) {
        geo -= amount;
        if (geo < 0)
            geo = 0;
    }
    public int getMaxNotches() {
        return maxNotches;
    }
    public int getUsedNotches() {
        return usedNotches;
    }

    public boolean equipCharm(Charm charm) {
        if (charm.isEquipped())
            return false;
        if (usedNotches + charm.getNotchCost() > maxNotches)
            return false;
        charm.setEquipped(true);
        usedNotches += charm.getNotchCost();
        return true;
    }

    public boolean unequipCharm(Charm charm) {
        if (!charm.isEquipped())
            return false;
        charm.setEquipped(false);
        usedNotches -= charm.getNotchCost();
        if (usedNotches < 0)
            usedNotches = 0;
        return true;
    }
}
