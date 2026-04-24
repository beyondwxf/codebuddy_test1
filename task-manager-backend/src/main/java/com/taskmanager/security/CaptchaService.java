package com.taskmanager.security;

import com.taskmanager.common.exception.CaptchaException;
import com.taskmanager.common.constant.Constants;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

/**
 * 验证码服务
 * 生成和校验图形验证码
 *
 * @author taskmanager
 */
@Component
public class CaptchaService {

    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;
    private static final String CHARS = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final Random random = new Random();

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 生成验证码并存储到 Redis
     */
    public CaptchaVO createCaptcha() {
        String captchaKey = UUID.randomUUID().toString().replace("-", "");
        String code = generateCode(4);
        String img = generateCaptchaImage(code);
        redisTemplate.opsForValue().set(
            Constants.CAPTCHA_CODE_KEY + captchaKey,
            code,
            Constants.CAPTCHA_EXPIRATION,
            TimeUnit.MINUTES
        );
        return new CaptchaVO(captchaKey, img);
    }

    private String generateCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    private String generateCaptchaImage(String code) {
        try {
            BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // 背景
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, WIDTH, HEIGHT);
            
            // 干扰线
            g.setColor(new Color(200, 200, 200));
            for (int i = 0; i < 5; i++) {
                g.drawLine(random.nextInt(WIDTH), random.nextInt(HEIGHT),
                    random.nextInt(WIDTH), random.nextInt(HEIGHT));
            }
            
            // 验证码文字
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.setColor(new Color(60, 60, 60));
            int x = 15;
            for (int i = 0; i < code.length(); i++) {
                g.drawString(String.valueOf(code.charAt(i)), x, 28);
                x += 22;
            }
            
            g.dispose();
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("生成验证码失败", e);
        }
    }

    /**
     * 校验验证码（忽略大小写）
     */
    public void validateCaptcha(String key, String code) {
        if (StrUtil.isBlank(key) || StrUtil.isBlank(code)) {
            throw new CaptchaException("验证码不能为空");
        }
        String redisKey = Constants.CAPTCHA_CODE_KEY + key;
        Object cachedCode = redisTemplate.opsForValue().get(redisKey);
        if (cachedCode == null) {
            throw new CaptchaException("验证码已过期，请刷新重试");
        }
        if (!code.equalsIgnoreCase(cachedCode.toString())) {
            throw new CaptchaException("验证码错误");
        }
        redisTemplate.delete(redisKey);
    }

    public static class CaptchaVO {
        private String uuid;
        private String img;

        public CaptchaVO(String uuid, String img) {
            this.uuid = uuid;
            this.img = img;
        }

        public String getUuid() { return uuid; }
        public void setUuid(String uuid) { this.uuid = uuid; }
        public String getImg() { return img; }
        public void setImg(String img) { this.img = img; }
    }
}
