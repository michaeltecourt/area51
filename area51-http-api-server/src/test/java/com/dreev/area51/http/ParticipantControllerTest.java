package com.dreev.area51.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import com.dreev.area51.participant.Participant;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWireMock(port = 0)
public class ParticipantControllerTest {

    private static final String ACCESS_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9"
            + ".eyJzdWIiOiI4MDZiNmVjNS02ZTEyLTQ5MzMtOTE1Yy02YmQ0ODk0NjRhMzYiLCJjb2duaXRvOmdyb3VwcyI6WyJkcmVldl9hcmVhNTFfYWRtaW4iXSwiaXNzIjoiaHR0cHM6Ly9mYWtlLWlkcC50ZXN0LmRyZWV2LmNvbSIsInZlcnNpb24iOjIsImNsaWVudF9pZCI6IjZqZnVmaWdxbjRqMTRocmltNGdqNzZta2pjIiwiZXZlbnRfaWQiOiJjMDc0OTUxYy00MjQ0LTQwMTEtOGVmOS00NDlmNzU1MmVhYjEiLCJ0b2tlbl91c2UiOiJhY2Nlc3MiLCJzY29wZSI6ImF3cy5jb2duaXRvLnNpZ25pbi51c2VyLmFkbWluIHBob25lIG9wZW5pZCBwcm9maWxlIGVtYWlsIGRyZWV2OmFyZWE1MTphbGwiLCJhdXRoX3RpbWUiOjE1NjM5NjU3MTksImV4cCI6NDU2Mzk2OTMxOSwiaWF0IjoxNTYzOTY1NzE5LCJqdGkiOiJlNzUxZTBlZi1hZmY0LTQxOTctYmY3My1iOGVmODk4YmEzZmMiLCJ1c2VybmFtZSI6IjgwNmI2ZWM1LTZlMTItNDkzMy05MTVjLTZiZDQ4OTQ2NGEzNiJ9"
            + ".UiwWhSfRoZyKLcGT39F7wYxOEQh_oI68hE0a9O1qH_QiUBIMbb6m-E9ootF2NaNWLIkGw0m-FsyOqg2koU3alG97xgGCjgVToY4qtfqBk2Y0cHY9JWPlsoofEFsHI8O-B97Ar_9yMSHaxineh76k8uFZ-IjRFOlvpWmICtd8bphtl7yj4nsNtVdI_vZDGSn1W8wWa2O2WnZqqsEvE4zs5RyxI3Sv-M2R6VXM2hB8byz5WI7ip1Lx4IVz2riBV1imGlYJjwi76QiWphZpzyUGtIJp1OroYzL9UiFrpWSxE8VadCH8fgKVe2ERJDlfyIKF2ZdnMrSMGaO-tLL0jydNcg";

    @Value("http://localhost:${local.server.port}")
    String url;

    @Autowired
    AmqpTemplate amqp;
    
    @Value("${dreev.area51.amqp.routing-key}")
    String queue;

    @Before
    public void setUp() {
        RestAssured.baseURI = url;
    }

    @Test
    @Sql("/sql/clear.sql")
    public void context_should_load() {

    }

    @Test
    @Sql("/sql/clear.sql")
    public void post_participant_should_persist_participant() {
        RestAssured
            .given()
                .contentType(ContentType.JSON)
                .auth().preemptive().oauth2(ACCESS_TOKEN)
                .body(new Participant("michaeltecourt", "Michaël Técourt"))
                .log().all()
            .when()
                .post("/participants")
            .then()
                .log().all()
                .statusCode(HttpStatus.CREATED.value());

        RestAssured
            .given()
                .auth().preemptive().oauth2(ACCESS_TOKEN)
                .log().all()
            .when()
                .get("/participants")
            .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("participants.size()", is(1))
                .body("participants[0].id", is("michaeltecourt"))
                .body("participants[0].name", is("Michaël Técourt"));
        
        checkMessageSent();
    }

    @Test
    @Sql("/sql/clear.sql")
    public void post_participant_without_token_should_return_401() {
        RestAssured
            .given()
                .contentType(ContentType.JSON)
                .body(new Participant("michaeltecourt", "Michaël Técourt"))
            .when()
                .post("/participants")
            .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @Sql("/sql/clear.sql")
    public void get_participant_without_token_should_return_401() {
        RestAssured
            .given()
            .when()
                .get("/participants")
            .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    /**
     * Check that we received a message within 3 seconds in the
     * emergency_charge_command_monitoring queue.
     */
    private void checkMessageSent() {
        Message received = amqp.receive(queue, 3_000);
        assertThat(received.getBody()).isEqualTo("michaeltecourt".getBytes());
    }

}
