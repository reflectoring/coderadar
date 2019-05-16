package io.reflectoring.coderadar.core.analyzer.domain;

import lombok.Data;

/**
 * This Entity exists merely to be able to do bulk changes via HQL. When possible the actual
 * ManyToMany field in CommitToFileAssociation.modules should be used.
 */
@Data
public class ModuleAssociation {

  private ModuleAssociationId id;
}
