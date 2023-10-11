import { Component, OnInit } from '@angular/core';
import { CyclesService } from '../service/cycles.service';
import { Cycle } from '../models/cycle';
import { AuthService } from '../service/auth.service';
import { MatDialog } from '@angular/material/dialog';
import { AddtocartDialogComponent } from '../addtocart-dialog/addtocart-dialog.component';
import { faCartShopping, faArrowUp, faArrowDown, faTachographDigital } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-cycles',
  templateUrl: './cycles.component.html',
  styleUrls: ['./cycles.component.css']
})
export class CyclesComponent implements OnInit {
  cycles: Cycle[] = [];
  quantityValues: number[] = [];
  faCartShopping = faCartShopping;
  faArrowUp = faArrowUp;
  faArrowDown = faArrowDown;
  quantityOptions: number[] = Array.from({ length: 10 }, (_, i) => i + 1);

  constructor(private cyclesService: CyclesService, public authService: AuthService, private dialog: MatDialog) {
  }

  ngOnInit(): void {
    this.getCycles();
  }

  getCycles(): void {
    this.cyclesService.getCycles()
      .subscribe(cycles => {
        this.cycles = cycles.map(cycle => ({ ...cycle, selectedQuantity: 1 }));
      });
  }

  parseQuantity(qtyString: string): number {
    return parseInt(qtyString, 10);
  }

  addToCart(id: number, quantity: number) {
    this.cyclesService.addToCart(id, quantity)
      .subscribe(cycle => {
        this.openAddToCartDialog();
        console.log('Added to cart:', cycle);
        // Close the dialog after 1 second
        setTimeout(() => {
          this.dialog.closeAll();
        }, 1000);
      });
  }

  openAddToCartDialog() {
    this.dialog.open(AddtocartDialogComponent, {
      width: '200px',
      disableClose: true,
    });
  }

  restock(id: number, quantity: number): void {
    console.log(id, quantity);
    this.cyclesService.restockCycle(id, quantity)
      .subscribe(cycle => {
        // Update the quantity for the specific cycle
        const index = this.cycles.findIndex(c => c.id === cycle.id);
        if (index !== -1) {
          // Assuming there's a property called 'quantity' in your Cycle model
          this.cycles[index].quantity = cycle.quantity;
        }
      });
  }
}
