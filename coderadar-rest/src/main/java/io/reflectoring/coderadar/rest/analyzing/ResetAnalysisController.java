package io.reflectoring.coderadar.rest.analyzing;

import io.reflectoring.coderadar.analyzer.port.driver.ResetAnalysisUseCase;
import io.reflectoring.coderadar.rest.AbstractBaseController;
import io.reflectoring.coderadar.useradministration.service.security.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ResetAnalysisController implements AbstractBaseController {
  private final ResetAnalysisUseCase resetAnalysisUseCase;
  private final AuthenticationService authenticationService;

  @PostMapping(path = "projects/{projectId}/analyze/reset")
  public ResponseEntity<HttpStatus> resetAnalysis(@PathVariable("projectId") long projectId) {
    authenticationService.authenticateAdmin(projectId);
    resetAnalysisUseCase.resetAnalysis(projectId);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
