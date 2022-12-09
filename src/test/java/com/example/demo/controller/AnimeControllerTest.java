package com.example.demo.controller;

import com.example.demo.entity.Anime;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.AnimeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssert.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureJsonTesters
public class AnimeControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private AnimeService animeService;

    @Autowired
    private JacksonTester<List<Anime>> animeListJacksonTester;

    @Autowired
    private JacksonTester<Anime> animeJacksonTester;

    @Autowired
    ObjectMapper mapper;

    @Test
    public void アニメが全件取得できること() throws Exception {

        List<Anime> animeList = List.of(
                new Anime(1, "Your Name", "Romantic Fantasy"),
                new Anime(2, "Kill la Kill", "Action"));
        doReturn(animeList).when(animeService).getAllAnime();

        String url = "/api/anime";

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get(url)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(2)))
                        .andExpect(jsonPath("$[0].name", is(animeList.get(0).getName())))
                        .andReturn()
                        .getResponse();

        assertThat(response.getContentAsString()).isEqualTo(animeListJacksonTester.write(animeList).getJson());

        assertThatJson(response.getContentAsString()).isEqualTo(animeListJacksonTester.write(animeList).getJson());

        verify(animeService, times(1)).getAllAnime();

    }

    @Test
    public void アニメが取得できるときに1件アニメを返すこと() throws Exception {

        Anime anime = new Anime(1, "Your Name", "Romantic Fantasy");
        doReturn(anime).when(animeService).getAnime(1);

        String url = "/api/anime/1";
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get(url)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.name", is(anime.getName())))
                        .andReturn()
                        .getResponse();

        assertThatJson(response.getContentAsString()).isEqualTo(animeJacksonTester.write(anime).getJson());

        verify(animeService, times(1)).getAnime(1);

    }

    @Test
    public void アニメが取得できないときに例外をthrowすること() throws Exception {

        doThrow(new ResourceNotFoundException("resource not found")).when(animeService).getAnime(1);

        String url = "/api/anime/1";
        String error = mvc.perform(MockMvcRequestBuilders.get(url)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isNotFound())
                        .andReturn()
                        .getResolvedException().getMessage();

        assertThat(error).isEqualTo("resource not found");

        verify(animeService, times(1)).getAnime(1);

    }

    @Test
    public void アニメの登録ができること() throws Exception {

        Anime anime = new Anime("Your Name", "Romantic Fantasy");

        String url = "/api/anime";

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(this.mapper.writeValueAsString(anime)))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        assertThat(response.getContentAsString()).isEqualTo("anime successfully created");

        verify(animeService, times(1)).registerAnime("Your Name", "Romantic Fantasy");
    }

    @Test
    public void アニメの更新ができること() throws Exception {

        Anime updatedAnime = new Anime(1,"Kill la Kill", "Action");

        String url = "/api/anime/1";

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.patch(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(this.mapper.writeValueAsString(updatedAnime)))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        assertThat(response.getContentAsString()).isEqualTo("anime successfully updated");

        verify(animeService, times(1)).updateAnime(1, "Kill la Kill", "Action");
    }

    @Test
    public void 更新対象のアニメが存在しないときにレスポンスボディにエラーメッセージが返されること() throws Exception {

        doThrow(new ResourceNotFoundException("resource not found")).when(animeService).updateAnime(1, "Kill la Kill", "Action");

        Anime anime = new Anime(1,"Kill la Kill", "Action");

        String url = "/api/anime/1";

        String error = mvc.perform(MockMvcRequestBuilders.patch(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(this.mapper.writeValueAsString(anime)))
                .andExpect(status().isNotFound())
                .andReturn().getResolvedException().getMessage();

        assertThat(error).isEqualTo("resource not found");

        verify(animeService, times(1)).updateAnime(1, "Kill la Kill", "Action");
    }

    @Test
    public void アニメが削除できること() throws Exception {

        String url = "/api/anime/1";

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.delete(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        assertThat(response.getContentAsString()).isEqualTo("anime successfully deleted");

        verify(animeService, times(1)).deleteAnime(1);
    }

    @Test
    public void 削除対象のアニメが存在しないときにレスポンスボディにエラーメッセージが返されること() throws Exception {

        doThrow(new ResourceNotFoundException("resource not found")).when(animeService).deleteAnime(1);

        String url = "/api/anime/1";

        String error = mvc.perform(MockMvcRequestBuilders.delete(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andReturn().getResolvedException().getMessage();

        assertThat(error).isEqualTo("resource not found");

        verify(animeService, times(1)).deleteAnime(1);
    }

    //例のjsonフォーマットで試してみる
    @Test
    public void test() throws JSONException {
        String json1 = """
            {
                "key1": "value1",
                "key2": "value2"
            }
            """;

        String json2 = """
            {
                "key2": "value2",
                "key1": "value1"
            }
            """;

        assertThatJson(json1).isEqualTo(json2);
    }

}
