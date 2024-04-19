package com.group5.cpl.utils;

import com.group5.cpl.model.Promotion;
import com.group5.cpl.model.enums.PromotionStatus;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class Promotion_Status_Util {
    public static PromotionStatus promotionStatusChecker(Promotion promotion, PromotionStatus status) {
        Date currentDate = new Date();
        Date startDate = promotion.getStartDate();
        Date endDate = promotion.getEndDate();

        if (startDate == null || endDate == null) {
            // Handle the case where either startDate or endDate is null
            return PromotionStatus.UNAVAILABLE;
        }

        if (status == null || startDate.after(currentDate) || endDate.before(currentDate)) {
            // If status is null or current date is outside the promotion period, return UNAVAILABLE
            return PromotionStatus.UNAVAILABLE;
        } else {
            // If status is not null and current date is within the promotion period, return the provided status
            return status;
        }
    }
}
