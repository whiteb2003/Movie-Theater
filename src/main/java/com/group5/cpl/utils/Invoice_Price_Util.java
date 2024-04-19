package com.group5.cpl.utils;

import com.group5.cpl.model.Invoice;
import com.group5.cpl.model.Promotion;
import com.group5.cpl.model.Ticket;
import com.group5.cpl.model.enums.PromotionStatus;
import com.group5.cpl.model.enums.TicketStatus;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class Invoice_Price_Util {

    //Price of each seat
    public Double seatPriceCollector(Ticket ticket){
        return ticket.getSeat().getType().getPrice();
    }

    //Price of seat apply promo
    public Double seatPriceWithPromo(Ticket ticket, Promotion promotion){
        Double seatPrice = seatPriceCollector(ticket);
        seatPrice = applySinglePromotion(seatPrice, promotion);
        return seatPrice;
    }

    //Price of seats apply promo
    public Double ticketsPriceWithPromo(List<Ticket> tickets, Promotion promotion){
        Double sum = 0.0;
        for(Ticket item : tickets){
            Double seatPrice = seatPriceWithPromo(item, promotion);
            sum += seatPrice;
        }
        return sum;
    }

    //Apply promo
    public Double applySinglePromotion(Double price, Promotion promotion){
        if(promotion == null) return price;
        if(promotion.getStatus().equals(PromotionStatus.UNAVAILABLE)) return price;
        Double percentage = promotion.getDiscountPercentage();
        Double value = promotion.getDiscountValue();
        if(percentage != null && value != null){
            Double deduct = (price + value) * percentage / 100;
            price = price - deduct;
        }else if(percentage != null){
            Double deduct = price * percentage / 100;
            price = price - deduct;
        } else if(value != null){
            price = price - value;
        }
        if(price < 0) return 0.0;
        return price;
    }


    public Double seatsPriceCollector(List<Ticket> tickets){
        Double sum = 0.0;
        for(Ticket item : tickets){
            Double price = seatPriceCollector(item);
            sum += price;
        }
        return sum;
    }

    public Double paidTicketsCollector(Invoice invoice){
        List<Ticket> paidTickets = invoice.getTicketList().stream()
                .filter(ticket -> ticket.getStatus() == TicketStatus.PAID)
                .collect(Collectors.toList());
        Double price = ticketsPriceWithPromo(paidTickets, invoice.getPromotion());
        return price;
    }

    public String priceToStringConverter(Double price){
        if (price == null) {
            return "0 VND"; // or throw an exception, depending on your requirements
        }
        DecimalFormat formatter = new DecimalFormat("#,###");
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);
        // Convert the price to VND format
        String formattedPrice = formatter.format(price);

        // Append "VND" to the formatted price
        return formattedPrice + " VND";
    }







}
