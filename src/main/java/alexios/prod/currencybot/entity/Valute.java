package alexios.prod.currencybot.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Valute {
    @JsonProperty("ID")
    private long id;
    @JsonProperty("NumCode")
    private long numCode;
    @JsonProperty("CharCode")
    private Currency charCode;
    @JsonProperty("Nominal")
    private int nominal;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Value")
    private double value;
    @JsonProperty("Previous")
    private double previous;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Valute valute = (Valute) o;
        return id == valute.id && numCode == valute.numCode && nominal == valute.nominal &&
                Double.compare(valute.value, value) == 0 && Double.compare(valute.previous, previous) == 0 &&
                charCode == valute.charCode && Objects.equals(name, valute.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, numCode, value, previous);
    }

    @Override
    public String toString() {
        return "Valute{" +
                "id=" + id +
                ", numCode=" + numCode +
                ", charCode=" + charCode +
                ", nominal=" + nominal +
                ", name='" + name + '\'' +
                ", value=" + value +
                ", previous=" + previous +
                '}';
    }
}
