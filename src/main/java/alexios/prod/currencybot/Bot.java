package alexios.prod.currencybot;

import alexios.prod.currencybot.entity.Currency;
import alexios.prod.currencybot.service.CurrencyConversionService;
import alexios.prod.currencybot.service.CurrencyModeService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

//TODO: Лист объектов валют. Один раз парсим в поле класса и потом достаем по мере вызовов какие нужны
public class Bot extends TelegramLongPollingBot {
    private final CurrencyModeService currencyModeService = CurrencyModeService.getInstance();
    private final CurrencyConversionService currencyConversionService = CurrencyConversionService.getInstance();

    public static void main(String[] args) throws TelegramApiException {
        Bot bot = new Bot();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(bot);
    }

    @Override
    public String getBotUsername() {
        return "@MJCTestTrainBot";
    }

    @Override
    public String getBotToken() {
        return "5197833559:AAGfJ365cASNwjtKAe5qgi60raqDqTOAUdE";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            try {
                handleCallback(update.getCallbackQuery());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if (update.hasMessage()) {
            try {
                handleMessage(update.getMessage());
            } catch (TelegramApiException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleCallback(CallbackQuery callbackQuery) throws TelegramApiException {
        Message message = callbackQuery.getMessage();
        String[] param = callbackQuery.getData().split(":");
        String action = param[0];
        Currency newCurrency = Currency.valueOf(param[1]);
        switch (action) {
            case "ORIGINAL":
                currencyModeService.setOriginalCurrency(message.getChatId(), newCurrency);
                break;
            case "TARGET":
                currencyModeService.setTargetCurrency(message.getChatId(), newCurrency);
                break;
        }

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        Currency originalCurrency = currencyModeService.getOriginalCurrency(message.getChatId());
        Currency targetCurrency = currencyModeService.getTargetCurrency(message.getChatId());

        for (Currency currency : Currency.values()) {
            buttons.add(Arrays.asList(InlineKeyboardButton.builder()
                            .text(getCurrencyButton(originalCurrency, currency))
                            .callbackData("ORIGINAL:" + currency)
                            .build(),
                    InlineKeyboardButton.builder()
                            .text(getCurrencyButton(targetCurrency, currency))
                            .callbackData("TARGET:" + currency)
                            .build()));
        }

        execute(EditMessageReplyMarkup.builder()
                .chatId(message.getChatId().toString())
                .messageId(message.getMessageId())
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboard(buttons)
                        .build())
                .build());
    }

    private void handleMessage(Message message) throws TelegramApiException, IOException {
        //handle command
        if (message.hasText() && message.hasEntities()) {
            Optional<MessageEntity> commandEntity = message.getEntities().stream()
                    .filter(messageEntity -> "bot_command".equals(messageEntity.getType()))
                    .findFirst();
            if (commandEntity.isPresent()) {
                String command = message.getText().substring(commandEntity.get().getOffset(), commandEntity.get().getLength());
                executeCommand(command, message);
            }
        }
        if (message.hasText()) {
            String text = message.getText();
            Optional<Double> value = parseDouble(text);
            Currency originalCurrency = currencyModeService.getOriginalCurrency(message.getChatId());
            Currency targetCurrency = currencyModeService.getTargetCurrency(message.getChatId());
            double conversionRatio = currencyConversionService.getConversionRatio(originalCurrency, targetCurrency);
            if (value.isPresent()) {
                execute(SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text(String.format("%4.2f %s is %4.2f %s",
                                value.get(), originalCurrency, (value.get() * conversionRatio), targetCurrency))
                        .build());
                return;
            }
        }
    }

    private Optional<Double> parseDouble(String text) {
        try {
            return Optional.of(Double.parseDouble(text));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private void executeCommand(String command, Message message) throws TelegramApiException {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        Currency originalCurrency = currencyModeService.getOriginalCurrency(message.getChatId());
        Currency targetCurrency = currencyModeService.getTargetCurrency(message.getChatId());

        for (Currency currency : Currency.values()) {
            buttons.add(Arrays.asList(InlineKeyboardButton.builder()
                            .text(getCurrencyButton(originalCurrency, currency))
                            .callbackData("ORIGINAL:" + currency)
                            .build(),
                    InlineKeyboardButton.builder()
                            .text(getCurrencyButton(targetCurrency, currency))
                            .callbackData("TARGET:" + currency)
                            .build()));
        }

        switch (command) {
            case "/set_currency":
                execute(SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text("Please choose Original and Target currencies")
                        .replyMarkup(InlineKeyboardMarkup.builder()
                                .keyboard(buttons)
                                .build())
                        .build());
                return;
        }
    }

    private String getCurrencyButton(Currency saved, Currency current) {
        if (saved.equals(current)) {
            return current + " ✅";
        } else {
            return current.name();
        }
    }
}