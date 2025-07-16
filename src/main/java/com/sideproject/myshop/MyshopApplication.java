package com.sideproject.myshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;

@SpringBootApplication
public class MyshopApplication {


	//設定在intellij裡，所以這裡不需要
	// 從設定檔（像 application.properties 或 .env）中，讀取 stripe.secret 這個設定值，然後注入（放進）這個變數 stripeSecret。
//	@Value("${stripe.secret}")
//	private String stripeSecret;

	public static void main(String[] args) {
		SpringApplication.run(MyshopApplication.class, args);
	}

	// 這是一個生命週期註解，意思是：
	//當這個類別建立並完成依賴注入（例如 @Value 注入）後，自動執行 init() 方法一次。換句話說：Spring 幫你 new 好物件、幫你設定好 stripeSecret，然後會自動呼叫你寫的 init() 方法
//	@PostConstruct
//	public void init(){
//		Stripe.apiKey = this.stripeSecret;
//	}


	// 把這個放在@Configuration裡

	// @CrossOrigin只能簡單設定，複雜設定要靠這個

	// 「允許外部網站或前端程式來訪問你這個 Spring Boot 後端的 API」，也就是設定 跨來源資源共享（CORS）規則。
	@Bean
	public CorsFilter corsFilter() {

		// 建立一個 CorsConfiguration（CORS 設定規則），並搭配 URL 匹配器 source，讓你可以對所有路徑都套用這個 CORS 規則。
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		// ✅ 允許 所有來源 都可以訪問這個 API（如 localhost:3000, frontend.com...）。
		//但在正式環境下不安全，要限制特定網站。
		CorsConfiguration config = new CorsConfiguration();

		// ✅ 允許 所有來源 都可以訪問這個 API（如 localhost:3000, frontend.com...）。
		// 但在正式環境下不安全，要限制特定網站。
		config.setAllowedOriginPatterns(Collections.singletonList("*")); // Insecure, but for demo purposes it's ok

		// 表示允許前端傳送這些「請求標頭（Request Header）」，也就是：
		//前端發送的 headers（例如 JWT Token）會被接受。
		config.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "responseType", "Authorization", "x-authorization", "content-range","range"));

		// 允許使用這些 HTTP 方法對你的 API 發送請求。
		//OPTIONS 是瀏覽器在跨域請求前自動發出的「預檢請求」，非常重要！
		config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH"));

		// 之前admin panel的錯誤訊息就跟這有關：「前端讀不到 Content-Range」→ 你就需要把 content-range 加進 ExposedHeaders

		// 表示「允許前端讀取」這些「回應標頭（Response Header）」
		// 例如：content-range: 分頁用、X-Total-Count: 總筆數、Content-Type: 回傳資料型別、沒寫在這裡的話，瀏覽器雖然收到資料，卻會拒絕讓 JavaScript 存取這些欄位。
		config.setExposedHeaders(Arrays.asList("X-Total-Count", "X-Total-Count", "content-range", "Content-Type", "Accept", "X-Requested-With", "remember-me"));

		// 將這個 CORS 設定套用到你所有的 API 路徑（/**）
		// 回傳一個 CorsFilter 物件，讓 Spring Boot 在處理每一個請求之前，先進行 CORS 驗證。
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}

}
