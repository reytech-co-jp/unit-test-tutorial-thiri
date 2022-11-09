package com.example.demo.mapper;

import com.example.demo.entity.Anime;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

@DBRider
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AnimeMapperTest {
    @Autowired
    AnimeMapper animeMapper;

    @Test
    @DataSet(value = "anime.yml")
    void すべてのアニメが取得できること(){
        List<Anime> animeList = animeMapper.findAll();
        assertThat(animeList)
                .hasSize(2)
                .contains(
                        new Anime(1, "Kill la Kill", "Action"),
                        new Anime(2, "Fairy Tail", "Adventure")
                );
    }

    @Test
    @DataSet(value = "empty.yml")
    void アニメが存在しない場合に空のListが取得できること() {
        List<Anime> animeList = animeMapper.findAll();
        assertThat(animeList).isEmpty();
    }

    @Test
    @DataSet(value = "anime.yml")
    void 引数のidに対応したアニメを取得できること() {
        Optional<Anime> anime = animeMapper.findById(1);
        assertThat(anime).contains(new Anime(1, "Kill la Kill", "Action"));
    }

    @Test
    @DataSet(value = "anime.yml")
    void 引数のidに対応したアニメが存在しない時_空のOptionalを取得すること() {
        Optional<Anime> anime = animeMapper.findById(3);
        assertThat(anime).isEmpty();
    }

    @Test
    @DataSet(value = "anime.yml")
    @ExpectedDataSet(value = "expectedAfterInsertAnime.yml", ignoreCols = "id")
    void アニメが登録できること() {
        animeMapper.createAnime(new Anime(3, "Gintama", "Comedy"));
    }
}
