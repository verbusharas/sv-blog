package lt.verbus.svblog.util;

import lt.verbus.svblog.annotation.ValidPassword;
import lombok.SneakyThrows;
import org.passay.*;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;


/**
 * <h2>PasswordConstraintValidator</h2>
 *
 * @author aek
 */
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(final ValidPassword arg0) {

    }

    @SneakyThrows
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
//        "passay.properties"
        //customizing validation messages
        String language = LocaleContextHolder.getLocale().getLanguage();
        String propertiesFilename = language.equals("en") ? "validationMessages.properties" : "validationMessages_" + language +".properties";
        Properties props = new Properties();
        InputStream inputStream = getClass()
                .getClassLoader().getResourceAsStream(propertiesFilename);
        props.load(inputStream);
        MessageResolver resolver = new PropertiesMessageResolver(props);

        PasswordValidator validator = new PasswordValidator(resolver, Arrays.asList(

                // length between 8 and 16 characters
                new LengthRule(8, 200),

                // at least one upper-case character
                new CharacterRule(EnglishCharacterData.UpperCase, 1),

                // at least one lower-case character
                new CharacterRule(EnglishCharacterData.LowerCase, 1),

                // at least one digit character
                new CharacterRule(EnglishCharacterData.Digit, 1),

                // at least one symbol (special character)
                new CharacterRule(EnglishCharacterData.Special, 1),

                // no whitespace
                new WhitespaceRule(),

                // rejects passwords that contain a sequence of >= 5 characters alphabetical  (e.g. abcdef)
                new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 5, false),
                // rejects passwords that contain a sequence of >= 5 characters numerical   (e.g. 12345)
                new IllegalSequenceRule(EnglishSequenceData.Numerical, 5, false)
        ));

        RuleResult result = validator.validate(new PasswordData(password));

        if (result.isValid()) {
            return true;
        }

        List<String> messages = validator.getMessages(result);
//        messages.add(0, "{password.validator.prefix} ");
        String joinedMessage = String.join(", ", messages);
        String messageTemplate = "{password.validator.prefix}: " + joinedMessage;
        context.buildConstraintViolationWithTemplate(messageTemplate)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();
        return false;
    }

}