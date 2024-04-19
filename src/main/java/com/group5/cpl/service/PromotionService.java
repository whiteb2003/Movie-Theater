package com.group5.cpl.service;

import com.group5.cpl.model.*;

import com.group5.cpl.model.enums.PromotionStatus;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface PromotionService {

    public Promotion savePromotion(Promotion promotion);
    public Promotion getPromotionByID(Long promotionId);
    public Promotion getPromotionByIDChecked(Long promotionId, boolean isManager);
    public Page<Promotion> getPromotions(String title, Pageable pageable);
    public Promotion updatePromotion(Promotion promotion, Long promotionId);
    public Promotion updatePromotionStatus(Long promotionId, PromotionStatus status);
    public Page<Promotion> getActivePromotions(Pageable pageable);
    public List<Promotion> getAllActivePromotions();
    public Page<Promotion> getUnactivePromotions(Pageable pageable);

}
