package com.edufelip.meer.openapi;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.webmvc.api.OpenApiWebMvcResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(OpenApiSchemaValidationTest.TestOpenApiConfig.class)
class OpenApiSchemaValidationTest {

  @Autowired private OpenApiWebMvcResource openApiWebMvcResource;
  private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

  @TestConfiguration
  static class TestOpenApiConfig {
    @Bean
    public GroupedOpenApi apiDocs() {
      return GroupedOpenApi.builder().group("meer").pathsToMatch("/**").build();
    }
  }

  @Test
  void openApiSchemaIncludesRatingsAndStoreDetails() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v3/api-docs");
    request.setScheme("http");
    request.setServerName("localhost");
    request.setServerPort(8080);
    request.setServletPath("/v3/api-docs");

    byte[] jsonBytes = openApiWebMvcResource.openapiJson(request, "/v3/api-docs", Locale.US);
    JsonNode root = objectMapper.readTree(new String(jsonBytes, StandardCharsets.UTF_8));
    assertThat(root.path("openapi").asText()).isNotBlank();

    JsonNode paths = root.path("paths");
    assertThat(paths.has("/stores/{storeId}/ratings")).isTrue();
    assertThat(paths.path("/stores/{storeId}/ratings").path("get").isMissingNode()).isFalse();
    assertThat(paths.has("/stores/{id}")).isTrue();
    assertThat(paths.path("/stores/{id}").path("get").isMissingNode()).isFalse();
    assertThat(paths.has("/stores/{storeId}/feedback")).isTrue();
    assertThat(paths.path("/stores/{storeId}/feedback").path("delete").isMissingNode()).isFalse();

    JsonNode components = root.path("components").path("schemas");
    assertThat(components.has("StoreRatingDto")).isTrue();
    assertThat(components.has("ThriftStoreDto")).isTrue();

    JsonNode responses =
        paths.path("/stores/{storeId}/ratings").path("get").path("responses").path("200");
    JsonNode ratingsSchema = schemaFromResponse(responses);

    var pageSchemaNames = findPageResponseSchemaNames(components);
    assertThat(pageSchemaNames).as("No PageResponse-like schemas found in components").isNotEmpty();

    boolean hasPageProps = hasPageResponseProperties(ratingsSchema, components);
    boolean referencesPageSchema = referencesSchema(ratingsSchema, pageSchemaNames, components);
    assertThat(hasPageProps || referencesPageSchema)
        .as("ratings schema: %s", ratingsSchema.toPrettyString())
        .isTrue();

    JsonNode storeSchema = components.path("ThriftStoreDto");
    assertThat(storeSchema.path("properties").has("rating")).isTrue();
    assertThat(storeSchema.path("properties").has("reviewCount")).isTrue();
  }

  private JsonNode resolveSchema(JsonNode schemaNode, JsonNode components) {
    if (schemaNode.has("$ref")) {
      String ref = schemaNode.path("$ref").asText();
      String prefix = "#/components/schemas/";
      if (ref.startsWith(prefix)) {
        String name = ref.substring(prefix.length());
        return components.path(name);
      }
    }
    return schemaNode;
  }

  private JsonNode schemaFromResponse(JsonNode response) {
    JsonNode schema = response.path("content").path("application/json").path("schema");
    if (!schema.isMissingNode() && !schema.isNull()) {
      return schema;
    }
    JsonNode content = response.path("content");
    if (content.isObject()) {
      var fields = content.fieldNames();
      if (fields.hasNext()) {
        return content.path(fields.next()).path("schema");
      }
    }
    return schema;
  }

  private boolean hasPageResponseProperties(JsonNode schemaNode, JsonNode components) {
    if (schemaNode == null || schemaNode.isMissingNode() || schemaNode.isNull()) return false;
    JsonNode resolved = resolveSchema(schemaNode, components);
    if (resolved.path("properties").has("items")
        && resolved.path("properties").has("page")
        && resolved.path("properties").has("hasNext")) {
      return true;
    }
    for (String keyword : new String[] {"allOf", "oneOf", "anyOf"}) {
      JsonNode list = resolved.path(keyword);
      if (list.isArray()) {
        for (JsonNode item : list) {
          if (hasPageResponseProperties(item, components)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  private java.util.List<String> findPageResponseSchemaNames(JsonNode components) {
    java.util.List<String> names = new java.util.ArrayList<>();
    var fields = components.fieldNames();
    while (fields.hasNext()) {
      String name = fields.next();
      JsonNode schema = components.path(name);
      if (hasPageResponseProperties(schema, components)) {
        names.add(name);
      }
    }
    return names;
  }

  private boolean referencesSchema(
      JsonNode schemaNode, java.util.List<String> names, JsonNode components) {
    if (schemaNode == null || schemaNode.isMissingNode() || schemaNode.isNull()) return false;
    JsonNode resolved = resolveSchema(schemaNode, components);
    if (resolved.has("$ref")) {
      String ref = resolved.path("$ref").asText();
      for (String name : names) {
        if (ref.endsWith("/" + name)) return true;
      }
    }
    for (String keyword : new String[] {"allOf", "oneOf", "anyOf"}) {
      JsonNode list = resolved.path(keyword);
      if (list.isArray()) {
        for (JsonNode item : list) {
          if (referencesSchema(item, names, components)) {
            return true;
          }
        }
      }
    }
    return false;
  }
}
