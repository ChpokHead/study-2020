package com.bank.service.impl;

import com.bank.domain.User;
import com.bank.repository.UserRepository;
import com.bank.service.PasswordEncriptor;
import com.bank.service.validator.Validator;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {
    private static final String ENCODED_PASSWORD = "encoded_password";
    private static final User USER =
            User.builder()
                    .withPassword(ENCODED_PASSWORD)
                    .build();
    private static final String PASSWORD = "password";
    private static final String USER_EMAIL = "user@gmail.com";
    private static final String INCORRECT_PASSWORD = "INCORRECT_PASSWORD";
    private static final String ENCODE_INCORRECT_PASSWORD = "encode_incorrect_password";

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncriptor passwordEncriptor;
    @Mock
    private Validator<User> userValidator;

    @InjectMocks
    private UserServiceImpl userService;

    @After
    public void resetMocks() {
        reset(userRepository, passwordEncriptor, userValidator);
    }

    @Test
    public void userShouldLoginSuccessfully() {
        when(passwordEncriptor.encript(eq(PASSWORD))).thenReturn(ENCODED_PASSWORD);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(USER));

        final boolean isLogin = userService.login(USER_EMAIL, PASSWORD);

        assertTrue(isLogin);
        verify(passwordEncriptor).encript(eq(PASSWORD));
        verify(userRepository).findByEmail(eq(USER_EMAIL));
        verifyZeroInteractions(userValidator);
    }

    @Test
    public void userShouldNotLoginAsThereIsNotUserWithSuchEmail() {
        when(passwordEncriptor.encript(eq(PASSWORD))).thenReturn(ENCODED_PASSWORD);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        final boolean isLogin = userService.login(USER_EMAIL, PASSWORD);

        assertFalse(isLogin);
        verify(passwordEncriptor).encript(eq(PASSWORD));
        verify(userRepository).findByEmail(eq(USER_EMAIL));
        verifyZeroInteractions(userValidator);
    }

    @Test
    public void userShouldNotLoginAsPasswordIsIncorrect() {
        when(passwordEncriptor.encript(eq(INCORRECT_PASSWORD))).thenReturn(ENCODE_INCORRECT_PASSWORD);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(USER));

        final boolean isLogin = userService.login(USER_EMAIL, INCORRECT_PASSWORD);

        assertFalse(isLogin);
        verify(passwordEncriptor).encript(eq("INCORRECT_PASSWORD"));
        verify(userRepository).findByEmail(eq(USER_EMAIL));
        verifyZeroInteractions(userValidator);
    }


}