import { Component } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-addtocart-dialog',
  templateUrl: './addtocart-dialog.component.html',
  styleUrls: ['./addtocart-dialog.component.css']
})
export class AddtocartDialogComponent {

  constructor(private dialogRef: MatDialogRef<AddtocartDialogComponent>) { }

  closeDialog(): void {
    this.dialogRef.close();
  }
}
