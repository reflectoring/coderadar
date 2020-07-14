package io.reflectoring.coderadar.vcs.adapter;

import io.reflectoring.coderadar.projectadministration.domain.Branch;
import io.reflectoring.coderadar.vcs.port.driven.GetAvailableBranchesPort;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.springframework.stereotype.Service;

@Service
public class GetAvailableBranchesAdapter implements GetAvailableBranchesPort {

  @Override
  public List<Branch> getAvailableBranches(String repositoryRoot) {
    try (Git git = Git.open(new File(repositoryRoot))) {
      return getBranches(git);
    } catch (Exception e) {
      throw new IllegalStateException(
          String.format("Error accessing git repository at %s", repositoryRoot), e);
    }
  }

  private List<Branch> getBranches(Git git) throws GitAPIException {
    List<Branch> result = new ArrayList<>();
    List<Ref> refs = new ArrayList<>();
    refs.addAll(git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call());
    refs.addAll(git.tagList().call());
    for (Ref ref : refs) {
      String[] branchName = ref.getName().split("/");
      int length = branchName.length;
      String truncatedName = branchName[length - 1];
      if (result.stream().noneMatch(branch -> branch.getName().equals(truncatedName))) {
        if (branchName[length - 2].equals("tags")) { // TODO: is this always correct?
          result.add(
              new Branch()
                  .setName(truncatedName)
                  .setCommitHash(ref.getObjectId().getName())
                  .setTag(true));
        } else {
          result.add(
              new Branch()
                  .setName(truncatedName)
                  .setCommitHash(ref.getObjectId().getName())
                  .setTag(false));
        }
      }
    }
    return result;
  }
}
