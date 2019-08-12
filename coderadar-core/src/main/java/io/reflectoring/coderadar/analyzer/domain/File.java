package io.reflectoring.coderadar.analyzer.domain;

import lombok.Data;
import lombok.ToString;

import java.util.LinkedList;
import java.util.List;

/** Represents a file in a VCS repository. */
@Data
public class File {
  private Long id;
  private String path;

  private List<MetricValue> metricValues = new LinkedList<>();

  @ToString.Exclude
  private List<FileToCommitRelationship> commits = new LinkedList<>();
}
