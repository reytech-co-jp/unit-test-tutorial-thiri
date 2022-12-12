package com.example.demo.form;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class AnimeFormTest {

    @Test
    void 正しい値を入力した時にバリデーションエラーとならないこと() {
        AnimeForm animeForm = new AnimeForm();
        animeForm.setName("Your Name");
        animeForm.setGenre("Romantic");

        Set<ConstraintViolation<AnimeForm>> violations =
                Validation
                        .buildDefaultValidatorFactory()
                        .getValidator()
                        .validate(animeForm);
        assertThat(violations.size()).isEqualTo(0);
    }

    @Test
    void nullを入力した時にバリデーションエラーとなること() {
        AnimeForm animeForm = new AnimeForm();
        animeForm.setName(null);
        animeForm.setGenre(null);

        Set<ConstraintViolation<AnimeForm>> violations =
                Validation
                        .buildDefaultValidatorFactory()
                        .getValidator()
                        .validate(animeForm);
        assertThat(violations.size()).isEqualTo(2);

        assertThat(violations)
                .extracting(
                        propertyPath -> propertyPath.getPropertyPath().toString(),
                        message -> message.getMessage())
                .containsOnly(
                        tuple("name", "cannot be empty"),
                        tuple("genre", "cannot be empty")
                );
    }

    @Test
    void 空文字を入力した時にバリデーションエラーとなること() {
        AnimeForm animeForm = new AnimeForm();
        animeForm.setName("");
        animeForm.setGenre("");

        Set<ConstraintViolation<AnimeForm>> violations =
                Validation
                        .buildDefaultValidatorFactory()
                        .getValidator()
                        .validate(animeForm);
        assertThat(violations.size()).isEqualTo(2);

        assertThat(violations)
                .extracting(
                        propertyPath -> propertyPath.getPropertyPath().toString(),
                        message -> message.getMessage())
                .containsOnly(
                        tuple("name", "cannot be empty"),
                        tuple("genre", "cannot be empty")
                );
    }
}
