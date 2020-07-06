package com.NowakArtur97.GlobalTerrorismAPI.validator;

import com.NowakArtur97.GlobalTerrorismAPI.annotation.validation.ValidPasswords;
import com.NowakArtur97.GlobalTerrorismAPI.dto.UserDTO;
import org.passay.*;
import org.passay.dictionary.WordListDictionary;
import org.passay.dictionary.WordLists;
import org.passay.dictionary.sort.ArraysSort;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PasswordsConstraintValidator implements ConstraintValidator<ValidPasswords, Object> {

    private DictionaryRule notCommonPasswordRule;

    @Override
    public void initialize(ValidPasswords constraintAnnotation) {

        try {
            String invalidPasswordList = this.getClass().getResource("/validation/common-passwords-list.txt").getFile();

            notCommonPasswordRule = new DictionaryRule(
                    new WordListDictionary(WordLists.createFromReader(
                            new FileReader[]{
                                    new FileReader(invalidPasswordList)
                            },
                            false,
                            new ArraysSort()
                    )));
        } catch (IOException exception) {
            throw new RuntimeException("Could not find list of common passwords", exception);
        }
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

        PasswordValidator validator = new PasswordValidator(Arrays.asList(
                new LengthRule(7, 30),
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.Special, 1),
                new WhitespaceRule(),
                new UsernameRule(),
                notCommonPasswordRule
        ));

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

        String messageTemplate = passwordResultMessages
                .stream()
                .distinct()
                .collect(Collectors.joining(","));

        context.buildConstraintViolationWithTemplate(messageTemplate)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();

        return false;
    }
}
