package io.reflectoring.coderadar.graph;

import io.reflectoring.coderadar.domain.Project;
import io.reflectoring.coderadar.graph.projectadministration.domain.ProjectEntity;
import io.reflectoring.coderadar.graph.projectadministration.project.ProjectMapper;
import java.util.Date;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ProjectMapperTest {
  private final ProjectMapper projectMapper = new ProjectMapper();

  @Test
  void testMapDomainObject() {
    Project testProject =
        new Project()
            .setId(1L)
            .setName("testName")
            .setVcsStart(new Date(123L))
            .setVcsPassword(new byte[] {1})
            .setVcsUsername("testUsername")
            .setWorkdirName("workdir")
            .setDefaultBranch("master")
            .setVcsUrl("testUrl");

    ProjectEntity result = projectMapper.mapDomainObject(testProject);
    Assertions.assertEquals("testName", result.getName());
    Assertions.assertEquals("testUsername", result.getVcsUsername());
    Assertions.assertEquals(1, result.getVcsPassword()[0]);
    Assertions.assertEquals("testUrl", result.getVcsUrl());
    Assertions.assertEquals(new Date(123L), result.getVcsStart());
    Assertions.assertEquals("master", result.getDefaultBranch());
    Assertions.assertEquals("workdir", result.getWorkdirName());
    Assertions.assertNull(result.getId());
  }

  @Test
  void testMapGraphObject() {
    ProjectEntity testProject =
        new ProjectEntity()
            .setId(1L)
            .setName("testName")
            .setVcsStart(new Date(123L))
            .setVcsPassword(new byte[] {1})
            .setVcsUsername("testUsername")
            .setWorkdirName("workdir")
            .setDefaultBranch("master")
            .setVcsUrl("testUrl");

    Project result = projectMapper.mapGraphObject(testProject);
    Assertions.assertEquals("testName", result.getName());
    Assertions.assertEquals("testUsername", result.getVcsUsername());
    Assertions.assertEquals(1, result.getVcsPassword()[0]);
    Assertions.assertEquals("testUrl", result.getVcsUrl());
    Assertions.assertEquals(new Date(123L), result.getVcsStart());
    Assertions.assertEquals("workdir", result.getWorkdirName());
    Assertions.assertEquals("master", result.getDefaultBranch());
    Assertions.assertEquals(1L, result.getId());
  }
}
