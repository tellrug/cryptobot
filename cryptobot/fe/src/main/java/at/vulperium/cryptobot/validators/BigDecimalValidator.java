package at.vulperium.cryptobot.validators;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;

import java.math.BigDecimal;

/**
 * Created by 02ub0400 on 04.01.2018.
 */
public class BigDecimalValidator implements Validator<String> {

    @Override
    public ValidationResult apply(String s, ValueContext valueContext) {
        if (s == null) {
            return ValidationResult.ok();
        }

        //Versuch in ein BigDecimal zu transformieren
        BigDecimal tmp;
        try {
            tmp = new BigDecimal(s);
        }
        catch (Exception e) {
            return ValidationResult.error("Es sind nur nummerische Werte erlaubt!");
        }

        if (tmp == null) {
            return ValidationResult.error("Es sind nur nummerische Werte erlaubt!");
        }
        return ValidationResult.ok();
    }
}
