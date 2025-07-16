package com.sideproject.myshop.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import io.swagger.v3.oas.models.security.SecurityScheme.In;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI(){

        // 建立 SecurityScheme (Bearer JWT)，定義這個驗證的名稱！
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("MyShop API")
                        .description("MyShop E-commerce Application API")
                        .version("1.0")
                        .contact(new Contact().name("The CodeReveal")))
                // 「整份 API 文件的所有API都要使用"bearerAuth"的安全要求（Security Requirement）」，
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                // 建立一個新的 Components 物件，OpenAPI 文件中用來「集中管理可重複使用的物件」的區域
                .components(new Components()
                        // 往 components.securitySchemes 這個欄位新增一個安全方案定義。
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName) //名稱
                                        .type(Type.HTTP) //  HTTP 認證方式，表示透過 HTTP header 傳遞認證資料
                                        .scheme("bearer") // !!定義認證方式!!，指定使用 Bearer token 認證，也就是類似 Authorization: Bearer <token>
                                        .bearerFormat("JWT") //補充說明格式，非必要的
                                        .in(In.HEADER))); // Token 是放在 HTTP 請求的 Header 內，不是在 query 或 cookie 裡
    }
}
