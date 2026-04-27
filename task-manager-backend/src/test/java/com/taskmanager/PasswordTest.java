package com.taskmanager;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码测试工具
 * 用于生成和验证 BCrypt 密码哈希
 */
public class PasswordTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String rawPassword = "Admin@2026";
        
        // 生成新的密码哈希
        String encodedPassword = encoder.encode(rawPassword);
        
        System.out.println("========================================");
        System.out.println("密码测试工具");
        System.out.println("========================================");
        System.out.println("原始密码: " + rawPassword);
        System.out.println("生成的 BCrypt 哈希: " + encodedPassword);
        System.out.println();
        
        // 测试几个已知的哈希值
        String[] knownHashes = {
            "$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8OS9Z1yyUxDe",
            "$2a$10$22BUeamzvjXTPmgwFVMQZedEJFW41hRtWsvbtHUzDjt5V9OpOd.N2",
            "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
        };
        
        System.out.println("验证已知的哈希值:");
        for (String hash : knownHashes) {
            boolean matches = encoder.matches(rawPassword, hash);
            System.out.println("哈希: " + hash.substring(0, Math.min(30, hash.length())) + "...");
            System.out.println("是否匹配: " + (matches ? "✓ 是" : "✗ 否"));
            System.out.println();
        }
        
        System.out.println("========================================");
        System.out.println("SQL 更新语句:");
        System.out.println("========================================");
        System.out.println("UPDATE sys_user SET password = '" + encodedPassword + "' WHERE user_name = 'customer1';");
        System.out.println();
        System.out.println("或者使用已知的正确哈希:");
        System.out.println("UPDATE sys_user SET password = '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8OS9Z1yyUxDe' WHERE user_name = 'customer1';");
    }
}
