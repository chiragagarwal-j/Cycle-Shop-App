import { Component } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { CyclesService } from '../service/cycles.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-confirmation-dialog',
  templateUrl: './confirmation-dialog.component.html',
  styleUrls: ['./confirmation-dialog.component.css']
})
export class ConfirmationDialogComponent {

  constructor(
    public dialogRef: MatDialogRef<ConfirmationDialogComponent>,
    private router: Router,
    private cyclesService: CyclesService
  ) { }

  confirm(): void {
    this.cyclesService.confirmOrder().subscribe(
      () => {
        this.dialogRef.close(true);
        this.router.navigate(['/cycles']);
      }
    );
  }

  cancel(): void {
    this.dialogRef.close(false);
  }
}
