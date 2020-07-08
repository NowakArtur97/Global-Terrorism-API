package com.NowakArtur97.GlobalTerrorismAPI.validator;

import com.NowakArtur97.GlobalTerrorismAPI.annotation.validation.ValidPasswords;
import com.NowakArtur97.GlobalTerrorismAPI.dto.UserDTO;
import org.passay.*;
import org.passay.dictionary.WordListDictionary;
import org.passay.dictionary.WordLists;
import org.passay.dictionary.sort.ArraysSort;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class PasswordsConstraintValidator implements ConstraintValidator<ValidPasswords, Object> {

    private static final String PASSAY_PROPERTIES_FILE = "/validation/passay.properties";
    private static final String COMMON_PASSWORDS_LIST = "/validation/common-passwords-list.txt";

    private DictionaryRule notCommonPasswordRule;

    private MessageResolver customMessagesResolver;

    @Override
    public void initialize(ValidPasswords constraintAnnotation) {

        loadCustomPassayMessages();

        loadCommonPasswordsList();
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {

        UserDTO user = (UserDTO) obj;

        String userName = user.getUserName();
        String password = user.getPassword();
        String matchingPassword = user.getMatchingPassword();

        if (password == null || matchingPassword == null) {
            return false;
        }

        PasswordValidator validator = new PasswordValidator(customMessagesResolver, defineRules());

        PasswordData passwordData = new PasswordData(password);
        PasswordData matchingPasswordData = new PasswordData(matchingPassword);

        passwordData.setUsername(userName);
        matchingPasswordData.setUsername(userName);

        RuleResult passwordResult = validator.validate(passwordData);
        RuleResult matchingPasswordResult = validator.validate(matchingPasswordData);

        if (passwordResult.isValid() && matchingPasswordResult.isValid()) {
            return true;
        }

        List<String> passwordResultMessages = validator.getMessages(passwordResult);
        List<String> matchingPasswordResultMessages = validator.getMessages(matchingPasswordResult);

        passwordResultMessages.addAll(matchingPasswordResultMessages);

        context.disableDefaultConstraintViolation();

        for (String message : matchingPasswordResultMessages) {

            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(message)
                    .addConstraintViolation();
        }

        return false;
    }

    private List<Rule> defineRules() {

        CharacterCharacteristicsRule characterCharacteristicsRule = new CharacterCharacteristicsRule();
        characterCharacteristicsRule.setNumberOfCharacteristics(2);

        characterCharacteristicsRule.getRules().add(new CharacterRule(EnglishCharacterData.UpperCase, 1));
        characterCharacteristicsRule.getRules().add(new CharacterRule(EnglishCharacterData.LowerCase, 1));
        characterCharacteristicsRule.getRules().add(new CharacterRule(EnglishCharacterData.Digit, 1));
        characterCharacteristicsRule.getRules().add(new CharacterRule(EnglishCharacterData.Special, 1));

        return Arrays.asList(
                new LengthRule(7, 30),
                new WhitespaceRule(),
                notCommonPasswordRule,
                new UsernameRule(true, true, MatchBehavior.Contains),
                new RepeatCharacterRegexRule(3, true),
                characterCharacteristicsRule
        );
    }


    private void loadCustomPassayMessages() {

        Properties props = new Properties();
        URL resource = this.getClass().getResource(PASSAY_PROPERTIES_FILE);

        try {
            props.load(new FileInputStream(resource.getPath()));

        } catch (IOException exception) {
            throw new RuntimeException("Could not find passay properties file with custom messages ", exception);
        }

        customMessagesResolver = new PropertiesMessageResolver(props);
    }

    private void loadCommonPasswordsList() {

        try {
            String invalidPasswordList = this.getClass().getResource(COMMON_PASSWORDS_LIST).getFile();

            notCommonPasswordRule = new DictionaryRule(
                    new WordListDictionary(WordLists.createFromReader(
                            new FileReader[]{
                                    new FileReader(invalidPasswordList)
                            },
                            false,
                            new ArraysSort()
                    )));
        } catch (IOException exception) {
            throw new RuntimeException("Could not find list of common passwords ", exception);
        }
    }
}
