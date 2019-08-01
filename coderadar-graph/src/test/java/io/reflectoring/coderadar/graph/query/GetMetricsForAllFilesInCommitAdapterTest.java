package io.reflectoring.coderadar.graph.query;

import static org.mockito.Mockito.mock;

import io.reflectoring.coderadar.graph.query.repository.GetMetricsForAllFilesInCommitRepository;
import io.reflectoring.coderadar.graph.query.service.GetMetricsForAllFilesInCommitAdapter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Get metrics for all files in commit")
class GetMetricsForAllFilesInCommitAdapterTest {
  private GetMetricsForAllFilesInCommitRepository getMetricsForAllFilesInCommitRepository =
      mock(GetMetricsForAllFilesInCommitRepository.class);
  private GetMetricsForAllFilesInCommitAdapter getMetricsForAllFilesInCommitAdapter;

  @Test
  @DisplayName("Should return list of GroupedMetricValueDTO when passing a valid argument")
  void shouldReturnListOfGroupedMetricValueDTOWhenPassingAValidArgument() {
    getMetricsForAllFilesInCommitAdapter = new GetMetricsForAllFilesInCommitAdapter(getMetricValuesOfCommitRepository, getProjectRepository, listModulesOfProjectRepository, createModuleRepository);

    /*    GetMetricsForAllFilesInCommitCommand command =
        new GetMetricsForAllFilesInCommitCommand("1A", new LinkedList<>());
    List<GroupedMetricValueDTO> returnedList = getMetricsForAllFilesInCommitAdapter.get(command);
    Assertions.assertThat(returnedList).isNotNull();*/
  }
}
