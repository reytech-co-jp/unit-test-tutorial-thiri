package com.example.demo.integrationtest;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static net.javacrumbs.jsonunit.assertj.JsonAssert.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DBRider
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AnimeIntegrationTest {

    @Autowired
    MockMvc mvc;

    ZonedDateTime zonedDateTime = ZonedDateTime.of(2022, 12, 13, 0, 0, 0, 0, ZoneId.of("Asia/Tokyo"));

    @Test
    @DataSet(value = "datasets/anime.yml")
    void アニメを全体取得できること() throws Exception{
        String url = "/api/anime";
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get(url)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(2)))
                        .andReturn()
                        .getResponse();

        String expected = """
                [{
                    "id": 1,
                    "name": "Kill la Kill",
                    "genre": "Action"
                },
                {
                    "id": 2,
                    "name": "Fairy Tail",
                    "genre": "Adventure"
                }]
                """;

        assertThatJson(response.getContentAsString()).isEqualTo(expected);

    }

    @Test
    @DataSet(value = "datasets/anime.yml")
    void アニメが取得できるときに1件アニメを返すこと() throws Exception{
        String url = "/api/anime/1";
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get(url)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse();

        String expected = """
                {
                    "id": 1,
                    "name": "Kill la Kill",
                    "genre": "Action"
                }
                """;

        assertThatJson(response.getContentAsString()).isEqualTo(expected);

    }

    @Test
    @DataSet(value = "datasets/anime.yml")
    void アニメが取得できないときにレスポンスボディにエラーjsonが返されること() throws Exception {
        try (MockedStatic<ZonedDateTime> zonedDateTimeMockedStatic = Mockito.mockStatic(ZonedDateTime.class)) {
            zonedDateTimeMockedStatic.when(ZonedDateTime::now).thenReturn(zonedDateTime);
            String url = "/api/anime/99";
            MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get(url)
                            .accept(MediaType.APPLICATION_JSON_VALUE))
                            .andExpect(status().isNotFound())
                            .andReturn().getResponse();


            String expected = """
                    {
                        "error": "Not Found",
                        "path": "/api/anime/99",
                        "status": "404",
                        "message": "resource not found",
                        "timestamp": "2022-12-13T00:00+09:00[Asia/Tokyo]"

                    }
                    """;

            assertThatJson(response.getContentAsString()).isEqualTo(expected);

        }
    }

    @Test
    @DataSet(value = "datasets/anime.yml")
    @ExpectedDataSet(value = "datasets/expectedAfterITInsertAnime.yml", ignoreCols = "id")
    void 新規アニメが登録できること() throws Exception {
        String url = "/api/anime";
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.post(url)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("""
                                {
                                    "name" : "Gintama",
                                    "genre" : "Comedy" 
                                }
                                """))
                        .andExpect(status().isCreated())
                        .andReturn().getResponse();

        assertThat(response.getContentAsString()).isEqualTo("anime successfully created");
    }

    @Test
    @DataSet(value = "datasets/anime.yml")
    @ExpectedDataSet(value = "datasets/expectedAfterUpdateAnime.yml")
    void アニメが更新できること() throws Exception {
        String url = "/api/anime/1";
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.patch(url)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("""
                                {
                                    "name" : "No Game No Life",
                                    "genre" : "Fantasy" 
                                }
                                """))
                        .andExpect(status().isCreated())
                        .andReturn().getResponse();

        assertThat(response.getContentAsString()).isEqualTo("anime successfully updated");
    }

    @Test
    @DataSet(value = "datasets/anime.yml")
    void 更新時に指定したIDのアニメが存在しない場合404エラーとなりエラーのレスポンスを返すこと() throws Exception {
        try (MockedStatic<ZonedDateTime> zonedDateTimeMockedStatic = Mockito.mockStatic(ZonedDateTime.class)) {
            zonedDateTimeMockedStatic.when(ZonedDateTime::now).thenReturn(zonedDateTime);
            String url = "/api/anime/99";
            MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.patch(url)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("""
                                    {
                                        "name" : "No Game No Life",
                                        "genre" : "Fantasy" 
                                    }
                                    """))
                            .andExpect(status().isNotFound())
                            .andReturn().getResponse();
            String expected = """
                    {
                        "error": "Not Found",
                        "path": "/api/anime/99",
                        "status": "404",
                        "message": "resource not found",
                        "timestamp": "2022-12-13T00:00+09:00[Asia/Tokyo]"

                    }
                    """;


            assertThatJson(response.getContentAsString()).isEqualTo(expected);
        }
    }

    @Test
    @DataSet(value = "datasets/anime.yml")
    @ExpectedDataSet(value = "datasets/expectedAfterDeleteAnime.yml")
    void アニメが削除できること() throws Exception {
        String url = "/api/anime/1";
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.delete(url)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isCreated())
                        .andReturn().getResponse();

        assertThat(response.getContentAsString()).isEqualTo("anime successfully deleted");
    }

    @Test
    @DataSet(value = "datasets/anime.yml")
    void 削除時に指定したIDのアニメが存在しない場合404エラーとなりエラーのレスポンスを返すこと() throws Exception {
        try (MockedStatic<ZonedDateTime> zonedDateTimeMockedStatic = Mockito.mockStatic(ZonedDateTime.class)) {
            zonedDateTimeMockedStatic.when(ZonedDateTime::now).thenReturn(zonedDateTime);
            String url = "/api/anime/99";
            MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.delete(url)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                            .andExpect(status().isNotFound())
                            .andReturn().getResponse();
            String expected = """
                    {
                        "error": "Not Found",
                        "path": "/api/anime/99",
                        "status": "404",
                        "message": "resource not found",
                        "timestamp": "2022-12-13T00:00+09:00[Asia/Tokyo]"

                    }
                    """;


            assertThatJson(response.getContentAsString()).isEqualTo(expected);
        }
    }

}
