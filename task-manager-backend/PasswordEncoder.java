import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoder {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 生成 Admin@2026 的 BCrypt 哈希
        String rawPassword = "Admin@2026";
        String encodedPassword = encoder.encode(rawPassword);
        
        System.out.println("原始密码: " + rawPassword);
        System.out.println("BCrypt 哈希: " + encodedPassword);
        System.out.println();
        System.out.println("SQL 更新语句:");
        System.out.println("UPDATE sys_user SET password = '" + encodedPassword + "' WHERE user_name = 'customer1';");
    }
}
