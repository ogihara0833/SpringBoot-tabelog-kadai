package com.example.nagoyameshi.service;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.nagoyameshi.entity.User;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

@Service
public class MembershipStripeService {

    @Value("${stripe.api-key}")
    private String stripeApiKey;

    public String createMembershipSession(User user, HttpServletRequest request) {
        Stripe.apiKey = stripeApiKey;

        String requestUrl = request.getRequestURL().toString();


        SessionCreateParams params = SessionCreateParams.builder()
        	    .setClientReferenceId(String.valueOf(user.getId()))
        	    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
        	    .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
        	    .addLineItem(
        	        SessionCreateParams.LineItem.builder()
        	            .setPrice("price_1RiqUIB1nhrCgbjCGwlpsKSw")
        	            .setQuantity(1L)
        	            .build())
        	    .setSuccessUrl(requestUrl.replace("/upgrade", "/complete") + "?session_id={CHECKOUT_SESSION_ID}")
        	    .setCancelUrl(requestUrl.replace("/upgrade", "/cancel"))
        	    .build();

        try {
            Session session = Session.create(params);
            return session.getId();
        } catch (StripeException e) {
            e.printStackTrace();
            return "";
        }
    }
    
    public String retrieveSubscriptionId(String sessionId) {
        Stripe.apiKey = stripeApiKey;

        try {
            Session session = Session.retrieve(sessionId);

            return session.getSubscription(); // StripeからsubscriptionIdを取得！
        } catch (StripeException e) {
            e.printStackTrace();
            return null; 
        }
    }
    
    public void cancelSubscription(String subscriptionId) {
        Stripe.apiKey = stripeApiKey;

        try {
            Subscription subscription = Subscription.retrieve(subscriptionId);

            subscription.cancel(); 
            System.out.println("✅ Stripe契約キャンセル成功: " + subscriptionId);
        } catch (StripeException e) {
            System.out.println("❌ Stripe契約キャンセル失敗: " + e.getMessage());
        }
    }
}
