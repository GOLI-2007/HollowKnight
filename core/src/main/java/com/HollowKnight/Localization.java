package com.HollowKnight;

import java.util.HashMap;
import java.util.Map;

public class Localization {
    private static Localization instance;
    private String currentLanguage = "English";
    private final Map<String, Map<String, String>> translations;

    private Localization() {
        translations = new HashMap<>();

        // --- انگلیسی (پیش‌فرض) ---
        Map<String, String> en = new HashMap<>();
        en.put("title", "HOLLOW KNIGHT VOIDHEART EDITION");
        en.put("start_game", "START GAME");
        en.put("settings", "SETTINGS");
        en.put("guide", "GUIDE");
        en.put("achievements", "ACHIEVEMENTS");
        en.put("quit", "QUIT GAME");
        en.put("select_profile", "SELECT PROFILE");
        en.put("new_game", "NEW GAME");
        en.put("continue", "CONTINUE");
        en.put("clear_save", "CLEAR SAVE");
        en.put("back", "BACK");
        en.put("audio", "AUDIO");
        en.put("video", "VIDEO");
        en.put("keyboard", "KEYBOARD");
        en.put("language", "LANGUAGE");
        en.put("options", "OPTIONS");
        en.put("pause", "PAUSED");
        en.put("resume", "RESUME");
        en.put("quit_to_menu", "QUIT TO MENU");
        en.put("cheat_codes", "CHEAT CODES");
        en.put("inventory", "INVENTORY");
        en.put("equipped", "Equipped");
        en.put("click_to_equip", "Press [Click] to Equip");
        en.put("select_item", "Select an Item");
        en.put("hover_details", "Hover over an item to see its details.");
        en.put("old_nail", "Old Nail");
        en.put("old_nail_desc", "A traditional weapon of Hallownest. Its blade is blunt with age and wear.");
        en.put("focus", "Focus");
        en.put("focus_desc", "Focus collected SOUL to repair your shell and heal damage. Strike enemies to gather SOUL.");
        en.put("geo", "Geo");
        en.put("geo_desc", "The currency of Hallownest, made from fossilised shells of various forms. Can be traded for goods or used as toll in various old mechanisms.");
        en.put("controls", "CONTROLS");
        en.put("abilities", "ABILITIES");
        en.put("masks", "Masks: Your health. Lose a mask when hit.");
        en.put("soul", "Soul: Gain 11 soul per hit (max 99).");
        en.put("focus_heal", "Focus (hold {0}): Heal 1 mask (costs 33 soul, 1.5s).");
        en.put("cheats", "CHEAT CODES");
        en.put("cheat_teleport", "Ctrl+B: Teleport to Boss Arena");
        en.put("cheat_noclip", "Ctrl+N: Noclip / Spectator Mode");
        en.put("cheat_soul", "Ctrl+S: Refill Soul Vessel");
        en.put("cheat_god", "Ctrl+G: God Mode");
        en.put("cheat_emergency_heal", "Ctrl+H: Emergency Heal (when health zero)");
        en.put("cheat_insta_kill", "Ctrl+K: Insta-Kill all enemies (Extra)");
        en.put("master_volume", "MASTER VOLUME:");
        en.put("sound_volume", "SOUND VOLUME:");
        en.put("music_volume", "MUSIC VOLUME:");
        en.put("reset_defaults", "RESET DEFAULTS");
        en.put("resolution", "RESOLUTION:");
        en.put("fullscreen", "FULL SCREEN:");
        en.put("vsync", "V-SYNC:");
        en.put("particles", "PARTICLE EFFECTS:");
        en.put("blur", "BLUR QUALITY:");
        en.put("fps_cap", "FRAME RATE CAP:");
        en.put("brightness", "BRIGHTNESS");
        en.put("close", "CLOSE");
        en.put("up", "UP:");
        en.put("down", "DOWN:");
        en.put("left", "LEFT:");
        en.put("right", "RIGHT:");
        en.put("jump", "JUMP:");
        en.put("attack", "ATTACK:");
        en.put("dash", "DASH:");
        en.put("sfx_toggle", "Sound Effects");
        translations.put("English", en);

        // --- فرانسوی ---
        Map<String, String> fr = new HashMap<>();
        fr.put("title", "HOLLOW KNIGHT EDITION COEUR-DU-VIDE");
        fr.put("start_game", "COMMENCER LE JEU");
        fr.put("settings", "PARAMÈTRES");
        fr.put("guide", "GUIDE");
        fr.put("achievements", "SUCCÈS");
        fr.put("quit", "QUITTER");
        fr.put("select_profile", "SÉLECTIONNER LE PROFIL");
        fr.put("new_game", "NOUVELLE PARTIE");
        fr.put("continue", "CONTINUER");
        fr.put("clear_save", "EFFACER LA SAUVEGARDE");
        fr.put("back", "RETOUR");
        fr.put("audio", "AUDIO");
        fr.put("video", "VIDÉO");
        fr.put("keyboard", "CLAVIER");
        fr.put("language", "LANGUE");
        fr.put("options", "OPTIONS");
        fr.put("pause", "PAUSE");
        fr.put("resume", "REPRENDRE");
        fr.put("quit_to_menu", "QUITTER LE JEU");
        fr.put("cheat_codes", "CODES TRICHES");
        fr.put("inventory", "INVENTAIRE");
        fr.put("equipped", "Équipé");
        fr.put("click_to_equip", "Cliquez pour équiper");
        fr.put("select_item", "Sélectionnez un objet");
        fr.put("hover_details", "Survolez un objet pour voir les détails.");
        fr.put("old_nail", "Vieux Clou");
        fr.put("old_nail_desc", "Une arme traditionnelle de Hallownest. Sa lame est émoussée par l'âge.");
        fr.put("focus", "Concentration");
        fr.put("focus_desc", "Concentrez l'ÂME pour réparer votre coquille. Frappez les ennemis pour recueillir de l'ÂME.");
        fr.put("geo", "Géo");
        fr.put("geo_desc", "La monnaie de Hallownest, faite de coquilles fossilisées. Peut être échangée contre des biens ou utilisée comme péage.");
        fr.put("controls", "COMMANDES");
        fr.put("abilities", "CAPACITÉS");
        fr.put("masks", "Masques : votre santé. Perdez un masque en cas de coup.");
        fr.put("soul", "Âme : gagnez 11 âmes par coup (max 99).");
        fr.put("focus_heal", "Concentration (maintenez {0}) : soigne 1 masque (coûte 33 âmes, 1.5s).");
        fr.put("cheats", "CODES TRICHES");
        fr.put("cheat_teleport", "Ctrl+B : Téléportation à l'arène du boss");
        fr.put("cheat_noclip", "Ctrl+N : Mode Noclip / Spectateur");
        fr.put("cheat_soul", "Ctrl+S : Recharger l'Âme");
        fr.put("cheat_god", "Ctrl+G : Mode Dieu");
        fr.put("cheat_emergency_heal", "Ctrl+H : Soin d'urgence (quand la santé est à zéro)");
        fr.put("cheat_insta_kill", "Ctrl+K : Insta-tuer tous les ennemis (Bonus)");
        fr.put("master_volume", "VOLUME MAÎTRE :");
        fr.put("sound_volume", "VOLUME DES SONS :");
        fr.put("music_volume", "VOLUME DE LA MUSIQUE :");
        fr.put("reset_defaults", "RÉINITIALISER");
        fr.put("resolution", "RÉSOLUTION :");
        fr.put("fullscreen", "PLEIN ÉCRAN :");
        fr.put("vsync", "SYNCHRO VERTICALE :");
        fr.put("particles", "EFFETS DE PARTICULES :");
        fr.put("blur", "QUALITÉ DU FLOU :");
        fr.put("fps_cap", "LIMITE DE FPS :");
        fr.put("brightness", "LUMINOSITÉ");
        fr.put("close", "FERMER");
        fr.put("up", "HAUT :");
        fr.put("down", "BAS :");
        fr.put("left", "GAUCHE :");
        fr.put("right", "DROITE :");
        fr.put("jump", "SAUT :");
        fr.put("attack", "ATTAQUE :");
        fr.put("dash", "DASH :");
        fr.put("sfx_toggle", "Effets sonores");
        translations.put("French", fr);
    }

    public static Localization getInstance() {
        if (instance == null) instance = new Localization();
        return instance;
    }

    public String get(String key) {
        Map<String, String> langMap = translations.get(currentLanguage);
        if (langMap != null && langMap.containsKey(key)) {
            return langMap.get(key);
        }
        return key; // fallback
    }

    public String getCurrentLanguage() {
        return currentLanguage;
    }

    public void setLanguage(String language) {
        if (translations.containsKey(language)) {
            this.currentLanguage = language;
        }
    }

    public String getTitleImagePath() {
        if ("French".equals(currentLanguage)) {
            return "ui/vheart_title_french.png";
        } else {
            return "ui/vheart_title.png";
        }
    }
}
