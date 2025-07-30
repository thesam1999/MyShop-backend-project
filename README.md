
# 🛍️Shopping Website 後端作品集

<p>
  <img src="images/網站首頁.png" alt="網站首頁" width="75%" />
</p>

## 📘簡介

這是一個基於 Spring Boot 開發的後端購物網站，涵蓋會員身分認證、商品後台管理、購物車及結帳操作、Stripe 金流整合與 Swagger API 文件功能。

此專案展示了我在學習 RESTful API 設計、後端邏輯、資料庫操作與關聯建立的基本掌握與實作經驗。


## 目錄
- [🌐 RESTful API](#-restful-api)
- [📌 資料庫結構簡介](#-資料庫結構簡介)
- [🔗 關聯設計](#-關聯設計)
- [📦 DTO(資料傳輸物件)](#-DTO資料傳輸物件)
- [🖼️ 畫面展示](#-畫面展示)


<details>

<summary><strong>專案結構</strong></summary>

```
├───java
│   └───com
│       └───sideproject
│           └───myshop
│               │   MyshopApplication.java
│               │   
│               ├───auth
│               │   ├───config
│               │   │       JWTAuthenticationFilter.java
│               │   │       JWTAuthenticationToken.java
│               │   │       WebSecurityConfig.java
│               │   │       
│               │   ├───controller
│               │   │       AuthController.java
│               │   │       OAuth2Controller.java
│               │   │       UserDetailController.java
│               │   │       
│               │   ├───dto
│               │   │       ApiErrorResponse.java
│               │   │       LoginRequest.java
│               │   │       RegistrationRequest.java
│               │   │       RegistrationResponse.java
│               │   │       UserDetailsDto.java
│               │   │       UserToken.java
│               │   │       
│               │   ├───entities
│               │   │       Authority.java
│               │   │       User.java
│               │   │       
│               │   ├───exceptions
│               │   │       RESTAuthenticationEntryPoint.java
│               │   │       
│               │   ├───helper
│               │   │       JWTTokenHelper.java
│               │   │       VerificationCodeGenerator.java
│               │   │       
│               │   ├───repositories
│               │   │       AuthorityRepository.java
│               │   │       UserDetailRepository.java
│               │   │       
│               │   └───services
│               │           AuthorityService.java
│               │           CustomUserDetailService.java
│               │           EmailService.java
│               │           OAuth2Service.java
│               │           RegistrationService.java
│               │           
│               ├───config
│               │       SwaggerConfig.java
│               │       
│               ├───controllers
│               │       AddressController.java
│               │       CategoryController.java
│               │       FileUpload.java
│               │       OrderController.java
│               │       ProductController.java
│               │       TestController.java
│               │       
│               ├───dto
│               │       AddressRequest.java
│               │       CategoryDto.java
│               │       CategoryTypeDto.java
│               │       ExceptionResponse.java
│               │       OrderDetails.java
│               │       OrderItemDetail.java
│               │       OrderItemRequest.java
│               │       OrderRequest.java
│               │       OrderResponse.java
│               │       ProductDto.java
│               │       ProductResourceDto.java
│               │       ProductVariantDto.java
│               │       
│               ├───entities
│               │       Address.java
│               │       Category.java
│               │       CategoryType.java
│               │       Order.java
│               │       OrderItem.java
│               │       OrderStatus.java
│               │       Payment.java
│               │       PaymentStatus.java
│               │       Product.java
│               │       ProductVariant.java
│               │       Resources.java
│               │       
│               ├───exceptions
│               │       GlobalExceptionHandler.java
│               │       InternalServerException.java
│               │       PaymentFailedException.java
│               │       ResourceNotFoundEx.java
│               │       
│               ├───mapper
│               │       CategoryMapper.java
│               │       OrderMapper.java
│               │       ProductMapper.java
│               │       
│               ├───repositories
│               │       AddressRepository.java
│               │       CategoryRepository.java
│               │       OrderRepository.java
│               │       ProductRepository.java
│               │       
│               ├───services
│               │       AddressService.java
│               │       CategoryService.java
│               │       FileUploadService.java
│               │       OrderService.java
│               │       PaymentIntentService.java
│               │       ProductService.java
│               │       ProductServiceImpl.java
│               │       
│               └───specification
│                       ProductSpecification.java
│                       
└───resources
    │   application.properties
    │   
    ├───static
    └───templates

```
</details>


## 🚀畫面功能展示

- 📄 **[Swagger API 文件](#Swagger)**（提供測試功能）
- 🔐 **[會員系統](#使用者註冊流程介紹)**（註冊、登入、登出、Email驗證、Google OAuth2）
- 🛒 **[購物車及結帳功能](#結帳流程)**（加入、刪除、數量調整與總金額計算）
- 💳 **[Stripe](#Stripe管理後台畫面)** 第三方金流整合 (後臺可查看詳細數據)
- 📦 **[後台商品與分類管理](#商品管理)**（增加及刪除商品）

[//]: # (- 🚫 **[錯誤處理機制]&#40;#常見錯誤&#41;**（帳號重複、驗證失敗等錯誤訊息提示）)

## 🧩 技術架構
本專案使用以下技術：

- Spring Boot (Spring Security, Spring Data JPA(Hibernate), Spring MVC)
- JWT（JSON Web Token）
- Postgresql
- Google OAuth2（第三方登入）
- Stripe 金流整合 (信用卡付款)
- Swagger API 文件
- JavaMailSender（Email 驗證碼寄送）
- Maven
- RESTful API 設計原則

---


## 🌐 RESTful API

撰寫 `GlobalExceptionHandler` 類別透過  `@RestControllerAdvice` 監聽 Controller 層級例外，當觸發例外時，回傳指定狀態碼與固定JSON錯誤訊息：
1. `ResourceNotFoundEx` 例外，回傳狀態碼 404，error："Resource Not Found in Database"
2. `InternalServerException` 例外，回傳狀態碼 500，error："Internal Server Error"
3. `PaymentFailedException` 例外，回傳狀態碼 400，error："Payment Failed"
4. `IllegalArgumentException` 例外，回傳狀態碼 400，error："Invalid Argument"
5. `BadRequestException` 例外，回傳狀態碼400，error："Bad Request"
6. 其他例外，回傳狀態碼 500，error："Something Went Wrong"




### User
- `POST /api/auth/register`：
  用戶註冊，將信箱與密碼(Bcrypt加密)存入資料庫，預設給予 `USER` 權限，寄送驗證碼到使用者信箱(資料庫 `enabled` 欄位，預設 `false`)。
    - 若帳號已存在且完成信箱驗證返回 `400 BAD_REQUEST`；
    - 若帳號已存在，未完成信箱驗證，更新資料庫驗證碼重新寄送。


- `POST /api/auth/verify` ： 使用者輸入驗證碼進行信箱驗證，驗證成功後將 `enabled` 欄位改為 `true` ；驗證失敗，回傳 `400 BAD_REQUEST` 。


- `POST /api/auth/login` ： 用戶登入，檢查是否完成信箱驗證(`enabled = true`)
    - 若已驗證則生成JWT(一小時到期)。
    - 若未信箱驗證或使用者名稱或密碼輸入錯誤，回傳`401 UNAUTHORIZED`


- `GET /oauth2/success` ： 用戶透過Google第三方登入後，後端利用 `@AuthenticationPrincipal OAuth2User` 取得email：
    - 若 email 對應的帳號已存在，則產生JWT，重新導向前端頁面，於 URL 上夾帶token。
    - 若未註冊過，使用email創建一個新用戶，再完成上述動作。


- `GET /api/user/profile` ： 使用者登入後，透過 `Principal` 類別取得使用者名稱，從資料庫查詢並回船使用者資料。若找不到使用者資料，回傳 `401 UNAUTHORIZED`


- `POST /api/address` ： 創建使用者地址
- `DELETE /api/address/{id}` ： 透過地址id刪除地址

### Order

- `POST /api/order` ： 創建訂單，同時建立詳細付款資訊(付款狀態pending)並關聯至該筆訂單，透過 `@Transactional` 確保訂單操作資料庫上的原子性。
    - 若沒提供地址，拋出 `ResourceNotFoundEx`
    - 若付款方式為信用卡，回傳 Stripe 的 `client_secret` ，供前端進行付款流程


- `PUT /api/order/update-payment` ： 使用者信用卡付款完成後，從Stripe取得付款資訊，並更新資料庫的訂單狀態及付款資訊。處理邏輯如下：
    1. **查詢付款資訊：** 透過 `paymentIntentId` 向Stripe API查詢付款詳細內容
        - 使用 `PaymentIntent.retrieve(paymentIntentId)` 向 Stripe API 查詢付款詳細內容。
        - 若查不到此筆付款（可能是 id 錯誤或已被刪除），拋出 `IllegalArgumentException`
    2. **驗證付款狀態：** 確認付款狀態為 `succeeded`
        - 若付款尚未成功，則拋出自定義例外 `PaymentFailedException`，表示付款流程尚未成功，後續無須更新訂單。
    3. **取得 orderId：** 從 Stripe `PaymentIntent `的 `metadata` 欄位中取得 `orderId`。
        - 這個欄位是在建立付款 `PaymentIntent` 時手動加上的附加資料。
        - 若找不到對應的 `orderId`，拋出 `BadRequestException`，提示 Stripe metadata 配置有誤。
    4. **查詢訂單：** 根據 `orderId` 查詢訂單資料庫
        - 若找不到對應的訂單，代表使用者付款的訂單有誤，拋出 `ResourceNotFoundEx`。
    5. **更新付款資訊：** 更新付款資訊（如狀態、付款方式）
        - 透過已關聯的 `Payment` 實體，更新其付款狀態與方式。
        - 關聯關係是既有的，因此不重新設定 `order.setPayment(...)`。
    6. **更新訂單狀態：** 更新訂單本身的狀態與付款方式
        - 將訂單狀態更新為 `IN_PROGRESS`
        - 使用`paymentIntent.getPaymentMethod()`，取得 `PaymentMethod` 物件的ID，並設定到 `payment` 資料表的 `paymentMethod` 欄位，以記錄付款來源
    7. **回傳結果：** 儲存更新後的訂單，並回傳包含訂單 ID 的 Map 給前端
        - 成功後回傳 `Map<String, String>`，其中包含 orderId 作為成功確認資料。



- `PUT /api/order/{id}` ： 根據 id 取得訂單。並透過 `Principal` 驗證使用者身份。 將該筆訂單的 `OrderStatus` 欄位更新為 CANCELED。
    - 若 id 找不到訂單，拋出 `ResourceNotFoundEx`


- `GET /api/order/user` ： 使用 `Principal` 取得使用者建立的所有訂單，並回傳給前端。

### Product

- `GET /api/products` ： 根據前端傳來的查詢參數，使用 `Specification` 動態組合查詢條件：
    1. 若有 `slug` ，直接依據 `slug` 查詢並回傳單一商品。
    2. 如果沒有 `slug`，則依據 `categoryId`（分類）與 `typeId`（商品類型）進行條件式查詢，可單獨或同時使用。
    3. 若三個參數都沒傳，則回傳所有商品。


- `GET /api/products/{id}` ： 透過商品 id ，取得商品資訊。 找不到商品拋出 `ResourceNotFoundEx` 例外


- `POST /api/products/` ： 創建一筆新商品，同時新增多個商品款式(不同顏色、尺寸)以及多個商品圖片(`Product_Resource`)。



- `PUT /api/products/{id}` ：透過商品 id 更新商品的資料(前端透過一同回傳舊的資料)。若找不到拋出 `ResourceNotFoundEx` 。


### Category

- `GET /api/category` ： 取得所有商品分類(男生、女生)，及該分類底下的各類型商品資料(T-shirt、Hoodie)


- `GET /api/category/{id}` ： 透過分類id取得特定分類，及該分類下的各類型商品資料
    - 若找不到拋出 `ResourceNotFoundEx` 例外

- `POST /api/category` ： 新增一個商品分類(男生、女生)，同時可新增該類下的商品類型(T-shirt, Hoodie)


- `PUT /api/category/{id}` ： 透過分類id更新分類資料，並可同時更新分類底下的商品類型資料(T-shirt, Hoodie)
    - 若查無此分類，拋出 `ResourceNotFoundEx` 例外


- `DELETE /api/category/{id}` ： 刪除指定分類，同時刪除分類底下的關聯商品類型





---

## 📌 資料庫結構簡介

<p>
  <img src="images/MyShopERD.png" alt="MyShopERD網站DB關係圖" width="100%" />
</p>

整體資料庫結構以正規化為基礎，降低重複資料增加資料的彈性與可維護性。

### User
- **auth_user_details**：儲存會員基本資料，如 email、姓名、電話、密碼(可自訂加密方式，預設用 `bcrypt` 加密)等。
- **auth_authority**：角色與權限管理，支援不同角色（例如管理員、用戶）。
- **auth_user_authority**：auth_user_details 和 auth_authority中介表。
- **addresses**：會員的收件地址。

### Order
- **orders**：訂單資訊，記錄訂單狀態、付款方式、總金額、地址id、會員id等。
- **order_items**：某個商品的購買明細（商品種類、數量、價格）。每筆訂單對應多個商品項目，且每個商品可對應多個商品項目。
- **payment**：付款詳細資訊，包括金額、日期、狀態、付款方式。

### Category
- **categories**：商品分類(男生、女生)。
- **category_type**：商品類型(T-shirt、Hoodie)。

### Product
- **products**：商品主表，包含名稱、品牌、價格、庫存等。
- **product_variant**：商品款式（如顏色、尺寸）。
- **product_resources**：商品圖片資源。


## 🔗 關聯設計

### @ManyToMany（多對多）
使用中介表 `auth_user_authority`管理多對多關係：
- 一個**會員**（`auth_user_details`）可以有多個**角色權限**（`auth_authority`）。
- 一個**角色權限**（`auth_authority`）可以有多個**會員**（`auth_user_details`）。

### @OneToMany/@ManyToOne(多對一，雙向關聯)
由「**多**」的一方管理關聯：
- 一個**會員**（`auth_user_details`）可以有多個**地址**（`addresses`）、多筆**訂單**（`orders`）。
- 每筆**訂單**（`orders`）可包含多筆**訂單明細**（`order_items`）
- 一個**商品**（`products`）可以有多筆**訂單明細**（`order_items`）、多個**商品款式**（`product_variant`）與多個**圖片資源**（`product_resources`）。
- 一個**商品分類**（`categories`）可以有多個**商品類型**（`category_type`)、多個**商品**（`orders`）。
- 一個**商品類型**（`category_type`)可以有多個**商品**（`orders`）。

### @OneToOne(一對一)
- 每筆**訂單**（`orders`）對應一筆**付款詳細資訊**（`payment`）。(由`payment` 表管理關聯)



## 📦 DTO(資料傳輸物件)

- 使用 DTO 來封裝前端和後端的資料，避免直接暴露資料庫結構。
- 隱藏敏感欄位（如密碼），也方便進行欄位格式轉換與擴充，讓 API 傳輸與內部資料庫解耦。

[//]: # ()
[//]: # (## Product的DTO與Entity轉換)

[//]: # ()
[//]: # (Product表格部分欄位與其他表格存在關聯，DTO與Entity轉換邏輯相對複雜。<u>為了讓Service層乾淨簡潔，並提升轉換邏輯的可讀性</u>，將Product相關的轉換程式碼獨立出來，集中放在mapper資料夾，方便未來擴充與管理。)
[//]: # (DTO與Entity轉換邏輯相對繁雜，<u>為了讓Service層乾淨簡潔，並提升轉換邏輯的可讀性</u>，將較為冗長、複雜的轉換程式碼獨立出來，集中放在mapper資料夾，方便未來擴充與管理)

### Mapper層級封裝 (Entity &harr; DTO)
- 將 DTO 與 Entity 間的轉換邏輯獨立封裝於 `mapper` 資料夾中。
- 原因是：**轉換程式碼大多冗長，若涉及關聯資料處理則更為繁雜，寫在 Service 層會降低可讀性與維護性。**
- 抽出轉換邏輯放置 `Mapper` 使 Service 更專注於商業邏輯，提升程式的**模組化程度與擴展性**。


---

## 👤 用戶註冊

### 🔒 密碼存儲

- 原先採用 `BCryptPasswordEncoder` (雜湊強度12)加密，儲存密碼
- 後續優化：
  1. 用 `DelegatingPasswordEncoder` (由 `PasswordEncoderFactories.createDelegatingPasswordEncoder()` 建立)
  2. 系統根據密碼儲存格式的標記自動選擇合適的加密算法進行驗證，也方便未來擴充。


## 🔐 使用者登入

### 🪪 JWT優化
- 每一個 `request` 都會驗證JWT，並新建一個 `Authentication` 
- 後續優化：
  1. 先檢查是否已經做過JWT驗證，如果有就跳過，避免每次都建立 `Authentication`。
  2. 檢查方式：用 `instanceof` 檢查是否為 `JWTAuthenticationToken` (繼承 `UsernamePasswordAuthenticationToken` )
  
#### 🔧 密鑰管理歷程（HS256 → hardcode → RS256）：
- 初期：採用HS256 簽章JWT，透過`KeyGenerator`動態產生簽章的密鑰。 但伺服器重新啟動時，密鑰會重新生成，造成使用者必須重新登入。

- 中期：將密鑰改為 Hardcode 存放於環境變數中，避免重啟導致 JWT 失效。

- 後期：為提升安全性，進一步改用 RS256 (RSA SHA256) 非對稱式簽章演算法：
  - 使用 OpenSSL 工具 生成的 RSA 公鑰與私鑰
  - 伺服器使用私鑰簽章 JWT，客戶端或驗證方使用公鑰驗證簽章


### 未認證回應處理
- Spring Security 預設下，當使用者未認證存取資源時，回傳html登入頁，不利前端處理。
- 改為呼叫自訂`RestAuthenticationEntryPoint` ，設定 HTTP 狀態碼401回傳統一 JSON 錯誤訊息，讓前端能夠統一處理未認證情況

[//]: # (  透過`exceptionHandling&#40;&#41;`方法)

### ObjectMapper 單例優化
- 原先在`RestAuthenticationEntryPoint` 中每次都用` new ObjectMapper()` 建立新物件。
- `ObjectMapper` 較耗費資源且屬於 stateless 工具類別，不需要每次重建。因此改為由 Spring 管理，使用單例模式託管，減少記憶體浪費與效能開銷。

[//]: # (  不管你呼叫幾次，它都不會記住上一次你給的是誰。所以你可以放心地多人共用同一個 ObjectMapper 實例，可以直接做成單例。 )

---



# 🖼️ 畫面展示

## 查Swagger文件

### 產生 Swagger API 文件，供前端測試使用，也能使用JWT登入
<p>
  <img src="images/swagger/swaggerJwt認證1.png" alt="swaggerJwt認證畫面" width="90%" />
</p>

<p>
<img src="images/swagger/swaggerJwt認證2.png" alt=swaggerJwt認證畫面" width="90%" />
</p>

<p>
  <img src="images/swagger/swaggerJwt認證3.png" alt="swaggerJwt認證畫面" width="90%" />

</p>

<p>
  <img src="images/swagger/swagger1.png" alt="swagger頁面1" width="90%" />

</p>

<p>
  <img src="images/swagger/swagger2.png" alt="swagger頁面2" width="100%"  />
</p>


---

## 使用者註冊流程介紹

### 1. 填寫註冊資料
<p>
  <img src="images/註冊/註冊頁面.png" alt="註冊頁面" width="65%"   />
</p>
使用者在註冊頁面中填寫電子信箱與密碼。

### 2. 送出註冊、寄送驗證碼
<p>
  <img src="images/註冊/驗證碼認證.png" alt="驗證碼認證" width="40%"  style=" margin-right: 10px;" />
  <img src="images/註冊/驗證碼信件.png" alt="驗證信畫面" width="39%"  />
</p>

使用者送出註冊後，系統自動寄送驗證碼至使用者email。


### 3. 驗證成功，註冊完成。
<p>
  <img src="images/註冊/註冊成功.png" alt="註冊成功" width="65%" />
</p>

---

## 登入功能說明

### 一般登入流程：

<p>
  <img src="images/登入/登入頁面.png" alt="登入頁面" width="65%" />
</p>

使用者輸入註冊帳號密碼，<u>透過 **JWT** 驗證身分</u>。

### Google 帳號登入：

<p>
  <img src="images/登入/google登入.png" alt="Google 登入畫面" width="40%" style=" margin-right: 10px;"/>
  <img src="images/登入/google登入2.png" alt="Google 登入成功" width="39%" />
</p>

透過<u>**Google OAuth2**</u>登入，Gmail作為使用者帳號：
1. 若帳號已存在，則綁定到現有帳號
2. 若帳號不存在，自動建立新帳號


### 登入後會自動導回首頁
<p>
  <img src="images/網站首頁.png" alt="網站首頁" width="65%" />
</p>

---

## 結帳流程

### 加入購物車
<p>
  <img src="images/結帳/加入購物車.png" alt="加入購物車" width="65%" />
</p>

### 結帳

<p>
    <img src="images/結帳/結帳1.png" alt="結帳頁面" width="65%" />
</p>

### 付款

<p>
  <img src="images/結帳/結帳2.png" alt="付款" width="65%" />
</p>

### 信用卡付款

<p>
  <img src="images/結帳/信用卡付款.png" alt="信用卡付款" width="20%" />
</p>

### 付款成功

<p>
<img src="images/結帳/結帳成功.png" alt="結帳成功" width="50%" />
</p>

---

## Stripe管理後台畫面

<p>
<img src="images/Stripe/後臺頁面1.png" alt="後臺頁面1" width="70%" />
</p>

Stripe後臺，可以看到此訂單相關資訊

<p>
<img src="images/Stripe/後臺頁面2.png" alt="後臺頁面2" width="70%" />
</p>

後端設定會記錄此訂單id以及使用者id

<p>
<img src="images/Stripe/後臺頁面3.png" alt="後臺頁面3" width="70%" />
</p>

可以透過後臺日誌，查看付款狀況

---

## 商品管理

<p>
  <img src="images/商品管理台/管理台1.png" alt="管理台1" width="40%" style=" margin-right: 10px;"/>
  <img src="images/商品管理台/管理台2.png" alt="管理台2" width="45%"/>
</p>

管理商品控制台

<p>
  <img src="images/商品管理台/新增商品1.png" alt="新增商品1" width="45%" style=" margin-right: 10px;"/>
  <img src="images/商品管理台/新增商品2.png" alt="新增商品2" width="45%"/>
</p>

新增或刪除商品

<p>
  <img src="images/商品管理台/更新後管理台1.png" alt="更新後管理台" width="70%"/>
</p>

商品新建成功！

---

[//]: # (## 常見錯誤)

[//]: # (### 帳號已存在)

[//]: # (<p>)

[//]: # (  <img src="images/常見錯誤/帳號已存在.png" alt="帳號已存在" width="60%" />)

[//]: # (</p>)

[//]: # (帳號已存在，且完成信箱認證，出現「信箱已存在」錯誤提示)

