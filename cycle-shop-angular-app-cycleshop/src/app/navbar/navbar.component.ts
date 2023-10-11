import { Component } from '@angular/core';
import { AuthService } from '../service/auth.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {
  title = "cycles"

  constructor(public authService: AuthService) {}

  logout(): void {
    this.authService.logout();
  }

}
