package io.watchdog.samples.provider.user_center.domain.member.repository.jpa;

import io.watchdog.samples.provider.user_center.domain.member.Gender;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * @author le
 * @since v0.1.0
 */
@Converter(autoApply = true)
public class GenderConverter implements AttributeConverter<Gender, String> {

    @Override
    public String convertToDatabaseColumn(Gender attribute) {
        if (attribute == null)
            return null;
        return attribute.name();
    }

    @Override
    public Gender convertToEntityAttribute(String dbData) {
        if (dbData == null)
            return null;
        return Gender.valueOf(dbData);
    }

}
