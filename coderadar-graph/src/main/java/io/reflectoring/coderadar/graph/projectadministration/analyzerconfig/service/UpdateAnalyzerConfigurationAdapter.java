package io.reflectoring.coderadar.graph.projectadministration.analyzerconfig.service;

import io.reflectoring.coderadar.analyzer.domain.AnalyzerConfiguration;
import io.reflectoring.coderadar.graph.analyzer.domain.AnalyzerConfigurationEntity;
import io.reflectoring.coderadar.graph.projectadministration.analyzerconfig.AnalyzerConfigurationMapper;
import io.reflectoring.coderadar.graph.projectadministration.analyzerconfig.repository.AnalyzerConfigurationRepository;
import io.reflectoring.coderadar.projectadministration.AnalyzerConfigurationNotFoundException;
import io.reflectoring.coderadar.projectadministration.port.driven.analyzerconfig.UpdateAnalyzerConfigurationPort;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UpdateAnalyzerConfigurationAdapter implements UpdateAnalyzerConfigurationPort {
  private final AnalyzerConfigurationRepository analyzerConfigurationRepository;
  private final AnalyzerConfigurationMapper analyzerConfigurationMapper =
      new AnalyzerConfigurationMapper();

  public UpdateAnalyzerConfigurationAdapter(
      AnalyzerConfigurationRepository analyzerConfigurationRepository) {
    this.analyzerConfigurationRepository = analyzerConfigurationRepository;
  }

  @Override
  public void update(AnalyzerConfiguration configuration) {
    Optional<AnalyzerConfigurationEntity> persistedAnalyzerConfiguration =
        analyzerConfigurationRepository.findById(configuration.getId());

    if (persistedAnalyzerConfiguration.isPresent()) {
      persistedAnalyzerConfiguration.get().setAnalyzerName(configuration.getAnalyzerName());
      persistedAnalyzerConfiguration.get().setEnabled(configuration.getEnabled());
      if (configuration.getAnalyzerConfigurationFile() != null) {
        persistedAnalyzerConfiguration
            .get()
            .setAnalyzerConfigurationFile(
                analyzerConfigurationMapper.mapConfigurationFileDomainObject(
                    configuration.getAnalyzerConfigurationFile()));
      }
      analyzerConfigurationRepository.save(persistedAnalyzerConfiguration.get());
    } else {
      throw new AnalyzerConfigurationNotFoundException(configuration.getId());
    }
  }
}
