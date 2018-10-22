package com.mastering.spring.springbootexample;


import com.mastering.spring.springbootexample.bean.Todo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URI;
import java.util.Date;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringbootExampleApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TodoControllerIT {
    @LocalServerPort
    private int port;

    @Test
    public void retrieveTodos() throws Exception {
        String expected = "["
                + "{id:1,user:Jack,desc:\"Learn Spring MVC\",done:false}" + ","
                + "{id:2,user:Jack,desc:\"Learn Struts\",done:false}" + "]";
        String uri = "/users/Jack/todos";
        OAuth2RestTemplate oauthTemplate = getOAuth2("user", "12345678", "/oauth/token", "user", "87654321", "password");
        ResponseEntity<String> response =
                oauthTemplate.getForEntity(createUrl(uri), String.class);
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    private String createUrl(String uri) {
        return "http://localhost:" + port + uri;
    }

    @Test
    public void retrieveTodo() throws Exception {
        String expected = "{id:1,user:Jack,desc:\"Learn Spring MVC\",done:false}";
        OAuth2RestTemplate oauthTemplate = getOAuth2("user", "12345678", "/oauth/token", "user", "87654321", "password");
        ResponseEntity<String> response =
                oauthTemplate.getForEntity(createUrl("/users/Jack/todos/1"), String.class);
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    public void addTodo() throws Exception {
        Todo todo = new Todo(-1, "Jill", "Learn Hibernate", new Date(), false);
        OAuth2RestTemplate oauthTemplate = getOAuth2("user", "12345678", "/oauth/token", "user", "87654321", "password");
        URI location = oauthTemplate.postForLocation(createUrl("/users/Jill/todos"), todo);
        assertThat(location.getPath(), containsString("/users/Jill/todos/4"));
    }

    private OAuth2RestTemplate getOAuth2(String username, String password, String accessTokenUri, String clientId, String clientSecret, String grantType) {
        ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();
        resource.setUsername(username);
        resource.setPassword(password);
        resource.setAccessTokenUri(createUrl(accessTokenUri));
        resource.setClientId(clientId);
        resource.setClientSecret(clientSecret);
        resource.setGrantType(grantType);
        return new OAuth2RestTemplate(resource, new DefaultOAuth2ClientContext());
    }

}
