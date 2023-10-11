import { Component, OnInit } from '@angular/core';
import { CyclesService } from '../service/cycles.service';
import { CartItem } from '../models/CartItems';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmationDialogComponent } from '../confirmation-dialog/confirmation-dialog.component';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit {
  cartItems: CartItem[] = [];
  filteredData: any;
  totalPrice: number = 0;

  constructor(
    private cyclesService: CyclesService,
    private router: Router,
    private dialog: MatDialog
  ) { }

  ngOnInit(): void {
    this.getCartItems();
  }

  getCartItems(): void {
    this.cyclesService.getCartItems().subscribe((cartItems) => {
      this.cartItems = cartItems;
      this.filteredData = cartItems.filter(item => !item.ordered)
    });
  }

  quantityOptions: number[] = Array.from({ length:20 }, (_, i) => i + 1);

  calculateTotalPrice(): number {
    this.totalPrice = 0;
    for (const item of this.cartItems) {
      if (item.ordered === false) {
        this.totalPrice += item.price * item.quantity;
      }

    }
    return this.totalPrice;
  }

  removeFromCart(cycleId: number) {
    this.cyclesService.removeFromCart(cycleId)
      .subscribe(response => {
        this.getCartItems();
      });
  }

  updateCartItemQuantity(cycleId: number, newQuantity: number) {
    this.cyclesService.updateCartItemQuantity(cycleId, newQuantity).subscribe(
      (response) => { }
    );
  }

  goToCheckout(): void {
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      width: '400px',
      height: '200px',
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.router.navigate(['/cycles']);
      }
    });
  }
}
