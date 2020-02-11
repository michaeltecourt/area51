package com.dreev.area51.http;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import io.restassured.RestAssured;

import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWireMock(port = 0)
public class HealthCheckTest {

    private static final String ACCESS_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9"
            + ".eyJzdWIiOiI4MDZiNmVjNS02ZTEyLTQ5MzMtOTE1Yy02YmQ0ODk0NjRhMzYiLCJjb2duaXRvOmdyb3VwcyI6WyJkcmVldl9hcmVhNTFfYWRtaW4iXSwiaXNzIjoiaHR0cHM6Ly9mYWtlLWlkcC50ZXN0LmRyZWV2LmNvbSIsInZlcnNpb24iOjIsImNsaWVudF9pZCI6IjZqZnVmaWdxbjRqMTRocmltNGdqNzZta2pjIiwiZXZlbnRfaWQiOiJjMDc0OTUxYy00MjQ0LTQwMTEtOGVmOS00NDlmNzU1MmVhYjEiLCJ0b2tlbl91c2UiOiJhY2Nlc3MiLCJzY29wZSI6ImF3cy5jb2duaXRvLnNpZ25pbi51c2VyLmFkbWluIHBob25lIG9wZW5pZCBwcm9maWxlIGVtYWlsIGRyZWV2OmFyZWE1MTphbGwiLCJhdXRoX3RpbWUiOjE1NjM5NjU3MTksImV4cCI6NDU2Mzk2OTMxOSwiaWF0IjoxNTYzOTY1NzE5LCJqdGkiOiJlNzUxZTBlZi1hZmY0LTQxOTctYmY3My1iOGVmODk4YmEzZmMiLCJ1c2VybmFtZSI6IjgwNmI2ZWM1LTZlMTItNDkzMy05MTVjLTZiZDQ4OTQ2NGEzNiJ9"
            + ".UiwWhSfRoZyKLcGT39F7wYxOEQh_oI68hE0a9O1qH_QiUBIMbb6m-E9ootF2NaNWLIkGw0m-FsyOqg2koU3alG97xgGCjgVToY4qtfqBk2Y0cHY9JWPlsoofEFsHI8O-B97Ar_9yMSHaxineh76k8uFZ-IjRFOlvpWmICtd8bphtl7yj4nsNtVdI_vZDGSn1W8wWa2O2WnZqqsEvE4zs5RyxI3Sv-M2R6VXM2hB8byz5WI7ip1Lx4IVz2riBV1imGlYJjwi76QiWphZpzyUGtIJp1OroYzL9UiFrpWSxE8VadCH8fgKVe2ERJDlfyIKF2ZdnMrSMGaO-tLL0jydNcg";

    @Value("http://localhost:${local.server.port}")
    String url;

    @Before
    public void setUp() {
        RestAssured.baseURI = url;
    }

    @Test
    public void get_health_should_return_up() {
        RestAssured
            .given()
            .when()
                .get("/actuator/health")
            .then()
                .log().all()
                .statusCode(200)
                .body("status", is("UP"))
                .body("details", nullValue());
    }

    @Test
    public void get_health_should_return_more_info_when_logged() {
        RestAssured
            .given()
                .auth().preemptive().basic("admin", "not_password")
            .when()
                .get("/actuator/health")
            .then()
                .log().all()
                .statusCode(200)
                .body("status", is("UP"))
                .body("details.diskSpace.status", equalTo("UP"));;
    }

    @Test
    public void get_metrics_should_return_401_without_creds() {
        RestAssured
            .given()
            .when()
                .get("/actuator/metrics")
            .then()
                .log().all()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void get_metrics_should_return_403_with_token() {
        RestAssured
            .given()
                .auth().preemptive().oauth2(ACCESS_TOKEN)
            .when()
                .get("/actuator/metrics")
            .then()
                .log().all()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void get_metrics_should_return_stuff_with_creds() {
        RestAssured
            .given()
                .auth().preemptive().basic("admin", "not_password")
            .when()
                .get("/actuator/metrics")
            .then()
                .log().all()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void get_participants_should_return_forbidden_with_actuator_creds() {
        RestAssured
        .given()
            .auth().preemptive().basic("admin", "not_password")
        .when()
            .get("/participants")
        .then()
            .log().all()
            .statusCode(HttpStatus.FORBIDDEN.value());
    }

}
