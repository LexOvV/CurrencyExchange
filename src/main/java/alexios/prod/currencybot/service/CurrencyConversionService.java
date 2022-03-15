package alexios.prod.currencybot.service;

import alexios.prod.currencybot.entities.Currency;
import alexios.prod.currencybot.service.implementations.NbrbCurrencyConversionService;

import java.io.IOException;

public interface CurrencyConversionService {

    static CurrencyConversionService getInstance() {
        return new NbrbCurrencyConversionService();
    }

    double getConversionRatio(Currency original, Currency target) throws IOException;
}