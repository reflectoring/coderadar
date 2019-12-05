package io.reflectoring.coderadar.projectadministration.project;

import static org.assertj.core.api.Assertions.assertThat;

import io.reflectoring.coderadar.projectadministration.port.driven.project.ListProjectsPort;
import io.reflectoring.coderadar.projectadministration.port.driver.project.get.GetProjectResponse;
import io.reflectoring.coderadar.projectadministration.service.project.ListProjectsService;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListProjectsServiceTest {

  @Mock private ListProjectsPort listProjectsPort;

  private ListProjectsService testSubject;

  @BeforeEach
  void setUp() {
    this.testSubject = new ListProjectsService(listProjectsPort);
  }

  @Test
  void returnsTwoProjects() {
    // given
    Date startDate = new Date();
    Date endDate = new Date();

    GetProjectResponse expectedResponse1 =
        new GetProjectResponse()
            .setId(1L)
            .setName("project 1")
            .setVcsUrl("http://github.com")
            .setVcsUsername("username1")
            .setVcsPassword("password1")
            .setVcsOnline(true)
            .setStartDate(startDate)
            .setEndDate(endDate);
    GetProjectResponse expectedResponse2 =
        new GetProjectResponse()
            .setId(2L)
            .setName("project 2")
            .setVcsUrl("http://bitbucket.org")
            .setVcsUsername("username2")
            .setVcsPassword("password2")
            .setVcsOnline(false)
            .setStartDate(startDate)
            .setEndDate(endDate);

    Mockito.when(listProjectsPort.getProjectResponses())
        .thenReturn(Arrays.asList(expectedResponse1, expectedResponse2));

    // when
    List<GetProjectResponse> actualResponses = testSubject.listProjects();

    // then
    assertThat(actualResponses).containsExactly(expectedResponse1, expectedResponse2);
  }
}
