package com.HollowKnight.model.world;

import com.HollowKnight.GameAssetManager;
import com.HollowKnight.model.AchievementManager;
import com.HollowKnight.model.enemies.*;
import com.HollowKnight.model.player.Player;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Array;


public class World {
    private ArrayList<Platform> platforms;
    public float respawnX, respawnY;
    private int currentRoom = 0;
    public Rectangle spikesArea;

    private Player player;

    private GameAssetManager assetManager;

    private Texture bgTexture;
    private Texture groundTile;
    private Texture wallTile, wallTop, brokenWall;
    private TextureRegion flippedWallTop, flippedBrokenWall;

    private Texture benchTexture, brokenBenchTexture, soulTotemTexture, fungTextre0, spikesTexture, breakeTexture;
    private Texture lampTexture, ruinLampTexture, lampCaveTexture02, lampCaveTexture01; // لامپ
    private Texture breakWallTex, spike3, cliffsLamp;
    private ArrayList<BreakableWall> breakableWalls = new ArrayList<BreakableWall>();

    private Animation<TextureRegion> cocoonAnim;
    private float cocoonTimer = 0;
    private Rectangle cocoonBounds;
    private boolean isCocoonActive = true;
    private Texture consumedCocoonTex;

    private Texture bgBossTexture, pillarTex;
    public float roomWidth = 1280f;

    private ArrayList<Enemy> enemies;
    private ArrayList<Enemy> room1Enemies, room2Enemies, room3Enemies, room4Enemies, room5Enemies, room6Enemies;


    private ArrayList<GeoItem> geoItems = new ArrayList<>();
    private Texture geoTexture;

    public Rectangle soulTotemBounds;
    private boolean soulTotemUsed = false;

    public boolean room3WallDestroyed = false;

    private Texture soulParticleTex;
    private boolean totemHasSoul = true;
    private float totemFadeAlpha = 1f;
    private ArrayList<TotemParticle> totemParticles = new ArrayList<>();
    private int totemHits = 3;

    public float screenShakeTime = 0f;

    private boolean bossSpawned = false;
    private HuskHornhead triggerEnemy;

    public boolean isBossDefeated = false;

    public World(GameAssetManager assetManager, Player player) {
        this.assetManager = assetManager;
        this.player = player;

        platforms = new ArrayList<>();
        breakableWalls = new ArrayList<>();
        enemies = new ArrayList<>();
        geoTexture = new Texture("ui/geo.png");

        bgTexture = assetManager.getManager().get("map/startRoom/bg_room.png", Texture.class);
        groundTile = assetManager.getManager().get("map/startRoom/ground_tile.png", Texture.class);
        wallTile = assetManager.getManager().get("map/startRoom/wall_tile.png", Texture.class);
        wallTop = assetManager.getManager().get("map/startRoom/wall_top.png", Texture.class);
        flippedWallTop = new TextureRegion(wallTop);
        flippedWallTop.flip(true, false);

        brokenWall = assetManager.getManager().get("map/startRoom/broken_wall.png", Texture.class);
        flippedBrokenWall = new TextureRegion(brokenWall); // قرینه سازی
        flippedBrokenWall.flip(true, false);

        brokenBenchTexture = assetManager.getManager().get("map/startRoom/broken_bench.png", Texture.class);
        benchTexture = assetManager.getManager().get("map/startRoom/bench.png", Texture.class);
        soulTotemTexture = assetManager.getManager().get("map/startRoom/soul_totem.png", Texture.class);
        soulParticleTex = new Texture("map/startRoom/credits flash_round.png");
        for (int i = 0; i < 15; i++) {
            totemParticles.add(new TotemParticle(500 + (float)Math.random() * 120, 200 + (float)Math.random() * 150));
        }

        lampTexture = assetManager.getManager().get("map/startRoom/lamp.png", Texture.class);
        ruinLampTexture = assetManager.getManager().get("map/startRoom/ruin_lamp.png", Texture.class);
        fungTextre0 = assetManager.getManager().get("map/startRoom/fung0.png", Texture.class);

        spikesTexture = assetManager.getManager().get("map/room2/spike2.png", Texture.class);
        breakeTexture = assetManager.getManager().get("map/room2/break.png", Texture.class);
        lampCaveTexture01 = assetManager.getManager().get("map/room2/lamp_cave_01.png", Texture.class);
        lampCaveTexture02 = assetManager.getManager().get("map/room2/lamp_cave_02.png", Texture.class);

        breakWallTex = assetManager.getManager().get("map/room3/breakable_wall.png", Texture.class);
        spike3 = assetManager.getManager().get("map/room3/spike3.png", Texture.class);
        cliffsLamp = assetManager.getManager().get("map/room3/cliffs_lamp.png", Texture.class);
        consumedCocoonTex = assetManager.getManager().get("map/room4/cocoon_base_01.png", Texture.class);

        bgBossTexture = assetManager.getManager().get("map/room5/bg_room5.png", Texture.class);
        pillarTex = assetManager.getManager().get("map/room5/pillar.png", Texture.class);

        initAllEnemies();

        buildRoom(1, assetManager);

        Array<TextureRegion> cocoonFrames = new Array<>();
        for(int i = 7; i <= 22; i++) {
            String num = String.format("%03d", i);
            Texture tex = assetManager.getManager().get("map/room4/health_plant_020" + num + ".png", Texture.class);
            cocoonFrames.add(new TextureRegion(tex));
        }
        cocoonAnim = new Animation<>(0.1f, cocoonFrames, Animation.PlayMode.LOOP);
    }

    class TotemParticle {
        float x, y, speedY, alpha;
        TotemParticle(float x, float y) {
            this.x = x;
            this.y = y;
            this.speedY = (float) (Math.random() * 15 + 10);
            this.alpha = (float) Math.random();
        }
    }

    public void buildRoom(int roomNumber, GameAssetManager assetManager) {
        if (this.currentRoom == 1) {
            room1Enemies.clear();
            room1Enemies.addAll(enemies);
        } else if (this.currentRoom == 2) {
            room2Enemies.clear();
            room2Enemies.addAll(enemies);
        } else if (this.currentRoom == 3) {
            room3Enemies.clear();
            room3Enemies.addAll(enemies);
        } else if (this.currentRoom == 4) {
        room4Enemies.clear();
        room4Enemies.addAll(enemies);
    } else if (this.currentRoom == 6) {
        room6Enemies.clear();
        room6Enemies.addAll(enemies);
    }

        this.currentRoom = roomNumber;
        platforms.clear();
        enemies.clear();
        geoItems.clear();
        spikesArea = null;
        roomWidth = 1280f;

        if (roomNumber == 1) {
            Platform ground = new Platform(0, 0, 1280, 64);
            ground.setTexture(groundTile);
            platforms.add(ground);

            Platform ceiling = new Platform(0, 736, 1280, 64);
            ceiling.setTexture(groundTile);
            platforms.add(ceiling);

            int tileWidth = 93;
            int tileHeight = 255;
            int overlapY = 5;
            int offsetX = -20;

            Platform wallSegment1 = new Platform(offsetX, 64, tileWidth, tileHeight);
            wallSegment1.setTexture(wallTile);
            platforms.add(wallSegment1);

            int segment2Height = 215;
            int segment2Y = 64 + tileHeight - overlapY;
            Platform wallSegment2 = new Platform(offsetX, segment2Y, tileWidth, segment2Height);
            wallSegment2.setTexture(wallTile);
            platforms.add(wallSegment2);

            int wallTopY = segment2Y + segment2Height - overlapY;
            Platform topCurve = new Platform(offsetX, wallTopY, 206, 422);
            topCurve.setTexture(wallTop);
            platforms.add(topCurve);

            float rightTopCurveX = 1094;
            float rightTopCurveY = 524;
            float rightTopCurveWidth = 206;
            float rightTopCurveHeight = 422;
            Platform rightTopCurve = new Platform(rightTopCurveX, rightTopCurveY, rightTopCurveWidth, rightTopCurveHeight);
            platforms.add(rightTopCurve);

            float rightBrokenWallWidth = 90;
            float rightBrokenWallHeight = 260;
            float rightBrokenWallX = 1195;
            float rightBrokenWallY = rightTopCurveY - rightBrokenWallHeight + 15;
            Platform rightBrokenWall = new Platform(rightBrokenWallX, rightBrokenWallY, rightBrokenWallWidth, rightBrokenWallHeight);
            rightBrokenWall.setTexture(brokenWall);
            platforms.add(rightBrokenWall);

            enemies.addAll(room1Enemies);

            respawnX = 100;
            respawnY = 150;
        }
        else if (roomNumber == 2) {
            Platform groundLeft = new Platform(0, 0, 400, 64);
            groundLeft.setTexture(groundTile);
            platforms.add(groundLeft);

            spikesArea = new Rectangle(400, 0, 480, 20);

            Platform groundRight = new Platform(880, 0, 400, 64);
            groundRight.setTexture(groundTile);
            platforms.add(groundRight);

            Platform float1 = new Platform(360, 110, 270, 75);
            float1.setTexture(breakeTexture);
            platforms.add(float1);

            Platform float2 = new Platform(700, 220, 270, 75);
            float2.setTexture(breakeTexture);
            platforms.add(float2);

            Platform ceiling = new Platform(0, 736, 1280, 64);
            ceiling.setTexture(groundTile);
            platforms.add(ceiling);

            addHangingWallsToRoom();

            enemies.addAll(room2Enemies);

            respawnX = 100;
            respawnY = 150;
        }

        else if (roomNumber == 3) {
            breakableWalls.clear();

            Platform groundLeft = new Platform(0, 0, 300, 64);
            groundLeft.setTexture(groundTile);
            platforms.add(groundLeft);

            Platform groundRightPart1 = new Platform(800, 0, 150, 64);
            groundRightPart1.setTexture(groundTile);
            platforms.add(groundRightPart1);


            Platform groundRightPart2 = new Platform(1050, 0, 230, 64);
            groundRightPart2.setTexture(groundTile);
            platforms.add(groundRightPart2);

            Platform platLeft = new Platform(250, 110, 270, 75);
            platLeft.setTexture(breakeTexture);
            platforms.add(platLeft);

            Platform platMid = new Platform(590, 220, 130, 75);
            platMid.setTexture(breakeTexture);
            platforms.add(platMid);

            if (!room3WallDestroyed) {
                BreakableWall bWall = new BreakableWall(935, 64, 110, 115, 3);
                bWall.setTexture(breakWallTex);
                breakableWalls.add(bWall);
                platforms.add(bWall);
            }


            Platform ceiling = new Platform(0, 736, 1280, 64);
            ceiling.setTexture(groundTile);
            platforms.add(ceiling);

            spikesArea = new Rectangle(300, 0, 500, 30);

            addHangingWallsToRoom();

            enemies.addAll(room3Enemies);

            respawnX = 50;
            respawnY = 150;

        } else if (roomNumber == 4) {
            breakableWalls.clear();

            Platform ground = new Platform(0, 0, 1280, 64);
            ground.setTexture(groundTile);
            platforms.add(ground);

            Platform ceiling = new Platform(0, 736, 1280, 64);
            ceiling.setTexture(groundTile);
            platforms.add(ceiling);

            addHangingWallsToRoom();

            cocoonBounds = new Rectangle(850, 64, 80, 120);
            isCocoonActive = true;

            enemies.addAll(room4Enemies);

            respawnX = 550;
            respawnY = 150;
        }
        else if (roomNumber == 5) {
            roomWidth = 1920f;

            // زمین و سقف
            Platform ground = new Platform(0, 0, roomWidth, 64);
            ground.setTexture(groundTile);
            platforms.add(ground);

            Platform ceiling = new Platform(0, 736, roomWidth, 64);
            ceiling.setTexture(groundTile);
            platforms.add(ceiling);

            //  دیوار سمت چپ
            Platform leftWall = new Platform(-20, 64, 100, 672);
            leftWall.setTexture(wallTile);
            platforms.add(leftWall);

            Platform leftTopCurve = new Platform(-20, 524, 206, 422);
            leftTopCurve.setTexture(wallTop);
            platforms.add(leftTopCurve);

            //  دیوار سمت راست
            Platform rightWall = new Platform(roomWidth - 80, 64, 100, 672);
            rightWall.setTexture(wallTile);
            platforms.add(rightWall);

            Platform rightTopCurve = new Platform(roomWidth - 186, 524, 206, 422);
            platforms.add(rightTopCurve);

            // --- اضافه کردن سکوهای معلق برای اتاق باس ---
            Platform bossPlat1 = new Platform(350, 220, 250, 40);
            bossPlat1.setTexture(breakeTexture);
            platforms.add(bossPlat1);

            Platform bossPlat2 = new Platform(1300, 220, 250, 40);
            bossPlat2.setTexture(breakeTexture);
            platforms.add(bossPlat2);

            Platform bossPlat3 = new Platform(800, 380, 300, 40);
            bossPlat3.setTexture(breakeTexture);
            platforms.add(bossPlat3);

            enemies.addAll(room5Enemies);

            respawnX = 150;
            respawnY = 150;
        }

        else if (roomNumber == 6) {
            roomWidth = 1280f;
            breakableWalls.clear();

            AchievementManager.getInstance().unlock("explorer");

            // کف اتاق
            Platform ground = new Platform(0, 0, roomWidth, 64);
            ground.setTexture(groundTile);
            platforms.add(ground);

            // سقف با یک سوراخ
            Platform ceilingLeft = new Platform(0, 736, 900, 64);
            ceilingLeft.setTexture(groundTile);
            platforms.add(ceilingLeft);

            Platform ceilingRight = new Platform(1100, 736, 180, 64);
            ceilingRight.setTexture(groundTile);
            platforms.add(ceilingRight);

            // دیوار بسته سمت چپ (مثل روم 5)
            Platform leftWall = new Platform(-20, 64, 100, 672);
            leftWall.setTexture(wallTile);
            platforms.add(leftWall);

            Platform leftTopCurve = new Platform(-20, 524, 206, 422);
            leftTopCurve.setTexture(wallTop);
            platforms.add(leftTopCurve);

            // دیوار بسته سمت راست
            Platform rightWall = new Platform(roomWidth - 80, 64, 100, 672);
            rightWall.setTexture(wallTile);
            platforms.add(rightWall);

            Platform step1 = new Platform(400, 180, 130, 40);
            step1.setTexture(breakeTexture);
            platforms.add(step1);

            Platform step2 = new Platform(550, 280, 130, 40);
            step2.setTexture(breakeTexture);
            platforms.add(step2);

            Platform step3 = new Platform(700, 380, 130, 40);
            step3.setTexture(breakeTexture);
            platforms.add(step3);

            Platform step4 = new Platform(850, 480, 130, 40);
            step4.setTexture(breakeTexture);
            platforms.add(step4);

            Platform step5 = new Platform(1000, 580, 130, 40);
            step5.setTexture(breakeTexture);
            platforms.add(step5);

            Platform step6 = new Platform(850, 680, 130, 40);
            step6.setTexture(breakeTexture);
            platforms.add(step6);

            // مجسمه روح در کف اتاق
            soulTotemBounds = new Rectangle(750, 64, 160, 230);

            enemies.addAll(room6Enemies);
            if (soulTotemUsed) {
                totemHasSoul = false;
                totemHits = 0;
                totemFadeAlpha = 0;
            } else {
                totemHasSoul = true;
                totemHits = 3;
                totemFadeAlpha = 1f;
            }

            respawnX = 950;
            respawnY = 64;
        }

    }

    private void addHangingWallsToRoom() {
        //  دیوار معلق سمت چپ
        float leftTopCurveX = -20;
        float leftTopCurveY = 524;
        Platform leftTopCurve = new Platform(leftTopCurveX, leftTopCurveY, 206, 422);
        leftTopCurve.setTexture(wallTop);
        platforms.add(leftTopCurve);

        float leftBrokenWallWidth = 90;
        float leftBrokenWallHeight = 260;
        float leftBrokenWallX = -5;
        float leftBrokenWallY = leftTopCurveY - leftBrokenWallHeight + 15;

        Platform leftBrokenWall = new Platform(leftBrokenWallX, leftBrokenWallY, leftBrokenWallWidth, leftBrokenWallHeight);
        platforms.add(leftBrokenWall);

        //  دیوار معلق سمت راست (خروجی)
        float rightTopCurveX = 1094;
        float rightTopCurveY = 524;
        float rightTopCurveWidth = 206;
        float rightTopCurveHeight = 422;
        Platform rightTopCurve = new Platform(rightTopCurveX, rightTopCurveY, rightTopCurveWidth, rightTopCurveHeight);
        platforms.add(rightTopCurve);

        float rightBrokenWallWidth = 90;
        float rightBrokenWallHeight = 260;
        float rightBrokenWallX = 1195;
        float rightBrokenWallY = rightTopCurveY - rightBrokenWallHeight + 15;
        Platform rightBrokenWall = new Platform(rightBrokenWallX, rightBrokenWallY, rightBrokenWallWidth, rightBrokenWallHeight);
        rightBrokenWall.setTexture(brokenWall);
        platforms.add(rightBrokenWall);
    }

    public void update(Player player, float delta) {
        player.setGrounded(false);
        Rectangle playerBounds = player.bounds;
        Rectangle swordBounds = player.getAttackBounds();


        // بررسی برخورد با تیغ‌ها (Spikes)
        if (spikesArea != null && player.bounds.overlaps(spikesArea) && !player.isNoclip) {
            if (!player.isDead) {
                if (!player.isGodMode && !player.isInvincible()) {
                    player.takeDamage(1);
                }
                // حتی در حالت گادمد، باید کاراکتر ری‌اسپاون شود تا گیر نکند
                if (player.health > 0 || player.isGodMode) {
                    player.position.set(respawnX, respawnY);
                    player.bounds.setPosition(respawnX, respawnY);
                    player.velocity.set(0, 0);
                }
            }
        }

        //  برخورد با پلتفرم‌ها
        if (!player.isNoclip) {
        for (Platform platform : platforms) {
            if (playerBounds.overlaps(platform.bounds)) {
                float overlapLeft = (playerBounds.x + playerBounds.width) - platform.bounds.x;
                float overlapRight = (platform.bounds.x + platform.bounds.width) - playerBounds.x;
                float overlapTop = (playerBounds.y + playerBounds.height) - platform.bounds.y;
                float overlapBottom = (platform.bounds.y + platform.bounds.height) - playerBounds.y;

                float minOverlapX = Math.min(overlapLeft, overlapRight);
                float minOverlapY = Math.min(overlapBottom, overlapTop);

                if (minOverlapX < minOverlapY) {
                    if (overlapLeft < overlapRight) {
                        player.position.x = platform.bounds.x - playerBounds.width;
                    } else {
                        player.position.x = platform.bounds.x + platform.bounds.width;
                    }
                    player.velocity.x = 0;
                } else {
                    if (overlapBottom < overlapTop) {
                        player.position.y = platform.bounds.y + platform.bounds.height;
                        player.setGrounded(true);
                        player.velocity.y = 0;
                    } else {
                        player.position.y = platform.bounds.y - playerBounds.height;
                        player.velocity.y = 0;
                    }
                }
                player.bounds.setPosition(player.position.x, player.position.y);
            }
        }
    }

        //  برخورد با پیله در اتاق ۴
        if (currentRoom == 4 && isCocoonActive && cocoonBounds != null) {
            if (playerBounds.overlaps(cocoonBounds)) {
                player.heal(player.maxHealth);
                isCocoonActive = false;
            }
        }

        //  بررسی سقوط در دره
        if (player.position.y < -50) {
            boolean isFallingToRoom6 = (currentRoom == 3 && player.position.x > 900 && player.position.x < 1100);

            if (!isFallingToRoom6 && !player.isNoclip) {
                if (!player.isDead) {
                    if (!player.isGodMode && !player.isInvincible()) {
                        player.takeDamage(1);
                    }
                    if (player.health > 0 || player.isGodMode) {
                        respawnPlayer(player);
                    }
                }
            }
        }

        //  پر شدن روح با ضربه به مجسمه در اتاق 6
        if (currentRoom == 6 && soulTotemBounds != null) {
            for (TotemParticle p : totemParticles) {
                p.y += p.speedY * delta;
                p.alpha -= 0.5f * delta;
                if (p.alpha <= 0 && totemHasSoul) { // ریست کردن ذره اگر مجسمه هنوز روح دارد
                    p.y = soulTotemBounds.y + 90 + (float)Math.random() * 100;
                    p.x = soulTotemBounds.x + (float)Math.random() * soulTotemBounds.width;
                    p.alpha = 1f;
                }
            }

            // مصرف شدن روح با تعداد دفعات مشخص
            if (totemHasSoul && player.isAttacking && !player.hasHitEnemyThisSwing && swordBounds != null) {
                if (swordBounds.overlaps(soulTotemBounds)) {
                    player.addSoulOnHit();
                    player.hasHitEnemyThisSwing = true;
                    totemHits--;
                    if (totemHits <= 0) {
                        totemHasSoul = false;
                        soulTotemUsed = true;
                    }
                    com.HollowKnight.audio.AudioManager.getInstance().playSoulTotemSlash();
                    com.HollowKnight.audio.AudioManager.getInstance().playSoulPickup();
                }
            }

            // محو شدن نرم کل ذرات بعد از مصرف
            if (!totemHasSoul && totemFadeAlpha > 0) {
                totemFadeAlpha -= delta; // کاهش شفافیت به مرور
                if (totemFadeAlpha < 0) totemFadeAlpha = 0;
            }
        }



        // مدیریت انمی‌ها

        if (currentRoom == 5 && triggerEnemy != null && !bossSpawned) {
            if (triggerEnemy.isDying()/* && triggerEnemy.fadeAlpha <= 0*/) {
                float spawnX = 960f;
                FalseKnight boss = new FalseKnight(spawnX, 1000, 180, 1480, this.player, triggerEnemy, this);
                enemies.add(boss);
                bossSpawned = true;
            }
        }

        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            enemy.update(delta);

            if (spikesArea != null && enemy.bounds.overlaps(spikesArea)) {
                if (!enemy.isDying()) {
                    enemy.takeDamage(100);
                }
            }
            if (!enemy.isDying()) {

                boolean sharpShadowActive = player.isDashing && com.HollowKnight.model.Inventory.getInstance().isCharmEquipped("sharp_shadow");
                // الف) برخورد بازیکن با انمی (دمیج خوردن بازیکن)
                if (!player.isInvincible() && !player.isDead && !player.isGodMode && !player.isNoclip && playerBounds.overlaps(enemy.bounds)) {                    // فقط در صورتی دمیج بخورد که قابلیت شارپ شدو فعال نباشد
                    if (!sharpShadowActive) {
                        player.takeDamage(1);
                        float pushForce = 250f;
                        if (player.position.x < enemy.position.x) {
                            player.velocity.x = -pushForce;
                        } else {
                            player.velocity.x = pushForce;
                        }
                        player.velocity.y = 120f;
                    }
                }

                if (sharpShadowActive && playerBounds.overlaps(enemy.bounds)) {
                    enemy.takeDamage(1);
                }

                // ب) ضربه زدن بازیکن به انمی با شمشیر
                if (player.isAttacking && !player.hasHitEnemyThisSwing && swordBounds != null) {
                    if (swordBounds.overlaps(enemy.bounds)) {

                        int damageToDeal = com.HollowKnight.model.Inventory.getInstance().isCharmEquipped("strength") ? 2 : 1;
                        if (com.HollowKnight.model.Inventory.getInstance().isCharmEquipped("void_heart")) {
                            damageToDeal = (int)(damageToDeal * 1.5f); // افزایش قدرت توانایی‌ها
                        }

                        enemy.takeDamage(damageToDeal);

                        player.hasHitEnemyThisSwing = true;
                        player.addSoulOnHit();
                        com.HollowKnight.audio.AudioManager.getInstance().playAttackSound();

                        // سیستم ناک‌بک (پرتاب به عقب)
                        if (!(enemy instanceof Mosscreep)) {
                            float knockbackForce = com.HollowKnight.model.Inventory.getInstance().isCharmEquipped("heavy_blow") ? 80f : 40f;
                            float playerRecoil = 20f;

                            if (player.position.x < enemy.position.x) {
                                enemy.position.x += knockbackForce;
                                player.position.x -= playerRecoil;
                            } else {
                                enemy.position.x -= knockbackForce;
                                player.position.x += playerRecoil;
                            }
                        }
                    }
                }
            }

            // ج) حذف انمی کاملاً مرده از بازی و دادن Geo
            if (enemy instanceof Crawlid) {
                Crawlid c = (Crawlid) enemy;
                if (c.isDead() && c.fadeAlpha <= 0) {
                    AchievementManager.getInstance().registerEnemyKill("Crawlid");
                    for(int j=0; j<2; j++) geoItems.add(new GeoItem(c.position.x, c.position.y, geoTexture));
                    com.HollowKnight.audio.AudioManager.getInstance().playGeoDrop();
                    enemies.remove(i);
                }
            } else if (enemy instanceof Mosquito) {
                Mosquito m = (Mosquito) enemy;
                if (m.isDying() && m.fadeAlpha <= 0) {
                    AchievementManager.getInstance().registerEnemyKill("Mosquito");
                    for (int j = 0; j < 3; j++) {
                        geoItems.add(new GeoItem(m.position.x + (j * 10), m.position.y, geoTexture));
                    }
                    com.HollowKnight.audio.AudioManager.getInstance().playGeoDrop();
                    enemies.remove(i);
                }
            } else if (enemy instanceof Mosscreep){
                Mosscreep m = (Mosscreep) enemy;
                if (m.isDying() && m.fadeAlpha <= 0) {
                    AchievementManager.getInstance().registerEnemyKill("Mosscreep");
                    for (int j = 0; j < 3; j++) {
                        geoItems.add(new GeoItem(m.position.x + (j * 10), m.position.y, geoTexture));
                    }
                    com.HollowKnight.audio.AudioManager.getInstance().playGeoDrop();
                    enemies.remove(i);
                }
            } else if (enemy instanceof WingedSentry) {
                WingedSentry ws = (WingedSentry) enemy;
                if (ws.isDying() && ws.fadeAlpha <= 0) {
                    AchievementManager.getInstance().registerEnemyKill("Winged Sentry");
                    for (int j = 0; j < 5; j++) {
                        geoItems.add(new GeoItem(ws.position.x + (j * 15), ws.position.y + 20, geoTexture));
                    }
                    com.HollowKnight.audio.AudioManager.getInstance().playGeoDrop();
                    enemies.remove(i);
                }
            }else if (enemy instanceof Crystallized) {
                Crystallized cs = (Crystallized) enemy;
                if (cs.isDying() && cs.fadeAlpha <= 0) {
                    AchievementManager.getInstance().registerEnemyKill("Crystallized");
                    for (int j = 0; j < 6; j++) {
                        geoItems.add(new GeoItem(cs.position.x + (j * 15), cs.position.y + 20, geoTexture));
                    }
                    com.HollowKnight.audio.AudioManager.getInstance().playGeoDrop();
                    enemies.remove(i);
                }
            }else if (enemy instanceof HuskHornhead) {
                HuskHornhead hh = (HuskHornhead) enemy;
                if (hh.isDying() && hh.fadeAlpha <= 0) {
                    AchievementManager.getInstance().registerEnemyKill("Husk Hornhead");
                    for (int j = 0; j < 5; j++) {
                        geoItems.add(new GeoItem(hh.position.x + (j * 15), hh.position.y, geoTexture));
                    }
                    com.HollowKnight.audio.AudioManager.getInstance().playGeoDrop();
                    enemies.remove(i);
                }
            } else if (enemy instanceof FalseKnight) {
                FalseKnight fk = (FalseKnight) enemy;
                if (fk.isDying() && fk.fadeAlpha <= 0) {
                    AchievementManager.getInstance().registerBossDefeated();
                    this.isBossDefeated = true;
                    enemies.remove(i);
                }
            }
        }

        //  بررسی ضربه به دیوارهای شکستنی
        if (player.isAttacking && !player.hasHitEnemyThisSwing && swordBounds != null) {
            for (BreakableWall wall : breakableWalls) {
                if (!wall.isDestroyed() && swordBounds.overlaps(wall.bounds)) {
                    wall.takeDamage(1);
                    player.hasHitEnemyThisSwing = true;
                    com.HollowKnight.audio.AudioManager.getInstance().playAttackSound();

                    if (wall.isDestroyed()) {
                        com.HollowKnight.audio.AudioManager.getInstance().playBrokenWall();

                        screenShakeTime = 0.4f;

                        if (currentRoom == 3) {
                            room3WallDestroyed = true;
                        }
                    }
                }
            }
        }
        if (screenShakeTime > 0) {
            screenShakeTime -= delta;
        }


        //  آپدیت و چک کردن برخورد بازیکن با سکه‌ها
        for (int j = geoItems.size() - 1; j >= 0; j--) {
            GeoItem item = geoItems.get(j);
            item.update(delta, platforms);

            if (player.bounds.overlaps(item.bounds)) {
                player.geo += 1;
                geoItems.remove(j);
                com.HollowKnight.audio.AudioManager.getInstance().playGeoCollect();
            }
        }
    }

    public void respawnPlayer(Player player) {
        player.position.set(respawnX, respawnY);
        player.velocity.set(0, 0);
        player.bounds.setPosition(respawnX, respawnY);
        player.setGrounded(false);
    }

    public int getCurrentRoom() {
        return currentRoom;
    }

    public void initAllEnemies() {
        room1Enemies = new ArrayList<>();
        room1Enemies.add(new Mosscreep(450, 64, this.player, 80, 1150));
        room1Enemies.add(new Mosscreep(850, 64, this.player, 80, 1150));


        room2Enemies = new ArrayList<>();
        room2Enemies.add(new Mosquito(450, 300, 60, 60, this.player));
        room2Enemies.add(new Mosquito(750, 400, 60, 60, this.player));


        room3Enemies = new ArrayList<>();
        room3Enemies.add(new Crawlid(150, 64, 100));
        room3Enemies.add(new Crawlid(1150, 64, 100));

        room4Enemies = new ArrayList<>();
        room4Enemies.add(new WingedSentry(850, 400, this.player));

        room5Enemies = new ArrayList<>();
        this.triggerEnemy = new HuskHornhead(300, 64, 100, 1200, this.player);
        room5Enemies.add(triggerEnemy);
        bossSpawned = false;
//        FalseKnight boss = new FalseKnight(1600, 64, 100, 1800, this.player, triggerEnemy, this);
//        room5Enemies.add(boss);

        room6Enemies = new ArrayList<>();
        room6Enemies.add(new Crystallized(600, 64, this.player));
    }

    public void resetOnPlayerDeath() {
        initAllEnemies();
        soulTotemUsed = false;
    }

    public void render(SpriteBatch batch) {
        if (currentRoom == 5 && bgBossTexture != null) {
            batch.draw(bgBossTexture, 0, -50, roomWidth, 900);
        } else if (bgTexture != null) {
            batch.draw(bgTexture, 0, 0, roomWidth, 800);
        }

        if (currentRoom == 1) {
            if (brokenBenchTexture != null) batch.draw(brokenBenchTexture, 600, 64);
            if (fungTextre0 != null) {
                batch.draw(fungTextre0, 200, 50);
                batch.draw(fungTextre0, 250, 50);
                batch.draw(fungTextre0, 900, 50);
                batch.draw(fungTextre0, 950, 50);
            }
            if (lampTexture != null) {
                batch.draw(lampTexture, 100, 64);
                batch.draw(lampTexture, 1100, 64);
            }
            if (ruinLampTexture != null){
                batch.draw(ruinLampTexture, 450, 64);
            }

        } else if (currentRoom == 2) {
            if (spikesTexture != null) {
                batch.draw(spikesTexture, 400, 0, 480, 50);
            }
            if (lampTexture != null) {
                batch.draw(lampTexture, 130, 64);
                batch.draw(lampTexture, 1030, 64);
            }
            if (lampCaveTexture01 != null) {
                batch.draw(lampCaveTexture01, 520, 180);
            }
            if (lampCaveTexture02 != null) {
                batch.draw(lampCaveTexture02, 710, 290);
            }

        }else if (currentRoom == 3) {
            if (spike3 != null) {
                for (int i = 0; i < 5; i++) {
                    batch.draw(spike3, 300 + (i * 100), 0, 100, 60);
                }
            }
            if (cliffsLamp != null) {
                batch.draw(cliffsLamp, 200, 120, 355, 515);
            }

        } else if (currentRoom == 4) {
            if (benchTexture != null) {
                batch.draw(benchTexture, 550, 64);
            }

            if (lampTexture != null) {
                batch.draw(lampTexture, 450, 64);
                batch.draw(lampTexture, 750, 64);
            }
            if (isCocoonActive && cocoonAnim != null) {
                cocoonTimer += com.badlogic.gdx.Gdx.graphics.getDeltaTime();
                TextureRegion frame = cocoonAnim.getKeyFrame(cocoonTimer);
                batch.draw(frame, cocoonBounds.x, cocoonBounds.y, cocoonBounds.width, cocoonBounds.height);
            } else if (!isCocoonActive && consumedCocoonTex != null) {
                batch.draw(consumedCocoonTex, cocoonBounds.x, cocoonBounds.y, cocoonBounds.width, cocoonBounds.height);
            }
        }
        else if (currentRoom == 5) {
            if (pillarTex != null) {
                float pillarWidth = 180;
                float pillarHeight = 736 - 64; // 672
                batch.draw(pillarTex, 300, 64, pillarWidth, pillarHeight);
                batch.draw(pillarTex, 800, 64, pillarWidth, pillarHeight);
                batch.draw(pillarTex, 1400, 64, pillarWidth, pillarHeight);
            }
            // رسم لامپ‌ها
            if (lampTexture != null) {
                batch.draw(lampTexture, 200, 64);
                batch.draw(lampTexture, 900, 64);
                batch.draw(lampTexture, 1600, 64);
            }
        }
        else if (currentRoom == 6) {
            // رسم لامپ‌ها
            if (lampTexture != null) {
                batch.draw(lampTexture, 100, 64);
                batch.draw(lampTexture, 1000, 64);
            }
            // رسم مجسمه روح
            if (soulTotemTexture != null && soulTotemBounds != null) {
                batch.draw(soulTotemTexture, soulTotemBounds.x, soulTotemBounds.y, soulTotemBounds.width, soulTotemBounds.height);

                // رسم ذرات با در نظر گرفتن شفافیت
                if (soulParticleTex != null && totemFadeAlpha > 0) {
                    for (TotemParticle p : totemParticles) {
                        float finalAlpha = p.alpha * totemFadeAlpha;
                        if (finalAlpha > 0) {
                            batch.setColor(1, 1, 1, finalAlpha);
                            batch.draw(soulParticleTex, p.x, p.y, 25, 25);
                        }
                    }
                    batch.setColor(1, 1, 1, 1);
                }
            }
        }

        // رسم انمی‌ها
        for (Enemy enemy : enemies) {
            enemy.render(batch);
        }

        // رسم پلتفرم‌ها
        for (Platform p : platforms) {
            p.render(batch);
        }
        if (flippedWallTop != null) {
            if (currentRoom >= 1 && currentRoom <= 4) {
                batch.draw(flippedWallTop, 1094, 524, 206, 422);
            } else if (currentRoom == 5 || currentRoom == 6) {
                batch.draw(flippedWallTop, roomWidth - 186, 524, 206, 422);
            }
        }

        if (currentRoom >= 2 && currentRoom <= 4 && flippedBrokenWall != null) {
            batch.draw(flippedBrokenWall, -10, 270, 90, 260);
        }
        for (GeoItem item : geoItems) {
            item.render(batch);
        }
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

}
