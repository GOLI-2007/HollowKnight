package com.HollowKnight.audio;

import com.HollowKnight.model.Settings;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AudioManager {
    private static AudioManager instance;

    private Music menuMusic;
    private Music gameMusic;
    private Sound clickSound;
    private Music currentMusic;
    private Sound attackSound;
    private Sound confirmSound;
    private Sound heroDamage;
    private Sound heroDeath;
    private Sound heroJump;
    private Sound heroLand;
    private Sound heroRun;
    private long runSoundId = -1;
    private boolean isRunSoundPlaying = false;
    private Sound geoDropSound;
    private Sound geoCollectSound;
    private Sound heroDash;
    private Sound brokenWallSound;
    private Sound soulPickupSound;
    private Sound soulTotemSlashSound;
    private Music bossMusic;
    private Sound bossStunSound;
    private Sound bossFinalHitSound;
    private Sound winSound;
    private Sound failSound;

    private Settings settings;

    private AudioManager() {
        settings = Settings.getInstance();
        loadAssets();
    }

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    private void loadAssets() {
        clickSound = Gdx.audio.newSound(Gdx.files.internal("music/Click_sound.mp3"));
        attackSound = Gdx.audio.newSound(Gdx.files.internal("music/hero_evade.wav"));
        confirmSound = Gdx.audio.newSound(Gdx.files.internal("music/ui_button_confirm.wav"));
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("music/Enter_Hallownest.mp3"));
        menuMusic.setLooping(true);
        heroDamage = Gdx.audio.newSound(Gdx.files.internal("music/hero_damage.wav"));
        heroDeath = Gdx.audio.newSound(Gdx.files.internal("music/hero_death_v2.wav"));
        heroJump = Gdx.audio.newSound(Gdx.files.internal("music/hero_jump.wav"));
        heroLand = Gdx.audio.newSound(Gdx.files.internal("music/hero_land_hard.wav"));
        heroRun = Gdx.audio.newSound(Gdx.files.internal("music/hero_run_footsteps_bones.wav"));
        geoDropSound = Gdx.audio.newSound(Gdx.files.internal("music/geo1.mp3"));
        geoCollectSound = Gdx.audio.newSound(Gdx.files.internal("music/geo2.mp3"));
        heroDash = Gdx.audio.newSound(Gdx.files.internal("music/hero_dash.wav"));
        brokenWallSound = Gdx.audio.newSound(Gdx.files.internal("music/broken_wall.wav"));
        soulPickupSound = Gdx.audio.newSound(Gdx.files.internal("music/soul_pickup_5.wav"));
        soulTotemSlashSound = Gdx.audio.newSound(Gdx.files.internal("music/soul_totem_slash.wav"));
        bossMusic = Gdx.audio.newMusic(Gdx.files.internal("music/boss battle 1.wav"));
        bossMusic.setLooping(true);
        bossStunSound = Gdx.audio.newSound(Gdx.files.internal("music/boss_stun.wav"));
        bossFinalHitSound = Gdx.audio.newSound(Gdx.files.internal("music/boss_final_hit.wav"));
        winSound = Gdx.audio.newSound(Gdx.files.internal("music/Win Sound Effect.mp3"));
        failSound = Gdx.audio.newSound(Gdx.files.internal("music/Fail Sound Effect.mp3"));
        try {
            gameMusic = Gdx.audio.newMusic(Gdx.files.internal("music/Game_music.mp3"));
        } catch (Exception e) {
            gameMusic = menuMusic; // fallback
        }
        gameMusic.setLooping(true);

        updateVolume();
    }

    public void playMenuMusic() {
        if (currentMusic == menuMusic && menuMusic.isPlaying()) return;
        stopCurrentMusic();
        menuMusic.play();
        currentMusic = menuMusic;
    }

    public void playGameMusic() {
        if (currentMusic == gameMusic && gameMusic.isPlaying()) return;
        stopCurrentMusic();
        gameMusic.play();
        currentMusic = gameMusic;
    }

    private void stopCurrentMusic() {
        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.stop();
        }
    }

    public void stopAllMusic() {
        if (menuMusic != null && menuMusic.isPlaying()) menuMusic.stop();
        if (gameMusic != null && gameMusic.isPlaying()) gameMusic.stop();
        if (bossMusic != null && bossMusic.isPlaying()) bossMusic.stop();
        currentMusic = null;
    }

    public void updateVolume() {
        float vol = settings.getMusicVolume();
        if (menuMusic != null) menuMusic.setVolume(vol);
        if (gameMusic != null) gameMusic.setVolume(vol);
    }

    public void playClick() {
        if (settings.isSfxOn() && clickSound != null) {
            clickSound.play(settings.getSoundVolume());
        }
    }

    public void playAttackSound() {
        if (settings.isSfxOn() && attackSound != null) {
            attackSound.play(settings.getSoundVolume());
        }
    }

    public void playConfirmSound() {
        if (settings.isSfxOn() && confirmSound != null) {
            confirmSound.play(settings.getSoundVolume());
        }
    }

    public void playHeroDamage() { if (settings.isSfxOn()) heroDamage.play(settings.getSoundVolume()); }
    public void playHeroDeath() { if (settings.isSfxOn()) heroDeath.play(settings.getSoundVolume()); }
    public void playHeroJump() { if (settings.isSfxOn()) heroJump.play(settings.getSoundVolume()); }
    public void playHeroLand() { if (settings.isSfxOn()) heroLand.play(settings.getSoundVolume()); }

    public void playHeroRun() {
        if (settings.isSfxOn() && !isRunSoundPlaying) {
            runSoundId = heroRun.loop(settings.getSoundVolume());
            isRunSoundPlaying = true;
        }
    }

    public void stopHeroRun() {
        if (isRunSoundPlaying) {
            heroRun.stop();
            isRunSoundPlaying = false;
            runSoundId = -1;
        }
    }

    public void playGeoDrop() {
        if (settings.isSfxOn() && geoDropSound != null) {
            geoDropSound.play(settings.getSoundVolume());
        }
    }

    public void playGeoCollect() {
        if (settings.isSfxOn() && geoCollectSound != null) {
            geoCollectSound.play(settings.getSoundVolume());
        }
    }

    public void playHeroDash() {
        if (settings.isSfxOn() && heroDash != null) {
            heroDash.play(settings.getSoundVolume());
        }
    }

    public void playBrokenWall() {
        if (settings.isSfxOn() && brokenWallSound != null) {
            brokenWallSound.play(settings.getSoundVolume());
        }
    }

    public void playSoulPickup() {
        if (settings.isSfxOn() && soulPickupSound != null) {
            soulPickupSound.play(settings.getSoundVolume());
        }
    }

    public void playSoulTotemSlash() {
        if (settings.isSfxOn() && soulTotemSlashSound != null) {
            soulTotemSlashSound.play(settings.getSoundVolume());
        }
    }

    public void playBossMusic() {
        stopCurrentMusic();
        bossMusic.setVolume(settings.getMusicVolume());
        bossMusic.play();
        currentMusic = bossMusic;
    }

    public void playBossStun() {
        if (settings.isSfxOn()) bossStunSound.play(settings.getSoundVolume());
    }

    public void playBossFinalHit() {
        if (settings.isSfxOn()) bossFinalHitSound.play(settings.getSoundVolume());
    }

    public void playWinSound() {
        if (settings.isSfxOn() && winSound != null) {
            stopAllMusic();
            winSound.play(settings.getSoundVolume());
        }
    }

    public void playFailSound() {
        if (settings.isSfxOn() && failSound != null) {
            stopAllMusic();
            failSound.play(settings.getSoundVolume());
        }
    }

    public void dispose() {
        if (menuMusic != null) menuMusic.dispose();
        if (gameMusic != null && gameMusic != menuMusic) gameMusic.dispose();
        if (clickSound != null) clickSound.dispose();
        if (confirmSound != null) confirmSound.dispose();
        if (attackSound != null) attackSound.dispose();
        if (geoDropSound != null) geoDropSound.dispose();
        if (geoCollectSound != null) geoCollectSound.dispose();
        if (heroDash != null) heroDash.dispose();
        if (brokenWallSound != null) brokenWallSound.dispose();
        if (soulPickupSound != null) soulPickupSound.dispose();
        if (soulTotemSlashSound != null) soulTotemSlashSound.dispose();
        if (bossMusic != null) bossMusic.dispose();
        if (bossStunSound != null) bossStunSound.dispose();
        if (bossFinalHitSound != null) bossFinalHitSound.dispose();
        if (winSound != null) winSound.dispose();
        if (failSound != null) failSound.dispose();
    }
}
