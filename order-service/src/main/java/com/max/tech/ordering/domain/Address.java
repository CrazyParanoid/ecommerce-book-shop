package com.max.tech.ordering.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.max.tech.ordering.domain.common.IdentifiedDomainObject;
import com.max.tech.ordering.domain.common.ValueObject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Getter
@AllArgsConstructor
@Table(name = "addresses")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address extends IdentifiedDomainObject implements ValueObject {
    @Column(name = "city", columnDefinition = "TEXT")
    private String city;
    @Column(name = "street", columnDefinition = "TEXT")
    private String street;
    @Column(name = "house", columnDefinition = "TEXT")
    private String house;
    private Integer flat;
    private Integer floor;
    private Integer entrance;

    public void validate() {
        if (StringUtils.isBlank(this.city))
            throw new IllegalArgumentException("city can't be null or empty");
        if (StringUtils.isBlank(this.house))
            throw new IllegalArgumentException("house can't be null or empty");
        if (StringUtils.isBlank(this.street))
            throw new IllegalArgumentException("street can't be null or empty");
    }

    @Override
    public String toString() {
        return this.city + ", ул. "
                + this.street + ", д."
                + this.house + ", "
                + formFlat()
                + formFloor()
                + formEntrance();
    }

    private String formFlat() {
        if(Objects.isNull(this.flat)) return StringUtils.SPACE;
        return "кв. " + this.flat + ",";
    }

    private String formFloor(){
        if(Objects.isNull(this.floor)) return StringUtils.SPACE;
        return StringUtils.SPACE + this.floor + " этаж, ";
    }

    private String formEntrance(){
        if(Objects.isNull(this.entrance)) return StringUtils.SPACE;
        return this.entrance + " подъезд";
    }

}
