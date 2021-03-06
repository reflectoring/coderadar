package io.reflectoring.coderadar.rest.analysis;

import static io.reflectoring.coderadar.rest.JsonHelper.fromJson;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.reflectoring.coderadar.graph.analyzer.domain.MetricValueEntity;
import io.reflectoring.coderadar.graph.analyzer.repository.CommitRepository;
import io.reflectoring.coderadar.graph.analyzer.repository.MetricRepository;
import io.reflectoring.coderadar.graph.projectadministration.analyzerconfig.repository.AnalyzerConfigurationRepository;
import io.reflectoring.coderadar.graph.projectadministration.domain.CommitEntity;
import io.reflectoring.coderadar.projectadministration.port.driver.analyzerconfig.create.CreateAnalyzerConfigurationCommand;
import io.reflectoring.coderadar.projectadministration.port.driver.project.create.CreateProjectCommand;
import io.reflectoring.coderadar.rest.ControllerTestTemplate;
import io.reflectoring.coderadar.rest.domain.ErrorMessageResponse;
import io.reflectoring.coderadar.rest.domain.IdResponse;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

class StartAnalysisControllerIntegrationTest extends ControllerTestTemplate {

  @Autowired private CommitRepository commitRepository;
  @Autowired private MetricRepository metricRepository;
  @Autowired private Session session;
  @Autowired private AnalyzerConfigurationRepository analyzerConfigurationRepository;

  private Long projectId;

  @BeforeEach
  void setUp() throws Exception {
    URL testRepoURL = this.getClass().getClassLoader().getResource("test-repository");
    CreateProjectCommand createProjectCommand =
        new CreateProjectCommand(
            "test-project",
            "username",
            "password",
            Objects.requireNonNull(testRepoURL).toString(),
            false,
            null,
            "master");
    MvcResult result =
        mvc()
            .perform(
                post("/api/projects")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(createProjectCommand)))
            .andReturn();

    projectId = fromJson(result.getResponse().getContentAsString(), IdResponse.class).getId();
    analyzerConfigurationRepository.deleteAll();
  }

  @Test
  void startAnalysisTest() throws Exception {
    CreateAnalyzerConfigurationCommand createAnalyzerConfigurationCommand =
        new CreateAnalyzerConfigurationCommand(
            "io.reflectoring.coderadar.analyzer.loc.LocAnalyzerPlugin", true);
    mvc()
        .perform(
            post("/api/projects/" + projectId + "/analyzers")
                .content(toJson(createAnalyzerConfigurationCommand))
                .contentType(MediaType.APPLICATION_JSON));

    mvc()
        .perform(
            post("/api/projects/" + projectId + "/analyze").contentType(MediaType.APPLICATION_JSON))
        .andDo(document("analysis/start"));

    mvc()
        .perform(get("/api/projects/" + projectId + "/analyzingStatus"))
        .andDo(
            document(
                "analysis/status",
                responseFields(
                    fieldWithPath("status")
                        .description("Whether the Analyzing Job is started or not."))));

    session.clear();

    List<CommitEntity> commits = commitRepository.findByProjectIdAndBranchName(projectId, "master");
    for (CommitEntity commit : commits) {
      Assertions.assertTrue(commit.isAnalyzed());
    }
    List<MetricValueEntity> metricValues = metricRepository.findByProjectId(projectId);
    Assertions.assertEquals(48, metricValues.size());
  }

  @Test
  void returnsNotFoundWhenProjectWithIdDoesNotExist() throws Exception {
    MvcResult result =
        mvc()
            .perform(post("/api/projects/123/analyze"))
            .andExpect(status().isNotFound())
            .andReturn();

    ErrorMessageResponse response =
        fromJson(result.getResponse().getContentAsString(), ErrorMessageResponse.class);

    Assertions.assertEquals("Project with id 123 not found.", response.getErrorMessage());
  }
}
