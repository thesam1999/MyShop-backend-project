package com.sideproject.myshop.services;

import com.sideproject.myshop.auth.entities.User;
import com.sideproject.myshop.entities.Order;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// Payment Intents 範例
// https://docs.stripe.com/payments/quickstart

// Payment Objects Overview 這裡只使用low-level  PaymentIntent
// https://www.youtube.com/watch?v=CUAY6IQcVQM

// Payment Intents API 的細部解說
// https://docs.stripe.com/payments/payment-intents

// stripe付款流程圖
// https://docs.stripe.com/payments/accept-a-payment?platform=web&ui=elements


// 信用卡資料不會通過你的伺服器，而是應該 由前端直接傳給 Stripe，以確保 安全且符合 PCI DSS 安全規範。
// 後端會建立 PaymentIntent，把使用者訂單的一些資料傳給stripe(例如金額、幣別、metadata(訂單id))，取得 client_secret
//前端 使用 client_secret 搭配 Stripe.js，顯示信用卡輸入欄位
//用戶輸入卡號，前端直接將卡號傳送給 Stripe
//Stripe 回傳付款結果（成功/失敗）

//當一位使用者要進行線上付款時，這段程式會跟 Stripe 後端申請一個「付款請求」，並回傳 client_secret 給前端
@Component
public class PaymentIntentService {

    //  接受一個 Order 物件（訂單），並根據這筆訂單的資訊來建立一個 Stripe 的付款意圖，最後回傳一個 Map，裡面包含 Stripe 給的 client_secret。
    public Map<String, String> createPaymentIntent(Order order) throws StripeException {

        User user = order.getUser();

        // 之後呼叫如 PaymentIntent.create(...) 等 API 時，Stripe SDK 會自動把這個 key 加到 HTTP 請求中，進行身份驗證
        // 類似於登入 Stripe 的「後台管理密碼」， 用來驗證你（開發者）跟 Stripe 的 API 溝通授權
        Stripe.apiKey= System.getenv("STRIPE_SECRET_KEY");

        // 建立一個 metadata（附加資料），把訂單的 ID 加進去，讓 Stripe 的後台可以知道這個金額對應哪筆訂單
        Map<String, String> metaData = new HashMap<>();
        metaData.put("orderId",order.getId().toString()); // 用toString把他轉成字串
        metaData.put("userId", user.getId().toString()); // 把用戶id也存進去


        PaymentIntentCreateParams paymentIntentCreateParams= PaymentIntentCreateParams.builder()
                //設定這筆訂單金額
                // Stripe 為了統一格式，要求所有金額都要以最小單位呈現：
                //台幣、美元、歐元這些是 兩位小數 → 要乘以 100
                //日本日圓（JPY）、韓圓（KRW）這些是 零小數 → 不用乘，以「元」當最小單位
                // BigDecimal不支援加減乘除，要改用方法！
                // Stripe amount只支援long要做轉換！
                .setAmount(order.getTotalAmount().multiply(BigDecimal.valueOf(100)).longValue()) //去除小數點部分
                .setCurrency("usd")
                // 設定這筆付款的描述和 metadata（會出現在 Stripe 的後台）
                .putAllMetadata(metaData)
                .setDescription("Test Payment Project ")
                // 啟用自動付款方式（Stripe 會自動幫你選擇支援的付款方式，像是信用卡、UPI、網銀等）
                // 可以透過後臺界面更新與設定支付方式，無須程式碼
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods
                                .builder()
                                .setEnabled(true)
                                .build()
                )
                .build();


        // 建立 PaymentIntent，呼叫 Stripe 的伺服器 API， 把前面準備好的paymentIntentCreateParams（付款的參數，例如金額、幣別、metadata），傳給Stripe伺服器，並取得 client_secret
        //用 Stripe 的 SDK 建立付款意圖，會自動生成client_Secret
        PaymentIntent paymentIntent = PaymentIntent.create(paymentIntentCreateParams);

        Map<String, String> map = new HashMap<>();
        // 從 Stripe 回傳的 PaymentIntent 物件中取得 client_secret，這是一段機密字串，前端會用它來完成付款流程。
        map.put("client_secret", paymentIntent.getClientSecret());
//        System.out.println(map);
        return map;
    }
}