package alexios.prod.currencybot.service;

import alexios.prod.currencybot.entity.Currency;
import alexios.prod.currencybot.service.implementations.HashMapCurrencyModeService;

public interface CurrencyModeService {

    static CurrencyModeService getInstance() {
        return new HashMapCurrencyModeService();
    }

    Currency getOriginalCurrency(long chatId);

    Currency getTargetCurrency(long chatId);

    void setOriginalCurrency(long chatId, Currency currency);

    void setTargetCurrency(long chatId, Currency currency);
}
