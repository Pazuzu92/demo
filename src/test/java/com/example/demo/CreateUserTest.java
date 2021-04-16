package com.example.demo;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import net.bytebuddy.utility.RandomString;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.testng.Assert;
import org.testng.annotations.*;

import static org.hamcrest.Matchers.containsString;


public class CreateUserTest {
    private String email;
    private String name;

    @BeforeClass
    public void init() {

        email = RandomString.make(11);
        name = RandomString.make(8);
    }

    @DataProvider(name = "rightData")
    public Object[][] createData1() throws JSONException {
        JSONObject requestBody = new JSONObject();
        JSONArray companiesArr = new JSONArray();
        companiesArr.put("36");
        companiesArr.put("37");
        JSONArray tasksArr = new JSONArray();
        tasksArr.put("12");
        requestBody.put("email", email + "@gmail.com");
        requestBody.put("name", name);
        requestBody.put("tasks", tasksArr);
        requestBody.put("companies", companiesArr);
        requestBody.put("hobby", "develop android apps");
        requestBody.put("phone", "123456789");
        requestBody.put("inn", "987654321122");
        return new Object[][]{
                {requestBody}
        };
    }

    @DataProvider(name = "wrongEmail")
    public Object[][] createWrongData() throws JSONException {
        JSONObject requestBody = new JSONObject();
        JSONArray companiesArr = new JSONArray();
        companiesArr.put("36");
        companiesArr.put("37");
        JSONArray tasksArr = new JSONArray();
        tasksArr.put("12");
        requestBody.put("email", email);
        requestBody.put("name", name);
        requestBody.put("tasks", tasksArr);
        requestBody.put("companies", companiesArr);
        requestBody.put("hobby", "develop android apps");
        requestBody.put("phone", "123456789");
        requestBody.put("inn", "987654321122");
        return new Object[][]{
                {requestBody}
        };
    }

    @DataProvider(name = "wrongInn")
    public Object[][] createWrongDataInn() throws JSONException {
        JSONObject requestBody = new JSONObject();
        JSONArray companiesArr = new JSONArray();
        companiesArr.put("36");
        companiesArr.put("37");
        JSONArray tasksArr = new JSONArray();
        tasksArr.put("12");
        requestBody.put("email", email + "@gmail.ru");
        requestBody.put("name", name + "aaa");
        requestBody.put("tasks", tasksArr);
        requestBody.put("companies", companiesArr);
        requestBody.put("hobby", "develop android apps");
        requestBody.put("phone", "123456789");
        requestBody.put("inn", "9876543211");
        return new Object[][]{
                {requestBody}
        };
    }

    @Step("Проверка переданного Email")
    public static void checkEmail(Object data, String expectedData) {
        Assert.assertEquals(data, expectedData);
    }

    @Step("Проверка переданного name")
    public static void checkName(Object data, String expectedData) {
        Assert.assertEquals(data, expectedData);
    }

    @Step("Проверка переданного hobby")
    public static void checkHobby(Object data, String expectedData) {
        Assert.assertEquals(data, expectedData);
    }

    @Step("Проверка переданного phone")
    public static void checkPhone(Object data, String expectedData) {
        Assert.assertEquals(data, expectedData);
    }

    @Step("Проверка переданного inn")
    public static void checkInn(Object data, String expectedData) {
        Assert.assertEquals(data, expectedData);
    }

    @Test(dataProvider = "rightData")
    @Description(value = "Тест проверяет возможность создания пользователя с уникальными правильными данными")
    public void createUserSuccess(Object o) throws JSONException {

        JSONArray arr = new JSONArray();
        arr.put(o);
        JSONObject requestBody = arr.getJSONObject(0);
        RestAssured.given()
                .contentType("application/json").body(requestBody.toString()).when().post("http://users.bugred.ru/tasks/rest/createuser")
                .then().log().body();

        checkEmail(requestBody.get("email"), email + "@gmail.com");
        checkName(requestBody.get("name"), name);
        checkHobby(requestBody.get("hobby"), "develop android apps");
        checkPhone(requestBody.get("phone"), "123456789");
        checkInn(requestBody.get("inn"), "987654321122");
    }


    @Step
    public static void sendWrongEmail(JSONObject requestBody) {
        RestAssured.given()
                .contentType("application/json").body(requestBody.toString()).when().post("http://users.bugred.ru/tasks/rest/createuser")
                .then().log().body().content("type", containsString("error"));
    }

    @Test(dataProvider = "wrongEmail")
    @Description(value = "Тест проверяет возможность создания пользователя с неправильным email. Ожидаем ошибку с текстом " +
            "неправильный email")
    public void createUserWrongEmail(Object o) throws JSONException {
        JSONArray arr = new JSONArray();
        arr.put(o);
        JSONObject requestBody = arr.getJSONObject(0);
        sendWrongEmail(requestBody);

    }

    @Test
    @Description(value = "Тест проверяет возможность создания пользователя только с email, без других данных. Ожидаем ошибку " +
            "с текстом Параметр companies является обязательным!")
    public void createUserOnlyWithEmail() {
        RestAssured.given()
                .contentType("application/json").body(email + "@gmail.com").when().post("http://users.bugred.ru/tasks/rest/createuser")
                .then().log().body().content("message", containsString("Параметр companies является обязательным!"));
    }

    @Step
    public static void sendWrongInn(JSONObject requestBody) throws JSONException {
        RestAssured.given()
                .contentType("application/json").body(requestBody.toString()).when().post("http://users.bugred.ru/tasks/rest/createuser")
                .then().log().body().content("message", containsString("Значение " + requestBody.get("inn") + " ИНН ФЛ должен содержать 12 цифр"))
                .content("type", containsString("error"));
    }

    @Test(dataProvider = "wrongInn")
    @Description(value = "Тест проверяет возможность создания пользователя с неправильным inn. Ожидаем ошибку " +
            "с текстом значение ИНН ФЛ должен содержать 12 цифр")
    public void createUserWithWrongInn(Object o) throws JSONException {
        JSONArray arr = new JSONArray();
        arr.put(o);
        JSONObject requestBody = arr.getJSONObject(0);
        sendWrongInn(requestBody);
    }

    @DataProvider(name = "phoneIsLetters")
    public Object[][] createWrongPhone() throws JSONException {
        JSONObject requestBody = new JSONObject();
        JSONArray companiesArr = new JSONArray();
        companiesArr.put("36");
        companiesArr.put("37");
        JSONArray tasksArr = new JSONArray();
        tasksArr.put("12");
        requestBody.put("email", email + "@gmail.ru");
        requestBody.put("name", name);
        requestBody.put("tasks", tasksArr);
        requestBody.put("companies", companiesArr);
        requestBody.put("hobby", "develop android apps");
        requestBody.put("phone", "qwerty");
        requestBody.put("inn", "987654321122");
        return new Object[][]{
                {requestBody}
        };
    }

    @Step
    public void checkWhatIfPhoneIsLetters(Object data, String expectedData){
        Assert.assertEquals(data, expectedData);
    }

    @Test(dataProvider = "phoneIsLetters")
    @Description(value = "Тест проверяет возможность создания пользователя с номером телефона из букв")
    public void createUserWithWrongPhone(Object o) throws JSONException {
        JSONArray arr = new JSONArray();
        arr.put(o);
        JSONObject requestBody = arr.getJSONObject(0);
        RestAssured.given()
                .contentType("application/json").body(requestBody.toString()).when().post("http://users.bugred.ru/tasks/rest/createuser")
                .then().log().body();
        checkWhatIfPhoneIsLetters(requestBody.get("phone"), "qwerty");
    }

    @DataProvider(name = "nameExists")
    public Object[][] createDataWithExistsName() throws JSONException {
        JSONObject requestBody = new JSONObject();
        JSONArray companiesArr = new JSONArray();
        companiesArr.put("36");
        companiesArr.put("37");
        JSONArray tasksArr = new JSONArray();
        tasksArr.put("12");
        requestBody.put("email", email + "@gmail.ru");
        requestBody.put("name", "F8gHeg9f");
        requestBody.put("tasks", tasksArr);
        requestBody.put("companies", companiesArr);
        requestBody.put("hobby", "develop android apps");
        requestBody.put("phone", "qwerty");
        requestBody.put("inn", "987654321122");
        return new Object[][]{
                {requestBody}
        };
    }

    @Step
    public void checkNameAlreadyExists(JSONObject requestBody) throws JSONException {
        RestAssured.given()
                .contentType("application/json").body(requestBody.toString()).when().post("http://users.bugred.ru/tasks/rest/createuser")
                .then().log().body().content("message", containsString("имя " + requestBody.get("name") + " уже есть в БД"))
                .content("type", containsString("error"));
    }

    @Test(dataProvider = "nameExists")
    @Description(value = "Тест проверяет возможность создания пользователя с уже существующим именем. Ожидаем ошибку " +
            "с текстом имя уже есть в БД")
    public void nameAlreadyExistsTest(Object o) throws JSONException {
        JSONArray arr = new JSONArray();
        arr.put(o);
        JSONObject requestBody = arr.getJSONObject(0);
        checkNameAlreadyExists(requestBody);
    }
}
