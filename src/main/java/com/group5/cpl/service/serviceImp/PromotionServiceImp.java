package com.group5.cpl.service.serviceImp;

import com.group5.cpl.model.Promotion;
import com.group5.cpl.model.enums.PromotionStatus;
import com.group5.cpl.repository.PromotionRepository;
import com.group5.cpl.service.PromotionService;
import com.group5.cpl.utils.Promotion_Status_Util;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;



import java.util.Date;
import java.util.List;

@Service
public class PromotionServiceImp implements PromotionService {

    @Autowired
    PromotionRepository promotionRepository;
    @Autowired
    Promotion_Status_Util promotion_status_util;

    @Override
    public Promotion savePromotion(Promotion promotion) {
//       promotion.setStatus(promotionStatusChecker(promotion, PromotionStatus.AVAILABLE));
//        promotion.setStatus(PromotionStatus.AVAILABLE);
       return promotionRepository.saveAndFlush(promotion);

    }

    private PromotionStatus promotionStatusChecker(Promotion promotion, PromotionStatus status) {
        Date currentDate = new Date();
        Date startDate = promotion.getStartDate();
        Date endDate = promotion.getEndDate();

        if (startDate == null || endDate == null || status == null) {
            // Handle the case where either startDate or endDate or state is null
            return PromotionStatus.UNAVAILABLE;
        }
        if ((currentDate.compareTo(startDate) < 0) || endDate.compareTo(currentDate) < 0) {
            // If current date is outside the promotion period, return UNAVAILABLE
            return PromotionStatus.UNAVAILABLE;
        } else {
            // If status is not null and current date is within the promotion period, return the provided status
            return status;
        }
    }

    @Override
    public Promotion getPromotionByID(Long promotionId) {
        return promotionRepository.findById(promotionId).orElseThrow(() -> new EntityNotFoundException("Promotion with id " + promotionId + " not found"));
    }

    //either be a manager or promotion must be open
    @Override
    public Promotion getPromotionByIDChecked(Long promotionId, boolean isManager) {
        Promotion promotion = promotionRepository.findById(promotionId).orElseThrow(() -> new EntityNotFoundException("Promotion with id " + promotionId + " not found"));
        if(isManager || promotion.getStatus().equals(PromotionStatus.AVAILABLE)){
            return promotion;
        }else{
            return null;
        }
    }

    @Override
    public Page<Promotion> getPromotions(String title, Pageable pageable) {
        List<Promotion> oldList = promotionRepository.findAllByStatus(PromotionStatus.AVAILABLE);
        //double check promotion status
        for(Promotion item : oldList){
            item.setStatus(promotionStatusChecker(item, item.getStatus()));
            promotionRepository.saveAndFlush(item);
        }
        return promotionRepository.findAll(title, pageable);
//        return promotionRepository.findAllPaginated(pageable);
    }


    @Override
    public Page<Promotion> getActivePromotions(Pageable pageable){
        return promotionRepository.findAllByStatus(PromotionStatus.AVAILABLE, pageable);
    }

    @Override
    public List<Promotion> getAllActivePromotions(){
        List<Promotion> oldList = promotionRepository.findAllByStatus(PromotionStatus.AVAILABLE);
        //double check promotion status
        for(Promotion item : oldList){
            item.setStatus(promotionStatusChecker(item, item.getStatus()));
            promotionRepository.saveAndFlush(item);
        }
        return promotionRepository.findAllByStatus(PromotionStatus.AVAILABLE);
    }

    @Override
    public Page<Promotion> getUnactivePromotions(Pageable pageable){
        List<Promotion> oldList = promotionRepository.findAllByStatus(PromotionStatus.AVAILABLE);
        //double check promotion status
        for(Promotion item : oldList){
            item.setStatus(promotionStatusChecker(item, item.getStatus()));
            promotionRepository.saveAndFlush(item);
        }
        return promotionRepository.findAllByStatus(PromotionStatus.UNAVAILABLE, pageable);
    }

    @Override
    public Promotion updatePromotion(Promotion promotion, Long promotionId) {
        promotion.setPromotion_id(promotionId);
        promotion.setStatus(promotionStatusChecker(promotion, promotion.getStatus()));
        return promotionRepository.saveAndFlush(promotion);
    }

    @Override
    public Promotion updatePromotionStatus(Long promotionId, PromotionStatus status) {
        Promotion promotion = promotionRepository.findById(promotionId).orElseThrow(() -> new EntityNotFoundException("Promotion with id " + promotionId + " not found"));
        promotion.setStatus(promotionStatusChecker(promotion, status));

        return promotionRepository.saveAndFlush(promotion);
    }




}
