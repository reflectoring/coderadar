package io.reflectoring.coderadar.graph.projectadministration.project.service;

import io.reflectoring.coderadar.graph.projectadministration.domain.ProjectEntity;
import io.reflectoring.coderadar.graph.projectadministration.project.repository.ProjectRepository;
import io.reflectoring.coderadar.projectadministration.ProjectNotFoundException;
import io.reflectoring.coderadar.projectadministration.domain.Project;
import io.reflectoring.coderadar.projectadministration.port.driven.project.UpdateProjectPort;
import org.springframework.stereotype.Service;

@Service
public class UpdateProjectAdapter implements UpdateProjectPort {
  private final ProjectRepository projectRepository;

  public UpdateProjectAdapter(ProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
  }

  @Override
  public void update(Project project) {
    ProjectEntity projectEntity =
        projectRepository
            .findProjectById(project.getId())
            .orElseThrow(() -> new ProjectNotFoundException(project.getId()));

    projectEntity.setName(project.getName());
    projectEntity.setVcsEnd(project.getVcsEnd());
    projectEntity.setVcsStart(project.getVcsStart());
    projectEntity.setVcsOnline(project.isVcsOnline());
    projectEntity.setVcsUsername(project.getVcsUsername());
    projectEntity.setVcsPassword(project.getVcsPassword());
    projectEntity.setVcsUrl(project.getVcsUrl());
    projectEntity.setWorkdirName(project.getWorkdirName());

    projectRepository.save(projectEntity);
  }

  @Override
  public void deleteProjectFilesCommitsAndMetrics(Long projectId) {
    if (!projectRepository.existsById(projectId)) {
      throw new ProjectNotFoundException(projectId);
    }
    projectRepository.deleteProjectFindings(projectId);
    projectRepository.deleteProjectMetrics(projectId);
    projectRepository.deleteProjectFilesAndModules(projectId);
    projectRepository.deleteProjectCommits(projectId);
  }
}
