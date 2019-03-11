import {Component} from '@angular/core';
import {Project} from '../project';
import {ProjectService} from '../project.service';
import {Router} from '@angular/router';
import {UserService} from '../user.service';

@Component({
  selector: 'app-add-project',
  templateUrl: './add-project.component.html',
  styleUrls: ['./add-project.component.css']
})
export class AddProjectComponent {

  project: Project;

  incorrectURL = false;
  projectExists = false;
  nameEmpty = false;

  constructor(private router: Router, private userService: UserService, private projectService: ProjectService) {
    this.project = new Project();
    this.project.name = '';
    this.project.vcsUrl = '';
  }

  /**
   * Validates user input and calls ProjectService.addProject().
   * Handles server errors.
   * If access is denied (403) sends the refresh token and tries to submit again.
   */
  submitForm() {
    if (this.validateInput()) {
      return;
    }

    this.projectService.addProject(this.project)
      .then(response => {
        this.project.id = response.body.id;
        this.router.navigate(['/project-configure', this.project.id]);
      })
      .catch(error => {
        if (error.status && error.status === 403) { // If access is denied
            this.userService.refresh()
              .then(() => this.submitForm());
        } else if (error.status && error.status === 400) {   // If there is a field error
            if (error.error && error.error.errorMessage === 'Validation Error') {
              error.error.fieldErrors.forEach(field => {  // Check which field
                if (field.field === 'vcsUrl') {
                  this.incorrectURL = true;
                }
              });
            }
        } else if (error.status === 500 && // If we get a general server error, then a project with this name exists already
            error.error.errorMessage === 'Project with name \'' + this.project.name + '\' already exists. Please choose another name.') {
              this.projectExists = true;
        }
    });
  }

  /**
   * Checks for empty form fields.
   */
  private validateInput(): boolean {
    this.incorrectURL = false;
    this.projectExists = false;
    this.nameEmpty = false;

    this.incorrectURL = this.project.vcsUrl.trim().length === 0;
    this.nameEmpty = this.project.name.trim().length === 0;

    return this.nameEmpty || this.incorrectURL;
  }
}
