package com.example.demo.controller;

import com.example.demo.entity.Anime;
import com.example.demo.service.AnimeService;
import org.junit.jupiter.api.Test;
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

        assertThat(response.getContentAsString()).isEqualTo(animeJacksonTester.write(anime).getJson());

        verify(animeService, times(1)).getAnime(1);

    }

}
