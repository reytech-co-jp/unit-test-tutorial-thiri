[![codecov](https://codecov.io/github/reytech-co-jp/unit-test-tutorial-thiri/branch/feature/coverage_report/graph/badge.svg?token=326UE8FZE9)](https://codecov.io/github/reytech-co-jp/unit-test-tutorial-thiri)
## 概要
このプロジェクトはアニメのデータの登録、検索、更新、削除をするレポジトリです。

## 前提

- Java 17
- Spring Boot 2.7.1
- Docker 20.10.13

## 起動手順
`$ git clone https://github.com/reytech-co-jp/unit-test-tutorial.git`

`$ cd unit-test-tutorial`

`$ docker-compose up -d`

`$ ./gradlew bootRun`

起動成功時のイメージ
![image](https://user-images.githubusercontent.com/97335620/179455637-4e0f537d-7ad8-444d-b2b8-d2dbd5b0d6d1.png)

## 使用方法
Postmanを使用した画面とcurlコマンドを記載

### 全件検索
```
curl 'http://localhost:8080/api/anime'
```
![image](https://user-images.githubusercontent.com/97335620/179456104-c209c4bc-c5f9-4364-8882-59b72e045cd6.png)

## 一件検索
```
curl 'http://localhost:8080/api/anime/1'
```
![image](https://user-images.githubusercontent.com/97335620/179456451-710f3f26-2046-429f-a781-838aba8fc07c.png)


## 登録
```
curl -X POST 'http://localhost:8080/api/anime' \
-H "Content-Type: application/json" \
-d '{
"name": "Dr.STONE",
"genre": "survival"
}'
```
※GitBashの場合、日本語の入力は不可

![image](https://user-images.githubusercontent.com/97335620/180655252-984b32f0-00bd-47db-ac39-7778edcde979.png)


## 更新
```
curl -X PATCH 'http://localhost:8080/api/anime/1' \
-H "Content-Type: application/json" \
-d '{
"name": "Fruits Basket",
"genre": "Romantic Comedy"
}'
```
※GitBashの場合、日本語の入力は不可

![image](https://user-images.githubusercontent.com/97335620/180655337-1519b6f3-8d2c-487e-990b-1aaaa737345d.png)


## 削除
```
curl -X DELETE 'http://localhost:8080/api/anime/1'
```
![image](https://user-images.githubusercontent.com/97335620/180655363-8ad18bb4-3d00-45cf-99dd-cb3f9c48c7b3.png)

## バリデーションエラー
![image](https://user-images.githubusercontent.com/97335620/180655479-66f9efce-0a18-4d7f-9f88-ae72ed3fd9dd.png)

## 更新及び削除で指定したIDのデータが存在しない時
![image](https://user-images.githubusercontent.com/97335620/180655420-90e50fda-05d4-4ead-aa75-2efed3bf75e1.png)

## 終了手順
（`./gradlew bootRun`コマンドで実行している場合）`ctrl + c`で終了する

`$ docker-compose down`

## ケアできていないエラー
- 存在しないURLへのアクセス
- DBへの接続できないなどのシステムエラー

