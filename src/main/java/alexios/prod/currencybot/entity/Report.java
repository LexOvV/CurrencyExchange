package alexios.prod.currencybot.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Report {
    @JsonProperty("Date")
    private LocalDateTime date;
    @JsonProperty("PreviousDate")
    private LocalDateTime previousDate;
    @JsonProperty("PreviousURL")
    private String previousURL;
    @JsonProperty("Timestamp")
    private LocalDateTime timestamp;
    @JsonProperty("Valute")
    private List<Valute> valute;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return date.equals(report.date) && previousDate.equals(report.previousDate) &&
                previousURL.equals(report.previousURL) && timestamp.equals(report.timestamp) && valute.equals(report.valute);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, previousDate, previousURL, timestamp);
    }

    @Override
    public String toString() {
        return "Report{" +
                "date=" + date +
                ", previousDate=" + previousDate +
                ", previousURL='" + previousURL + '\'' +
                ", timestamp=" + timestamp +
                ", valute=" + valute +
                '}';
    }
}
