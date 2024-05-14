package com.example.diploma.models;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum DeliveryPayment {
    ALL_POSSIBLE, UPON_DELIVERY, PARTIAL_PAYMENT, HALF_PAYMENT, FULL_PAYMENT;
     private static final Map<DeliveryPayment, String> russianNames = new HashMap<>();
        static {
            russianNames.put(ALL_POSSIBLE, "Всевозможные");
            russianNames.put(UPON_DELIVERY, "По факту поставки");
            russianNames.put( PARTIAL_PAYMENT, "25% предоплаты");
            russianNames.put(HALF_PAYMENT, "50% предоплаты");
            russianNames.put(FULL_PAYMENT, "100% предоплаты");
        }
        public static Map<DeliveryPayment, String> getRussianName()
        {
            return russianNames;
        }
}
