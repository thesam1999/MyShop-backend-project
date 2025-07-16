package com.sideproject.myshop.auth.helper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;


// 產生JWT 和驗證JWT
@Component
public class JWTTokenHelper {

    @Value("${jwt.auth.app}")
    private String appName;

    @Value("${jwt.auth.secret_key}")
    private String secretKey;

    @Value("${jwt.auth.expires_in}")
    private int expiresIn;

    @Value("${jwt.private-key-path}")
    private String privateKeyPath;

    @Value("${jwt.public-key-path}")
    private String publicKeyPath;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    @PostConstruct //會在物件建立、所有欄位注入完成後(@Value 或 @Autowired )，自動執行這個方法
    // 完成依賴注入完成後自動執行這個方法！
    public void init(){
        this.privateKey = getPrivateKey(privateKeyPath);
        this.publicKey = getPublicKey(publicKeyPath);
    }


    // 用基本的對稱式簽名，正式環境要用非對稱式簽名
    public String generateToken(String userName){
        return Jwts.builder()
                .issuer(appName) // 發行者
                .subject(userName) // 主題（通常是用戶名稱或ID）
                .issuedAt(new Date()) // 簽發時間
                .expiration(generateExpirationDate()) // 過期時間
                .signWith(privateKey) // 簽名密鑰(預設用HMAC SHA-256)，會自動判斷該用的演算法
                .compact();
    }

    /*
    對稱式簽名
     */
    // 方法1：動態產生隨機的密鑰的方法(B64字串格式)
    public String generateSecretKey() {
        try {
            // 建立一個 金鑰產生器 (KeyGenerator)，用的演算法是 HmacSHA256。
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            // 用剛剛的金鑰產生器 生成一把隨機的秘密金鑰 (SecretKey)
            SecretKey secretKey = keyGen.generateKey();
            System.out.println("Secret Key : " + secretKey.toString());
            // 將秘密金鑰的 位元組資料 (byte[]) 轉成 Base64 字串，這樣方便儲存或傳送。
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating secret key", e);
        }
    }

    //  把 secretKey  (Base64 編碼) 解碼成 byte[]，再用這個 byte[] 生成 SecretKey物件，用來給signWith使用(做 JWT 的簽章或驗證)。
    private SecretKey getSigningKey() {
        // 把儲存在 secretKey 這個字串，用 Base64 解碼成原始的位元組陣列（byte[]）。
        // 因為密鑰本身是二進位資料，不能直接用字串，要先解碼還原成位元組。
        byte[] keysBytes = Decoders.BASE64.decode(secretKey);
        // 使用 Keys 這個工具類（來自於 jjwt 套件，Java JWT）
        //hmacShaKeyFor(byte[] keyBytes) 方法會將這個 byte 陣列包成一個 SecretKey 物件，
        //這個物件可以被 JWT 簽章函式用來產生或驗證簽名，能用來做 HMAC SHA-256 簽名的密鑰物件
        return Keys.hmacShaKeyFor(keysBytes);
    }

    /*
    非對稱式簽名
     */

    public PrivateKey getPrivateKey(String filename) {
        try {

            /*
            為了方便處理 PrivateKey 先轉成字串，去除多餘字串，再轉回byte[]
            如果不需要去除多餘文字，可以直接包裝成 PKCS8EncodedKeySpec 物件
             */


            // 逐行讀取才會用到 BufferedReader 或 Scanner
            // 讀取檔案的「原始二進位資料」才會用到InputStream (圖片、影音、加密檔等)

            //一次讀取完整文字檔案用 Files.readAllBytes() ！！比較好
            // Paths.get(filename)：用給定路徑取得檔案路徑物件
            // Files.readAllBytes(...)：一次讀出檔案所有位元組
            // new String(...)：將位元組轉成字串
            String key = new String(Files.readAllBytes(Paths.get(filename))); // readAllBytes 回傳byte[]，要轉乘String
            key = key.replaceAll("-----BEGIN (.*)-----", "")
                    .replaceAll("-----END (.*)-----", "")
                    .replaceAll("\\s", "");

            // 把 Base64 編碼的字串還原成位元組陣列 (byte[] )
            byte[] keyBytes = Base64.getDecoder().decode(key);



            // 把「純二進位私鑰資料（byte[]）」轉成 Java 裡面可用的 PrivateKey 物件，讓你在程式裡可以用它來簽名、解密等操作

            //  將位元組包裝成 PKCS8EncodedKeySpec 物件（私鑰規範）
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);

            //  建立 RSA 演算法的 KeyFactory 工廠物件
            KeyFactory kf = KeyFactory.getInstance("RSA");

            //  利用工廠把 spec 轉成 PrivateKey 物件，代表可用來做 RSA 簽章的私鑰
            return kf.generatePrivate(spec);
        }catch (Exception e){
            e.printStackTrace();  // 也可以換成用 logger 紀錄
            return null;
        }
    }

    private PublicKey getPublicKey(String filename) {
        try {
            // 清理檔案不必要的字串
            String key = new String(Files.readAllBytes(Paths.get(filename)));
            key = key.replaceAll("-----BEGIN (.*)-----", "")
                    .replaceAll("-----END (.*)-----", "")
                    .replaceAll("\\s", "");
            byte[] keyBytes = Base64.getDecoder().decode(key);

            //  建立一個 X509EncodedKeySpec 物件，用來表示 X.509 格式的公鑰。（X.509 是一種常用的公鑰格式，很多 PEM 公鑰都是這種格式）
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            // 建立 RSA 的 KeyFactory，然後根據剛剛的 spec 產生 PublicKey 物件。
            //這個 PublicKey 物件可以拿去做： 驗證數位簽章、加密資料（對稱密鑰等）
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(spec);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    // 一小時後JWT過期
    private Date generateExpirationDate() {
        // 取得當下時間的「毫秒表示」 +  1 小時的毫秒數
        return new Date(new Date().getTime() + expiresIn * 1000L); // L是Long對應 getTime() 會回傳Long
    }

    public String getToken( HttpServletRequest request ) {

        String authHeader = getAuthHeaderFromHeader( request );
        // Authorization: Bearer <token> ，Bearer跟Token之間有空格，要記得！
        if ( authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return authHeader;
    }

    // 驗證token是否正確
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUserNameFromToken(token);
        return (
                username != null &&
                        username.equals(userDetails.getUsername()) &&
                        !isTokenExpired(token)
        );
    }

    private boolean isTokenExpired(String token) {
        Date expireDate=getExpirationDate(token);
        return expireDate.before(new Date());
    }

    private Date getExpirationDate(String token) {
        Date expireDate;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            expireDate = claims.getExpiration();
        } catch (Exception e) {
            expireDate = null;
        }
        return expireDate;
    }

    //取得JWT的Header，也就是Token
    private String getAuthHeaderFromHeader(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    public String getUserNameFromToken(String authToken) {
        String username;
        try {
            final Claims claims = this.getAllClaimsFromToken(authToken);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    // 檢查token是否有效，式的話就將所有token中使用者資訊封裝到Claims
    private Claims getAllClaimsFromToken(String token){
        //JWT的interface，專門儲存JWT資料的Map
        Claims claims;
        try{
                    // 先建立JWT解析器
            claims= Jwts.parser()
                    //設置密鑰，檢查token的方法是否有效
                    .verifyWith(publicKey)
                    //創建解析器實力
                    .build()
                    //解析並驗證傳入的 JWT
                    .parseSignedClaims(token)
                    //用來提取 JWT 內容（即 claims）的部分
                    //從 parseSignedClaims(token) 返回的結果中提取出有效負載（payload）並返回
                    .getPayload();
        }
        catch (Exception e){
            claims = null;
        }
        return claims;
    }
}