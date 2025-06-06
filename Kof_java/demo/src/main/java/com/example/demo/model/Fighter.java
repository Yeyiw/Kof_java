package com.example.demo.model;

import com.example.demo.util.Animation;
import com.example.demo.util.FrameData;
import com.example.demo.util.ResourceLoader;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import java.util.HashMap;
import java.util.Map;

public class Fighter extends Character {
    // 位置和大小
    private double x, y;
    private final double width, height;

    // 物理属性
    private double velocityX = 0;
    private double velocityY = 0;
    private boolean isJumping = false;
    private boolean isDashing = false;
    private double dashTimer = 0;
    private double dashCooldown = 0;
    private String lastMoveDirection = null;

    // 战斗属性
    private int health = 100;
    private boolean isBlocking = false;
    private boolean isHit = false;
    private double hitTimer = 0;
    private static final double HIT_DURATION = 0.3;
    private static final double HIT_KNOCKBACK = 5;
    private static final double GROUND_LEVEL = 550; // 地面高度

    // 动画状态
    private String facing = "right";
    private String state = "idle";
    private final Map<String, Animation> animations = new HashMap<>();
    private Animation currentAnimation;

    // 控制设置
    private Map<String, String> controls = Map.of(
            "left", "",
            "right", "",
            "jump", "",
            "punch", "",
            "kick", "",
            "special", "",
            "block", "",
            "dash", ""
    );

    // 攻击属性
    private final Map<String, Attack> attacks = Map.of(
            "punch", new Attack(10, 50, 0.5),
            "kick", new Attack(15, 70, 0.6),
            "special", new Attack(25, 70, 1.0)
    );

    /**
     * 获取角色的控制键位映射
     * @return 控制键位映射表
     */
    public Map<String, String> getControls() {
        return this.controls;
    }

    /**
     * 获取特定动作的控制键
     * @param action 动作名称（如"left", "punch"等）
     * @return 对应的键位字符串
     */
    public String getControl(String action) {
        return this.controls.get(action);
    }

    // 内部攻击类
    private static class Attack {
        final int damage;
        final double range;
        double cooldown;
        final double duration;

        Attack(int damage, double range, double duration) {
            this.damage = damage;
            this.range = range;
            this.duration = duration;
            this.cooldown = 0;
        }
    }

    public Fighter(double x, double y, double width, double height,
                   CharacterData characterData, Map<String, String> controls) {
        super(characterData);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.controls = new HashMap<>(controls);
        initializeAnimations();
    }

    private void initializeAnimations() {
        getCharacterData().getFrameData().forEach((action, frameData) -> {
            Image[] frames = loadAnimationFrames(action, frameData.getCount());
            if (frames != null) {
                animations.put(action, new Animation(frames, 1.0 / frameData.getSpeed()));
            }
        });
        this.currentAnimation = animations.getOrDefault("idle", null);
    }

    private Image[] loadAnimationFrames(String action, int frameCount) {
        try {
            // 构建精灵图路径
            String spriteSheetPath = String.format("com/example/demo/assets/characters/%s/%s_%s.png",
                    getCharacterData().getId(),
                    getCharacterData().getId(),
                    action);

            // 加载精灵图
            Image spriteSheet = ResourceLoader.loadImage(spriteSheetPath);
            if (spriteSheet == null) {
                throw new RuntimeException("Failed to load sprite sheet: " + spriteSheetPath);
            }

            // 获取精灵图的总宽度和高度
            int totalWidth = (int) spriteSheet.getWidth();
            int totalHeight = (int) spriteSheet.getHeight();

            // 每一帧的宽度 = 总宽度 / 帧数，高度保持不变
            int frameWidth = totalWidth / frameCount;
            int frameHeight = totalHeight;

            // 创建帧数组
            Image[] frames = new Image[frameCount];

            PixelReader reader = spriteSheet.getPixelReader();

            for (int i = 0; i < frameCount; i++) {
                int x = i * frameWidth;
                int y = 0;

                WritableImage frame = new WritableImage(reader, x, y, frameWidth, frameHeight);
                frames[i] = frame;
            }

            return frames;
        } catch (Exception e) {
            System.err.println("Error loading animation frames for " + action + ": " + e.getMessage());
            return null;
        }
    }

    public void update(double deltaTime) {
        updateDirection();
        updateMovement(deltaTime);
        updateGravity(deltaTime);
        updateCooldowns(deltaTime);
        updateAnimation(deltaTime);
        checkBoundaries();
    }

    private void updateDirection() {
        Fighter opponent = getOpponent();
        if (opponent != null) {
            facing = x < opponent.getX() ? "right" : "left";
        }
    }

    protected Fighter getOpponent() {
        // 应由GameController通过setter方法设置
        return null;
    }

    private void updateMovement(double deltaTime) {
        if (!isBlocking && !isAttacking()) {
            if (isDashing) {
                dashTimer -= deltaTime;
                if (dashTimer <= 0) {
                    isDashing = false;
                    velocityX = 0;
                }
            }

            x += velocityX * deltaTime * 100;
            y += velocityY * deltaTime * 100;
        }

        if (isHit) {
            hitTimer -= deltaTime;
            if (hitTimer <= 0) {
                isHit = false;
                state = "idle";
            }
        }
    }

    private void updateGravity(double deltaTime) {
        if (y + height < GROUND_LEVEL) {
            velocityY += 0.72 * deltaTime * 100;
            isJumping = true;
        } else {
            velocityY *= 0.25;
            if (Math.abs(velocityY) < 0.05 || y + height > GROUND_LEVEL) {
                y = GROUND_LEVEL - height;
                velocityY = 0;
                isJumping = false;
            }
        }
    }

    private void updateCooldowns(double deltaTime) {
        attacks.values().forEach(attack -> {
            if (attack.cooldown > 0) {
                attack.cooldown -= deltaTime;
            }
        });

        if (dashCooldown > 0) {
            dashCooldown -= deltaTime;
        }
    }

    private void updateAnimation(double deltaTime) {
        String frameType = getCurrentFrameType();
        currentAnimation = animations.get(frameType);
        if (currentAnimation != null) {
            currentAnimation.update(deltaTime);
        }
    }

    private String getCurrentFrameType() {
        if (isHit) return "hit";
        if (state.equals("punch")) return "punch";
        if (state.equals("kick")) return "kick";
        if (state.equals("special")) return "special";
        if (state.equals("blocking")) return "block";
        if (isJumping) return "jump";
        if (isDashing) {
            boolean isForwardDash = (facing.equals("right") && velocityX > 0) ||
                    (facing.equals("left") && velocityX < 0);
            return isForwardDash ? "dash" : "dashBack";
        }
        if (velocityX != 0) return velocityX > 0 ? "walk" : "walkBack";
        return "idle";
    }

    public boolean isAttacking() {
        return state.equals("punch") || state.equals("kick") || state.equals("special");
    }

    private void checkBoundaries() {
        if (x < 0) x = 0;
        if (x + width > 800) x = 800 - width;
    }

    public void draw(GraphicsContext gc) {
        if (currentAnimation == null || currentAnimation.getCurrentFrame() == null) {
            drawFallback(gc);
            return;
        }

        gc.save();
        applyFacingTransform(gc);
        drawCurrentFrame(gc);
        gc.restore();
    }

    private void drawFallback(GraphicsContext gc) {
        gc.setFill(Color.valueOf(getCharacterData().getColor()));
        gc.fillRect(x, y, width, height);
    }

    private void applyFacingTransform(GraphicsContext gc) {
        if (facing.equals("left")) {
            Affine transform = new Affine();
            transform.appendTranslation(width, 0);
            transform.appendScale(-1, 1);
            gc.setTransform(transform);
        }
    }

    private void drawCurrentFrame(GraphicsContext gc) {
        Image frame = currentAnimation.getCurrentFrame();
        double drawX = facing.equals("right") ? x : -x - width;
        double drawY = y + height - frame.getHeight();
        gc.drawImage(frame, drawX, drawY);
    }

    // 移动控制方法
    public void moveLeft() {
        if (!isDashing && !isBlocking && !isAttacking()) {
            velocityX = -3;
            lastMoveDirection = "left";
        }
    }

    public void moveRight() {
        if (!isDashing && !isBlocking && !isAttacking()) {
            velocityX = 3;
            lastMoveDirection = "right";
        }
    }

    public void stopMoving() {
        if (!isDashing) {
            velocityX = 0;
            lastMoveDirection = null;
        }
    }

    public boolean dash() {
        if (lastMoveDirection == null) return false;

        if (!isJumping && !isDashing && !isAttacking() && dashCooldown <= 0) {
            isDashing = true;
            dashTimer = 0.15;
            dashCooldown = 0.5;
            velocityX = lastMoveDirection.equals("left") ? -7 : 7;
            return true;
        }
        return false;
    }

    public void jump() {
        if (!isJumping && !isDashing) {
            velocityY = -8;
            isJumping = true;
        }
    }

    public void jumpBack() {
        if (!isJumping) {
            velocityY = -6;
            velocityX = facing.equals("right") ? -3 : 3;
            isJumping = true;
        }
    }

    // 战斗方法
    public void takeHit(int damage, Fighter attacker) {
        if (isBlocking) {
            damage /= 2; // 格挡减半伤害
        }

        health = Math.max(0, health - damage);
        isHit = true;
        hitTimer = HIT_DURATION;

        if (currentAnimation != null) {
            currentAnimation.reset();
        }

        applyKnockback(attacker);
    }

    private void applyKnockback(Fighter attacker) {
        double direction = x < attacker.getX() ? -1 : 1;
        velocityX = direction * HIT_KNOCKBACK;
    }

    public void attack(String type) {
        Attack attack = attacks.get(type);
        if (attack == null || isJumping || isDashing || attack.cooldown > 0) {
            return;
        }

        state = type;
        attack.cooldown = attack.duration + 0.5;
        currentAnimation = animations.get(type);
        if (currentAnimation != null) {
            currentAnimation.reset();
        }
    }

    public boolean canAttack() {
        return !isJumping && !isDashing;
    }

    public boolean canDash() {
        return !isJumping && !isDashing && !isAttacking() && dashCooldown <= 0;
    }
    public boolean canJump() {
        return !isAttacking() && !isDashing && !isAttacking() ;
    }

    public void block() {
        if (!isJumping && !isAttacking()) {
            isBlocking = true;
            state = "blocking";
        }
    }

    public void stopBlocking() {
        isBlocking = false;
        if (state.equals("blocking")) {
            state = "idle";
        }
    }

    // Getter 方法
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public int getHealth() { return health; }
    public boolean isBlocking() { return isBlocking; }
    public boolean isDashing() { return isDashing; }
    public boolean isJumping() { return isJumping; }
    public String getFacing() { return facing; }
    public String getState() { return state; }
    public double getCurrentAttackRange() {
        Attack attack = attacks.get(state);
        return attack != null ? attack.range : 0;
    }
    public int getCurrentAttackDamage() {
        Attack attack = attacks.get(state);
        return attack != null ? attack.damage : 0;
    }
}